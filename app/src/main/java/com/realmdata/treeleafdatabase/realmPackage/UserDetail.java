package com.realmdata.treeleafdatabase.realmPackage;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserDetail extends RealmObject {
    @PrimaryKey
    @Index
    private long id;

    @Required
    private String fullName;

    @Required
    private String gender;

    @Required
    private String address;

    @Required
    private  String desc;

    @Required
    private String imageLocation;

    @Required
    private String email;

    private String codedDescription;

    public String getCodedDescription() {
        return codedDescription;
    }

    public void setCodedDescription(String codedDescription) {
        this.codedDescription = codedDescription;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String  getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}
