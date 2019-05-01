package au.edu.federation.capstoneprototype;

public class Beacon {
    public String name;
    public String btClass;
    public String macAddress;
    public String lastSeen;

    public Beacon (String name, String btClass, String macAddress, String lastSeen) {
        this.name = name;
        this.btClass = btClass;
        this.macAddress = macAddress;
        this.lastSeen = lastSeen;
    }
}