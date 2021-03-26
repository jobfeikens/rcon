package nl.vv32.rcon;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

class PacketReader {

    final private Source source;

    public PacketReader(final Source source) {
        this.source = source;
    }

    final ByteBuffer buffer = ByteBuffer.allocate(4110)
            .order(ByteOrder.LITTLE_ENDIAN); //4 KiB

    public Packet read() throws IOException {

        //Read packet length
        readUntilAvailable(Integer.BYTES);
        buffer.flip();
        final int length = buffer.getInt();
        buffer.compact();

        //Read packet
        readUntilAvailable(length);
        buffer.flip();
        final Packet packet = PacketCodec.decode(buffer, length);
        buffer.compact();

        return packet;
    }

    private void readUntilAvailable(final int bytesAvailable)
            throws IOException {

        while (buffer.position() < bytesAvailable) {

            if (source.read(buffer) == -1) {
                throw new EOFException();
            }
        }
    }

    @FunctionalInterface
    public interface Source {

        int read(ByteBuffer destination) throws IOException;
    }
}
