package nl.vv32.rcon;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

class PacketWriter {

    final private Destination destination;
    final ByteBuffer buffer;

    public PacketWriter(final Destination destination,
                        final int bufferCapacity) {
        this.destination = destination;

        buffer = ByteBuffer.allocate(bufferCapacity)
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    public int write(final Packet packet) throws IOException {
        if (packet.payload.length() > 1446) {
            throw new IllegalArgumentException("Packet payload too big");
        }

        buffer.clear();

        buffer.putInt(10 + packet.payload.length());
        PacketCodec.encode(packet, buffer);

        buffer.flip();
        return destination.write(buffer);
    }

    @FunctionalInterface
    public interface Destination {

        int write(ByteBuffer source) throws IOException;
    }
}