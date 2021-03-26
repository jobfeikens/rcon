package nl.vv32.rcon;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;

public class Rcon implements Closeable {

    final private ByteChannel channel;
    final private PacketReader reader;
    final private PacketWriter writer;

    private boolean authenticated = false;
    private boolean closed = false;
    private int requestCounter;

    Rcon(final ByteChannel channel) {
        this.channel = channel;

        reader = new PacketReader(channel::read);
        writer = new PacketWriter(channel::write, 1460);
    }

    public static Rcon open(final SocketAddress remote) throws IOException {
        return new Rcon(SocketChannel.open(remote));
    }

    synchronized public boolean authenticate(final String password)
            throws IOException {
        requireOpen();

        if (!authenticated) {
            final Packet response = writeAndRead(PacketType.LOGIN, password);

            if (response.type != PacketType.COMMAND) {
                throw new IOException("Invalid login response type");
            }
            authenticated = response.isValid();
        }
        return authenticated;
    }

    synchronized public String sendCommand(final String command)
            throws IOException {
        requireOpen();

        if (!authenticated) {
            throw new IllegalStateException("Not authenticated");
        }
        final Packet response = writeAndRead(PacketType.COMMAND, command);

        if (response.type != PacketType.COMMAND_RESPONSE) {
            throw new IOException("Wrong command response type");
        }
        return response.payload;
    }

    private Packet writeAndRead(
            final PacketType type, final String payload) throws IOException {
        final int requestId = requestCounter++;

        writer.write(new Packet(requestId, type, payload));
        final Packet response = reader.read();

        if (response.isValid() && response.requestId != requestId) {
            throw new IOException("Invalid response id");
        }
        return response;
    }

    private void requireOpen() {
        if (closed) {
            throw new IllegalStateException("Rcon closed");
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
        closed = true;
    }
}
