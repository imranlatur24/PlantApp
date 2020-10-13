package com.plantapp.model;

import java.util.ArrayList;

public class TalukaListModel {

    public String response;

    public ArrayList<TalukaList> taluka_list;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<TalukaList> gettaluka_list() {
        return taluka_list;
    }

    public void settaluka_list(ArrayList<TalukaList> taluka_list) {
        this.taluka_list = taluka_list;
    }

    public static class TalukaList {
        private String taluka_id;
        private String taluka_name;
        private String city_id;

        public TalukaList(String taluka_id, String taluka_name, String city_id) {
            this.taluka_id = taluka_id;
            this.taluka_name = taluka_name;
            this.city_id = city_id;
        }

        public String gettaluka_id() {
            return taluka_id;
        }

        public void settaluka_id(String taluka_id) {
            this.taluka_id = taluka_id;
        }

        public String gettaluka_name() {
            return taluka_name;
        }

        public void settaluka_name(String taluka_name) {
            this.taluka_name = taluka_name;
        }

        public String getcity_id() {
            return city_id;
        }

        public void setcity_id(String city_id) {
            this.city_id = city_id;
        }
    }
}
