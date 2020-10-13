package com.plantapp.model;

import java.util.ArrayList;

public class CityListModel {

    public String response;

    public ArrayList<CityList> cities_list;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<CityList> getCities_list() {
        return cities_list;
    }

    public void setCities_list(ArrayList<CityList> cities_list) {
        this.cities_list = cities_list;
    }

    public static class CityList {
        private String city_id;
        private String city_name;
        private String state_id;

        public CityList(String city_id, String city_name, String state_id) {
            this.city_id = city_id;
            this.city_name = city_name;
            this.state_id = state_id;
        }

        public String getCity_id() {
            return city_id;
        }

        public void setCity_id(String city_id) {
            this.city_id = city_id;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public String getState_id() {
            return state_id;
        }

        public void setState_id(String state_id) {
            this.state_id = state_id;
        }
    }
}
