package com.example.findmate;

public class RequestRecyclerModel {

    String name , photoUri,phone, verification,uid;

    public RequestRecyclerModel() {
    }

    public RequestRecyclerModel(String name, String photoUri, String phone, String verification) {
        this.name = name;
        this.photoUri = photoUri;
        this.phone = phone;
        this.verification = verification;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
