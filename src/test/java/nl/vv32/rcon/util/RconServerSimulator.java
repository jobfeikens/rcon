package nl.vv32.rcon.util;

import nl.vv32.rcon.Packet;
import nl.vv32.rcon.PacketCodec;
import nl.vv32.rcon.PacketType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.util.function.Consumer;

public class RconServerSimulator implements ByteChannel {

    private boolean isOpen = false;
    private boolean isAuthenticated = false;
    private String password = "";
    private boolean returnWrongType = false;
    private boolean returnWrongId = false;
    private boolean returnEOF = false;
    private boolean doCsgoAuthentication = false;

    final private ByteBuffer buffer = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);

    public RconServerSimulator setPassword(String password) {
        this.password = password;
        return this;
    }

    public RconServerSimulator returnWrongType() {
        returnWrongType = true;
        return this;
    }

    public RconServerSimulator returnWrongId() {
        returnWrongId = true;
        return this;
    }

    public RconServerSimulator returnEOF() {
        returnEOF = true;
        return this;
    }

    public RconServerSimulator doCsgoAuthentication() {
        doCsgoAuthentication = true;
        return this;
    }

    @Override
    public int read(final ByteBuffer destination) {

        if (returnEOF) {
            return -1;
        }

        buffer.flip();

        final int requestLength = buffer.getInt();

        final Packet request = PacketCodec.decode(buffer, requestLength);
        buffer.compact();

        if (request.type == PacketType.SERVERDATA_AUTH) {
            isAuthenticated = request.payload.equals(this.password);
        }

        final int startPosition = destination.position();

        generateResponses(request, response -> {
            final Packet mutatedResponse = mutateResponse(response);

            destination.putInt(10 + mutatedResponse.payload.length());
            PacketCodec.encode(mutatedResponse, destination);
        });
        return destination.position() - startPosition;
    }

    @Override
    public int write(final ByteBuffer source) {
        final int startPosition = buffer.position();
        buffer.put(source);
        return buffer.position() - startPosition;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() {

    }

    private void generateResponses(final Packet request, Consumer<Packet> responseConsumer) {

        switch (request.type) {
            case PacketType.SERVERDATA_AUTH:

                if (doCsgoAuthentication) {
                    responseConsumer.accept(new Packet(request.requestId, PacketType.SERVERDATA_RESPONSE_VALUE));
                }

                responseConsumer.accept(new Packet(
                        request.payload.equals(password) ? request.requestId : -1,
                        PacketType.SERVERDATA_AUTH_RESPONSE));
                break;

            case PacketType.SERVERDATA_EXECCOMMAND:

                responseConsumer.accept(new Packet(
                        isAuthenticated ? request.requestId : -1,
                        PacketType.SERVERDATA_RESPONSE_VALUE,
                        isAuthenticated ? request.payload : "Not authenticated"));

                break;
            default:
                responseConsumer.accept(new Packet(request.requestId, 0xff));
                break;
        }
    }

    private Packet mutateResponse(Packet response) {
        return new Packet(
                response.requestId + (returnWrongId ? 1 : 0),
                returnWrongType ? 0xef : response.type,
                response.payload
        );
    }
}
