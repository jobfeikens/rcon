package nl.vv32.rcon;

import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RconBuilder {

    private ByteChannel channel;

    private Integer readBufferCapacity = 4110;
    private Integer writeBufferCapacity = 1460;
    private Charset charset = StandardCharsets.US_ASCII;

    public RconBuilder withChannel(ByteChannel channel) {
        this.channel = channel;
        return this;
    }

    public RconBuilder withReadBufferCapacity(final int readBufferCapacity) {
        this.readBufferCapacity = readBufferCapacity;
        return this;
    }

    public RconBuilder withWriteBufferCapacity(final int writeBufferCapacity) {
        this.writeBufferCapacity = writeBufferCapacity;
        return this;
    }

    public RconBuilder withCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    public Rcon build() {

        return new Rcon(Objects.requireNonNull(channel, "channel"),
                readBufferCapacity,
                writeBufferCapacity,
                new PacketCodec(charset)
        );
    }
}
