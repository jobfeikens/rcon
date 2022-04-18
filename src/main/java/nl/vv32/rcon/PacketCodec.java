package nl.vv32.rcon;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class PacketCodec {

    final private Charset charset;

    public PacketCodec(final Charset charset) {
        this.charset = charset;
    }

    public void encode(final Packet packet, final ByteBuffer destination) {

        destination.putInt(packet.requestId);
        destination.putInt(packet.type);
        destination.put(charset.encode(packet.payload));
        destination.put((byte) 0x00);
        destination.put((byte) 0x00);
    }

    public Packet decode(final ByteBuffer source, final int length) {
        int requestId = source.getInt();
        int packetType = source.getInt();

        int limit = source.limit();
        source.limit(source.position() + length - 10);
        String payload = charset.decode(source).toString();
        source.limit(limit);

        source.get(); // String termination
        source.get(); // Packet termination

        return new Packet(requestId, packetType, payload);
    }
}
