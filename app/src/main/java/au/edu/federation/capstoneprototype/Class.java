package au.edu.federation.capstoneprototype;

import java.util.Date;

public class Class {

    private int id;
    private String code;
    private String name;
    private int teacher_id;
    private String teacher_name;
    private String location;
    private String mac;
    private String date; // (yyyy-MM-dd HH:mm:ss)
    private String start; //
    private String finish; //
    private String present;
    private boolean cansee;
    private Date comp_date;

    public Class(){   }
    public Class(int id, String code, String name, int teacher_id, String teacher_name, String location, String mac, String date, String start, String finish, String present) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.teacher_id = teacher_id;
        this.teacher_name = teacher_name;
        this.location = location;
        this.mac = mac;
        this.date = date;
        this.start = start;
        this.finish = finish;
        this.present = present;
        comp_date = Utils.string_date_full(start);

    }

    public Date getComp_date(){return comp_date;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeacherID() {
        return teacher_id;
    }

    public void setTeacherID(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public void setTeacherName(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getTeacherName() {
        return teacher_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }


    public String isPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public boolean canSee(){return cansee;}

    public void setCansee(Boolean cansee) {this.cansee = cansee;}



}