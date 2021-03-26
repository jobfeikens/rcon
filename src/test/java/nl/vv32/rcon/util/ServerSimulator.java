package nl.vv32.rcon.util;

import nl.vv32.rcon.Packet;
import nl.vv32.rcon.PacketCodec;
import nl.vv32.rcon.PacketType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;

public class ServerSimulator implements ByteChannel {

    private boolean isOpen = false;

    private String password = "";
    private boolean returnWrongType = false;
    private boolean returnWrongId = false;
    private boolean returnEOF = false;

    final private ByteBuffer buffer = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);

    public ServerSimulator setPassword(String password) {
        this.password = password;
        return this;
    }

    public ServerSimulator returnWrongType() {
        returnWrongType = true;
        return this;
    }

    public ServerSimulator returnWrongId() {
        returnWrongId = true;
        return this;
    }

    public ServerSimulator returnEOF() {
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
        PacketType returnType;
        String payload = "";


        switch (request.type) {
            case LOGIN:
                if (!request.payload.equals(password)) {
                    requestId = -1;
                }
                returnType = PacketType.COMMAND;
                break;
            case COMMAND:
                returnType = PacketType.COMMAND_RESPONSE;
                payload = request.payload;
                break;
            default:
                returnType = PacketType.UNKNOWN;
                break;
        }

        if (returnWrongType) {
            returnType = PacketType.UNKNOWN;
        }
        if (returnWrongId) {
            requestId++;
        }

        return new Packet(requestId, returnType, payload);
    }
}
