package com.example.findmate;

public class RecyclerModel {

    String name, department, grade, situation,uid,photoUri;

    public RecyclerModel(String name, String department, String grade, String situation,String photoUri) {
        this.name = name;
        this.department = department;
        this.grade = grade;
        this.situation = situation;
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
