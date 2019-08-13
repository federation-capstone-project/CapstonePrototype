package au.edu.federation.capstoneprototype;

import java.time.*;
import java.util.Date;

public class CalItem {
    public String ClassName;
    public String ClassClin;
    public String ClassLocation;
    public String ClassTime;
    public LocalDateTime ClassDateTime;

    public CalItem(String name, String clin, String loc, String time,LocalDateTime date){
        this.ClassName = name;
        this.ClassClin = clin;
        this.ClassLocation = loc;
        this.ClassTime = time;
        this.ClassDateTime = date;

    }
}
