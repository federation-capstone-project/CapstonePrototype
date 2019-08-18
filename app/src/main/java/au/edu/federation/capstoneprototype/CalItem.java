package au.edu.federation.capstoneprototype;

import java.time.*;
import java.util.Date;

public class CalItem {
    public String ClassName;
    public String ClassClin;
    public String ClassLocation;
    public String ClassTime;

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

    public Date ClassDateTime;

    public CalItem(String name, String clin, String loc, String time,Date date){
        this.ClassName = name;
        this.ClassClin = clin;
        this.ClassLocation = loc;
        this.ClassTime = time;
        this.ClassDateTime = date;

    }
}
