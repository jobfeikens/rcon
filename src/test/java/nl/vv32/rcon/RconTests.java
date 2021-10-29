package nl.vv32.rcon;

import nl.vv32.rcon.util.RconServerSimulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.UnresolvedAddressException;

public class RconTests {

    @Test
    void connect() throws IOException {
        assertThrows(UnresolvedAddressException.class, () -> Rcon.open(new InetSocketAddress("thishostsdoesntexist", 65535)));
        assertThrows(UnresolvedAddressException.class, () -> Rcon.open("thishostdoesntexist", 65535));
    }

    @Test
    void setup() throws IOException {
        assertDoesNotThrow(new RconBuilder().withChannel(new RconServerSimulator())::build);
    }

    @Test
    void close() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator()).build();
        rcon.close();
    }

    @Test
    void authenticate() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        assertTrue(rcon.authenticate("password"));
    }

    @Test
    void authenticateTwice() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        assertTrue(rcon.authenticate("password"));
        assertTrue(rcon.authenticate("password"));
    }

    @Test
    void authenticateWithWrongPassword() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        assertFalse(rcon.authenticate("wrongPassword"));
    }

    @Test
    void authenticateWrongReturnType() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password").returnWrongType()).build();
        assertThrows(IOException.class, () -> rcon.authenticate("password"));
    }

    @Test
    void authenticateWrongId() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password").returnWrongId()).build();
        assertThrows(IOException.class, () -> rcon.authenticate("password"));
    }

    @Test
    void authenticateAfterClose() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator()).build();
        rcon.close();
        assertThrows(IllegalStateException.class, () -> rcon.authenticate("password"));
    }

    // See issue #3
    @Test
    void authenticateCsgo() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password").doCsgoAuthentication()).build();
        assertTrue(rcon.authenticate("password"));
    }

    @Test
    void authenticateCsgoWrongPassword() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password").doCsgoAuthentication()).build();
        assertFalse(rcon.authenticate("wrongPassword"));
    }

    @Test
    void sendCommand() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        rcon.authenticate("password");
        assertEquals("command", rcon.sendCommand("command"));
    }

    @Test
    void sendManyCommands() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        rcon.authenticate("password");

        for (int i = 0; i < 1000; i++) {
            assertEquals("command", rcon.sendCommand("command"));
        }
    }

    @Test
    void sendCommandWithoutAuthenticating() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator()).build();
        assertThrows(IOException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void sendCommandAfterClose() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        rcon.authenticate("password");
        rcon.close();
        assertThrows(IllegalStateException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void sendCommandWrongReturnType() throws IOException {
        RconServerSimulator simulator = new RconServerSimulator().setPassword("password");
        Rcon rcon = new RconBuilder().withChannel(simulator).build();
        rcon.authenticate("password");
        simulator.returnWrongType();
        assertThrows(IOException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void sendCommandWrongId() throws IOException {
        RconServerSimulator simulator = new RconServerSimulator().setPassword("password");
        Rcon rcon = new RconBuilder().withChannel(simulator).build();
        rcon.authenticate("password");
        simulator.returnWrongId();
        assertThrows(IOException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void testMaxPayloadSize() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password")).build();
        rcon.authenticate("password");
        assertDoesNotThrow(() -> rcon.sendCommand(new String(new char[1446])));
        assertThrows(IllegalArgumentException.class, () -> rcon.sendCommand(new String(new char[1447])));
    }

    @Test
    void testEOF() throws IOException {
        Rcon rcon = new RconBuilder().withChannel(new RconServerSimulator().setPassword("password").returnEOF()).build();
        assertThrows(EOFException.class, () -> rcon.authenticate("password"));
    }
}
