# RCON [![Java CI](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml/badge.svg)](https://github.com/jobfeikens/rcon/actions/workflows/workflow.yml)

My implementation of the RCON protocol in Java.

## Example (Java 8)
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
  
