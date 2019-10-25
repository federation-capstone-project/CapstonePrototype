package au.edu.federation.capstoneprototype.Base;

import java.util.Date;

public class CalItem {
    public String ClassName;
    public String ClassClin;
    public String ClassLocation;
    public String ClassTime;
    public String ClassPresence;
    public Date ClassDateTime;

    public CalItem(String name, String clin, String loc, String time, Date date, String presence) {
        this.ClassName = name;
        this.ClassClin = clin;
        this.ClassLocation = loc;
        this.ClassTime = time;
        this.ClassDateTime = date;
        this.ClassPresence = presence;

    }

    public String getClassPresence() {
        return ClassPresence;
    }

    public void setClassPresence(String classPresence) {
        ClassPresence = classPresence;
    }

    public String getClassName() {
        return ClassName;
    }

    public String getClassClin() {
        return ClassClin;
    }

    public String getClassLocation() {
        return ClassLocation;
    }

    public String getClassTime() {
        return ClassTime;
    }

    public Date getClassDateTime() {
        return ClassDateTime;
    }
}
