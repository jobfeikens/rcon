package nl.vv32.rcon;

public class Packet {

    final public int requestId;
    final public PacketType type;
    final public String payload;

    public Packet(final int requestId, final PacketType type,
                  final String payload) {
        this.requestId = requestId;
        this.type = type;
        this.payload = payload;
    }

    public boolean isValid() {
        return requestId != -1;
    }
}
  