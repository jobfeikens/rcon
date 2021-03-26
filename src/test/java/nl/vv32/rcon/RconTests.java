package nl.vv32.rcon;

import nl.vv32.rcon.util.ServerSimulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.UnresolvedAddressException;

public class RconTests {

    @Test
    void connect() throws IOException {
        assertThrows(UnresolvedAddressException.class, () -> Rcon.open(new InetSocketAddress("host", 65535)));
    }

    @Test
    void authenticate() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        assertTrue(rcon.authenticate("password"));
    }

    @Test
    void authenticateTwice() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        assertTrue(rcon.authenticate("password"));
        assertTrue(rcon.authenticate("password"));
    }

    @Test
    void authenticateWithWrongPassword() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        assertFalse(rcon.authenticate("wrongPassword"));
    }

    @Test
    void authenticateWrongReturnType() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password").returnWrongType());
        assertThrows(IOException.class, () -> rcon.authenticate("password"));
    }

    @Test
    void authenticateWrongId() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password").returnWrongId());
        assertThrows(IOException.class, () -> rcon.authenticate("password"));
    }

    @Test
    void authenticateAfterClose() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator());
        rcon.close();
        assertThrows(IllegalStateException.class, () -> rcon.authenticate("password"));
    }

    @Test
    void sendCommand() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        rcon.authenticate("password");
        assertEquals("command", rcon.sendCommand("command"));
    }

    @Test
    void sendManyCommands() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        rcon.authenticate("password");

        for (int i = 0; i < 1000; i++) {
            assertEquals("command", rcon.sendCommand("command"));
        }
    }

    @Test
    void sendCommandWithoutAuthenticating() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator());
        assertThrows(IllegalStateException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void sendCommandAfterClose() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        rcon.authenticate("password");
        rcon.close();
        assertThrows(IllegalStateException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void sendCommandWrongReturnType() throws IOException {
        ServerSimulator simulator = new ServerSimulator().setPassword("password");
        Rcon rcon = new Rcon(simulator);
        rcon.authenticate("password");
        simulator.returnWrongType();
        assertThrows(IOException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void sendCommandWrongId() throws IOException {
        ServerSimulator simulator = new ServerSimulator().setPassword("password");
        Rcon rcon = new Rcon(simulator);
        rcon.authenticate("password");
        simulator.returnWrongId();
        assertThrows(IOException.class, () -> rcon.sendCommand("command"));
    }

    @Test
    void testMaxPayloadSize() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password"));
        rcon.authenticate("password");
        assertDoesNotThrow(() -> rcon.sendCommand(new String(new char[1446])));
        assertThrows(IllegalArgumentException.class, () -> rcon.sendCommand(new String(new char[1447])));
    }

    @Test
    void testEOF() throws IOException {
        Rcon rcon = new Rcon(new ServerSimulator().setPassword("password").returnEOF());
        assertThrows(EOFException.class, () -> rcon.authenticate("password"));
    }
}
