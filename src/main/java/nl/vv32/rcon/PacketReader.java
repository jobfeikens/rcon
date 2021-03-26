package nl.vv32.rcon;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

class PacketReader {

    final private ReadableByteChannel readableChannel;

    public PacketReader(final ReadableByteChannel channel) {
        this.readableChannel = channel;
    }

    final ByteBuffer buffer = ByteBuffer.allocate(4110)
            .order(ByteOrder.LITTLE_ENDIAN); //4 KiB

    public Packet read() throws IOException {

        //Read packet length
        readUntilAvailable(Integer.BYTES);
        buffer.flip();
        final int length = buffer.getInt();
        buffer.compact();

        //Read packet ID
        readUntilAvailable(Integer.BYTES);
        buffer.flip();
        final int requestId = buffer.getInt();
        buffer.compact();

        //Read type
        readUntilAvailable(Integer.BYTES);
        buffer.flip();
        final int type = buffer.getInt();
        buffer.compact();

        //Read payload
        readUntilAvailable(length - 10);
        buffer.flip();
        int available = buffer.remaining();
        buffer.limit(length-10);
        final String payload = StandardCharsets.US_ASCII.decode(buffer).toString();
        buffer.limit(available);

        //Read padding
        buffer.get(); // payload null-termination
        buffer.get(); // packet padding

        buffer.compact();
        return new Packet(requestId, PacketType.fromId(type), payload);
    }

    private void readUntilAvailable(final int bytesAvailable)
            throws IOException {

        while (buffer.position() < bytesAvailable) {

            if (readableChannel.read(buffer) == -1) {
                throw new EOFException();
            }
        }
    }
}
