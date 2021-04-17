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

    private volatile boolean closed = false;
    private volatile int requestCounter;

    Rcon(final ByteChannel channel, final int readBufferCapacity, final int writeBufferCapacity) {
        this.channel = channel;

        reader = new PacketReader(channel::read, readBufferCapacity);
        writer = new PacketWriter(channel::write, writeBufferCapacity);
    }

    public static Rcon open(final SocketAddress remote) throws IOException {
        return new RconBuilder().withChannel(SocketChannel.open(remote)).build();
    }

    public static Rcon open(final String hostname, final int port) throws IOException {
        return open(new InetSocketAddress(hostname, port));
    }

    public boolean authenticate(final String password) throws IOException {
        final Packet response = writeAndRead(PacketType.SERVERDATA_AUTH, password);

        if (response.type != PacketType.SERVERDATA_AUTH_RESPONSE) {
            throw new IOException("Invalid auth response type: " + response.type);
        }
        return response.isValid();
    }

    public String sendCommand(final String command) throws IOException {
        final Packet response = writeAndRead(PacketType.SERVERDATA_EXECCOMMAND, command);

        if (response.type != PacketType.SERVERDATA_RESPONSE_VALUE) {
            throw new IOException("Wrong command response type: " + response.type);
        }
        if (!response.isValid()) {
            throw new IOException("Invalid command response: " + response.payload);
        }
        return response.payload;
    }

    private synchronized Packet writeAndRead(final int packetType, final String payload) throws IOException {
        if (closed) {
            throw new IllegalStateException("Trying to use RCON after close was called");
        }
        final int requestId = requestCounter++;

        writer.write(new Packet(requestId, packetType, payload));
        final Packet response = reader.read();

        if (response.isValid() && response.requestId != requestId) {
            throw new IOException(String.format("Unexpected response id (%d -> %d)", requestId, response.requestId));
        }
        return response;
    }

    @Override
    public void close() throws IOException {
        channel.close();
        closed = true;
    }
}
