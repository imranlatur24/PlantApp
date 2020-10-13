package com.plantapp.model;

import java.util.ArrayList;

public class StateList {

    public String response;

    public ArrayList<CityListModel.CityList> state_list;

    private String state_id;
    private String state_name;

    public StateList(String state_id, String state_name) {
        this.state_id = state_id;
        this.state_name = state_name;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }
}
