package nl.vv32.rcon;

import java.nio.channels.ByteChannel;

public class RconBuilder {

    public static int DEFAULT_READ_BUFFER_CAPACITY = 4110;
    public static int DEFAULT_WRITE_BUFFER_SIZE = 1460;

    final private ByteChannel channel;

    private Integer readBufferCapacity;
    private Integer writeBufferCapacity;

    public RconBuilder(final ByteChannel channel) {
        this.channel = channel;
    }

    public RconBuilder withReadBufferCapacity(final int readBufferCapacity) {
        this.readBufferCapacity = readBufferCapacity;
        return this;
    }

    public RconBuilder withWriteBufferCapacity(final int writeBufferCapacity) {
        this.writeBufferCapacity = writeBufferCapacity;
        return this;
    }

    public Rcon build() {
        return new Rcon(channel,
                readBufferCapacity != null ? readBufferCapacity : DEFAULT_READ_BUFFER_CAPACITY,
                writeBufferCapacity != null ? writeBufferCapacity : DEFAULT_WRITE_BUFFER_SIZE);
    }
}
