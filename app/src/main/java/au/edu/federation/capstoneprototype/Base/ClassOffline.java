package au.edu.federation.capstoneprototype.Base;


public class ClassOffline {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getPresent() {
        return present;
    }

    public void setPresent(String present) {
        this.present = present;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public ClassOffline(int id, String class_id, String student_id, String present, String manual) {
        this.id = id;
        this.class_id = class_id;
        this.student_id = student_id;
        this.present = present;
        this.manual = manual;
    }
    public ClassOffline() {
    }

    private int id;
    private String class_id;
    private String student_id;
    private String present;
    private String manual;

}