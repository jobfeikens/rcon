package nl.vv32.rcon;

public class PacketType {

    private PacketType() {}
    
    final public static int SERVERDATA_RESPONSE_VALUE = 0;
    final public static int SERVERDATA_EXECCOMMAND = 2;
    final public static int SERVERDATA_AUTH_RESPONSE = 2;
    final public static int SERVERDATA_AUTH = 3;
}
