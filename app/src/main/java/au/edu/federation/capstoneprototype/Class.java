package au.edu.federation.capstoneprototype;

public class Class {

    private int id;
    private String code;
    private String name;
    private String teacher;
    private String time;
    private String day;
    private String room;
    private String macAddress;
    private boolean present;

    Class(int id, String code, String name,String teacher, String time, String day, String room, String macAddress, boolean present) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.teacher = teacher;
        this.time = time;
        this.day = day;
        this.room = room;
        this.macAddress = macAddress;
        this.present = present;
    }

    int getClassId() {
        return id;
    }

    public void setClassId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    boolean isPresent() {
        return present;
    }

    void setPresent(boolean present) {
        this.present = present;
    }

}