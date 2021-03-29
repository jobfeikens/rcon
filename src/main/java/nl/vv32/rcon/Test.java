package nl.vv32.rcon;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Test {

    public static void main(String[] args) throws IOException {

        try(Rcon rcon = Rcon.open(new InetSocketAddress("vv32.nl", 25575))) {
            if (rcon.authenticate("f4LnVuBVYer3oVHz")) {
                System.out.println(rcon.sendCommand("list"));
            }
        }
    }
}
