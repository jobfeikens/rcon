package nl.vv32.rcon;

import nl.vv32.rcon.util.TestChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class RconTests {

    @Test
    void authenticate() throws IOException {
        ByteChannel channel = new TestChannel().withPassword("password");
        Rcon rcon = new Rcon(channel);
        assertTrue(rcon.authenticate("password"));
    }

    @Test
    void authenticateWithWrongPassword() throws IOException {
        ByteChannel channel = new TestChannel().withPassword("password");
        Rcon rcon = new Rcon(channel);
        assertFalse(rcon.authenticate("wrongPassword"));
    }
}
