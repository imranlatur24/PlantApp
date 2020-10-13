package com.plantapp.model;

import java.util.ArrayList;

public class ResponseResult {

    String response;
    private String userMsgList;

    public String getUserMsgList() {
        return userMsgList;
    }

    public void setUserMsgList(String userMsgList) {
        this.userMsgList = userMsgList;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<CusProfileList> getLogin() {
        return login;
    }

    public void setLogin(ArrayList<CusProfileList> login) {
        this.login = login;
    }

    private ArrayList<CusProfileList> login;


}
