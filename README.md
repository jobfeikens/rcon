# RCON [![Java CI](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml/badge.svg)](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml)

Minimal implementation of the RCON protocol in Java.

## Example (Java 8+)
```java
public static void main(String[] args) throws IOException {

    try(Rcon rcon = Rcon.open("localhost", 25575)) {
        if (rcon.authenticate("password")) {
            System.out.println(rcon.sendCommand("list"));
        } else {
            System.out.println("Failed to authenticate");
        }
    }
}
```

## Add RCON to your project

### Gradle
Add to `build.gradle`:
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'nl.vv32.rcon:rcon:1.1.0'
}
```

### Maven
Add to `pom.xml`:
```xml
<dependencies>
    <dependency>
        <groupId>nl.vv32.rcon</groupId>
        <artifactId>rcon</artifactId>
        <version>1.1.0</version>
    </dependency>
</dependencies>
```

### Jar
If you're not using Gradle, you can download the latest version [here](https://github.com/jobfeikens/rcon/releases).

## Changing the packet body codec
By default, the bodies of packets are encoded as ASCII.
To use UTF8 (or any other charset for that matter), use the following example;

```java
try (Rcon rcon = Rcon.newBuilder()
        .withChannel(SocketChannel.open(
                new InetSocketAddress("localhost", 25575)))
        .withCharset(StandardCharsets.UTF_8)
        .build()) {
    
    ...
}
```
