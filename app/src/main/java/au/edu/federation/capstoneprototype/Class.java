package au.edu.federation.capstoneprototype;

public class Class {

    public Class(int id, String name, String teacher, String time, String day, String room, String macAddress, boolean present) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.time = time;
        this.day = day;
        this.room = room;
        this.macAddress = macAddress;
        this.present = present;
    }

    public int getClassId() {
        return id;
    }

    public void setClassId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int id;
    public String name;
    public String teacher;
    public String time;
    public String day;
    public String room;
    public String macAddress;

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean present;

}