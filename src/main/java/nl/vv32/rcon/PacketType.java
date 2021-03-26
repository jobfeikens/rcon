package nl.vv32.rcon;

public enum PacketType {

    COMMAND_RESPONSE(0),
    COMMAND(2),
    LOGIN(3);

    final public int id;

    PacketType(final int id) {
        this.id = id;
    }

    public static PacketType fromId(final int id) {

        switch (id) {
            case 0: return COMMAND_RESPONSE;
            case 2: return COMMAND;
            case 3: return LOGIN;
            default: throw new IllegalArgumentException();
        }
    }
}
