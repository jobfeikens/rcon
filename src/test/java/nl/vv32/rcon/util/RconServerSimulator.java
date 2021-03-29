package nl.vv32.rcon.util;

import nl.vv32.rcon.Packet;
import nl.vv32.rcon.PacketCodec;
import nl.vv32.rcon.PacketType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;

public class RconServerSimulator implements ByteChannel {

    private boolean isOpen = false;
    private boolean isAuthenticated = false;
    private String password = "";
    private boolean returnWrongType = false;
    private boolean returnWrongId = false;
    private boolean returnEOF = false;

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
        final Packet response = generateResponse(request);

        destination.putInt(10 + response.payload.length());
        PacketCodec.encode(response, destination);
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

    private Packet generateResponse(final Packet request) {
        int requestId = request.requestId;
        int returnType;
        String payload = "";


        switch (request.type) {
            case PacketType.SERVERDATA_AUTH:
                if (!request.payload.equals(password)) {
                    requestId = -1;
                }
                returnType = PacketType.SERVERDATA_AUTH_RESPONSE;
                break;
            case PacketType.SERVERDATA_EXECCOMMAND:
                returnType = PacketType.SERVERDATA_RESPONSE_VALUE;
                payload = request.payload;
                if (!isAuthenticated) {
                    requestId = -1;
                    payload = "Not authenticated";
                }
                break;
            default:
                returnType = 0xff;
                break;
        }

        if (returnWrongType) {
            returnType = 0xef;
        }
        if (returnWrongId) {
            requestId++;
        }

        return new Packet(requestId, returnType, payload);
    }
}
