package com.plantapp.model;

import java.util.ArrayList;

public class DistrictModel
{
    public String response;

    public ArrayList<DistrictList> state_list;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<DistrictList> getDistrict_list() {
        return state_list;
    }

    public void setDistrict_list(ArrayList<DistrictList> state_list) {
        this.state_list = state_list;
    }

    public static class DistrictList
    {
        public String state_id;
        public String state_name;
        public String country_id;

        public DistrictList(String state_id, String state_name, String country_id) {
            this.state_id = state_id;
            this.state_name = state_name;
            this.country_id = country_id;
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

        public String getCountry_id() {
            return country_id;
        }

        public void setCountry_id(String country_id) {
            this.country_id = country_id;
        }
    }
}
