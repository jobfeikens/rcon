package nl.vv32.rcon;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

class PacketWriter {

    final private WritableByteChannel writableChannel;

    public PacketWriter(final WritableByteChannel channel) {
        this.writableChannel = channel;
    }

    public int write(final Packet packet) throws IOException {
        final int length = 14 + packet.payload.length();

        if (length > 1460) {
            throw new IllegalStateException("Packet size too big");
        }

        final ByteBuffer buffer = ByteBuffer.allocate(14 + packet.payload.length());
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(length - 4);
        buffer.putInt(packet.requestId);
        buffer.putInt(packet.type.id);

        buffer.put(StandardCharsets.US_ASCII.encode(packet.payload));
        buffer.put((byte) 0x00);
        buffer.put((byte) 0x00);

        buffer.flip();

        return writableChannel.write(buffer);
    }
}