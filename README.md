```
public static void main(String[] args) throws IOException {
    SocketAddress address = new InetSocketAddress("localhost", 25575);

    try(Rcon rcon = Rcon.open(address)) {
        if (rcon.authenticate("test")) {
            rcon.sendCommand("say Hello World");
        } else {
            System.out.println("Failed to authenticate");
        }
    }
}
```