package nl.vv32.rcon;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class PacketReader {

    final private Source source;
    final private PacketCodec codec;
    final private ByteBuffer buffer; //4 KiB

    public PacketReader(final Source source, final int bufferCapacity,
                        final PacketCodec codec) {
        this.source = source;
        this.codec = codec;
        buffer = ByteBuffer.allocate(bufferCapacity)
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    public Packet read() throws IOException {

        //Read packet length
        readUntilAvailable(Integer.BYTES);
        buffer.flip();
        final int length = buffer.getInt();
        buffer.compact();

        //Read packet
        readUntilAvailable(length);
        buffer.flip();
        final Packet packet = codec.decode(buffer, length);
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
