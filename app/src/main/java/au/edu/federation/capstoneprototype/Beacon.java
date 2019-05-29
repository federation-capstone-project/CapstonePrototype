package au.edu.federation.capstoneprototype;

public class Beacon {
    public String name;
    public String macAddress;
    public String lastSeen;

    public Beacon (String name, String macAddress, String lastSeen) {
        this.name = name;
        this.macAddress = macAddress;
        this.lastSeen = lastSeen;
    }
}