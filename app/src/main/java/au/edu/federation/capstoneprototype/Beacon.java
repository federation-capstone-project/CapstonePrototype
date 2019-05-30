package au.edu.federation.capstoneprototype;

public class Beacon {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getID() {
        return class_id;
    }

    public void setID(int class_id) {
        this.class_id = class_id;
    }

    public int class_id;
    public String name;
    public String macAddress;
    public String lastSeen;

    public Beacon (Integer class_id, String name, String macAddress, String lastSeen) {
        this.class_id = class_id;
        this.name = name;
        this.macAddress = macAddress;
        this.lastSeen = lastSeen;
    }
}