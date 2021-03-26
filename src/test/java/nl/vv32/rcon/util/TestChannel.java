package nl.vv32.rcon.util;

import nl.vv32.rcon.Packet;
import nl.vv32.rcon.PacketType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;

public class TestChannel implements ByteChannel {

    private boolean isOpen = false;

    private String password;

    final private ByteBuffer writeBuffer = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);
    final private ByteBuffer readBuffer = ByteBuffer.allocate(8192).order(ByteOrder.LITTLE_ENDIAN);

    public TestChannel withPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public int read(ByteBuffer destination) throws IOException {
        final int currentPosition = readBuffer.position();
        readBuffer.flip();
        destination.put(readBuffer);
        readBuffer.compact();
        return readBuffer.position() - currentPosition;
    }

    @Override
    public int write(ByteBuffer source) throws IOException {
        final int currentPosition = writeBuffer.position();
        writeBuffer.put(source);
        parseWriteBuffer();
        return writeBuffer.position() - currentPosition;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() throws IOException {

    }

    private void parseWriteBuffer() {
        writeBuffer.flip();

        try {
            do {
                if (writeBuffer.remaining() < 14) {
                    break;
                }
                int length = writeBuffer.getInt();
                int requestId = writeBuffer.getInt();
                PacketType type = PacketType.fromId(writeBuffer.getInt());

                int limit = writeBuffer.limit();

                writeBuffer.limit(writeBuffer.position() + length - 10);

                String payload = StandardCharsets.US_ASCII.decode(writeBuffer).toString();
                writeBuffer.limit(limit);

                writeBuffer.get();
                writeBuffer.get();

                generateResponse(requestId, type, payload);

            } while (writeBuffer.hasRemaining());
        }
        finally {
            writeBuffer.compact();
        }
    }

    private void generateResponse(int requestId, PacketType type, String payload) {

        if (type == PacketType.LOGIN) {
            System.out.println(payload);
            System.out.println(password);

            readBuffer.putInt(10);
            readBuffer.putInt(payload.equals(password) ? requestId : -1);
            readBuffer.putInt(PacketType.COMMAND.id);
            readBuffer.put((byte) 0x00);
            readBuffer.put((byte) 0x00);
        }
    }
}
