package nl.vv32.rcon;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class PacketCodec {

    final private static Charset CHARSET = StandardCharsets.US_ASCII;

    public static void encode(final Packet packet, final ByteBuffer destination) {

        destination.putInt(packet.requestId);
        destination.putInt(packet.type);
        destination.put(CHARSET.encode(packet.payload));
        destination.put((byte) 0x00);
        destination.put((byte) 0x00);
    }

    public static Packet decode(final ByteBuffer source, final int length) {
        int requestId = source.getInt();
        int packetType = source.getInt();

        int limit = source.limit();
        source.limit(source.position() + length - 10);
        String payload = CHARSET.decode(source).toString();
        source.limit(limit);

        source.get(); // String termination
        source.get(); // Packet termination

        return new Packet(requestId, packetType, payload);
    }
}
