# RCON [![Java CI](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml/badge.svg)](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml)

My implementation of the RCON protocol in Java.

## Example (Java 8)
```java
public static void main(String[] args) throws IOException {
    SocketAddress address = new InetSocketAddress("localhost", 25575);

    try(Rcon rcon = Rcon.open(address)) {
        if (rcon.authenticate("myPassword")) {
            rcon.sendCommand("say Hello World");
        } else {
            System.out.println("Failed to authenticate");
        }
    }
}
```
  
