package com.imist.italker.factory.model.api.account;

public class RegisterModel {
    private String account;
    private String name;
    private String password;
    private String pushId;

    @Override
    public String toString() {
        return "RegisterModel{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", pushId='" + pushId + '\'' +
                '}';
    }

    public RegisterModel(String account, String password, String name) {
        this.account = account;
        this.name = name;
        this.password = password;
    }

    public RegisterModel(String account, String password, String name, String pushId) {
        this.account = account;
        this.name = name;
        this.password = password;
        this.pushId = pushId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
