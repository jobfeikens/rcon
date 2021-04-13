# RCON [![Java CI](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml/badge.svg)](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml)

Java implementation of the RCON protocol

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
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'nl.vv32.rcon:rcon:1.0.0'
}
```

### Jar
If you're not using Gradle, you can download the latest version [here](https://github.com/jobfeikens/rcon/releases).
