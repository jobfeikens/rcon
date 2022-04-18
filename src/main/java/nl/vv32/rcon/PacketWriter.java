package nl.vv32.rcon;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class PacketWriter {

    final private Destination destination;
    final private PacketCodec codec;
    final ByteBuffer buffer;

    PacketWriter(final Destination destination, final int bufferCapacity,
                 final PacketCodec codec) {
        this.destination = destination;
        this.codec = codec;
        buffer = ByteBuffer.allocate(bufferCapacity)
                .order(ByteOrder.LITTLE_ENDIAN);
    }

    int write(final Packet packet) throws IOException {
        if (packet.payload.length() > 1446) {
            throw new IllegalArgumentException("Packet payload too big");
        }

        buffer.clear();
        buffer.position(Integer.BYTES);
        codec.encode(packet, buffer);
        buffer.putInt(0, buffer.position() - Integer.BYTES);
        buffer.flip();
        return destination.write(buffer);
    }

    @FunctionalInterface
    interface Destination {

        int write(ByteBuffer source) throws IOException;
    }
}