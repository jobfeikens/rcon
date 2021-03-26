package nl.vv32.rcon;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketCodec {

    public static void encode(final Packet packet, final ByteBuffer destination) {

        destination.putInt(packet.requestId);
        destination.putInt(packet.type.id);
        destination.put(StandardCharsets.US_ASCII.encode(packet.payload));
        destination.put((byte) 0x00);
        destination.put((byte) 0x00);
    }

    public static Packet decode(final ByteBuffer source, final int length) {

        int startPos = source.position();

        final int requestId = source.getInt();
        final PacketType type = PacketType.fromId(source.getInt());

        int limit = source.limit();
        source.limit(startPos + length - 2);

        final String payload = StandardCharsets.US_ASCII.decode(source).toString();
        source.limit(limit);

        source.get();
        source.get();

        return new Packet(requestId, type, payload);
    }
}
