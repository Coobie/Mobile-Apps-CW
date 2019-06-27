package com.example.jacob.chgraphex.apiRelated;


import com.example.jacob.chgraphex.model.searchResults.CompanyItem;
import com.example.jacob.chgraphex.model.searchResults.Item;
import com.example.jacob.chgraphex.model.searchResults.OfficerItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import android.util.Base64;

import java.util.List;


public class CHRequest {

    private String address = "https://api.companieshouse.gov.uk/"; //Base url
    private String key = "7cShYPdr05uUGBO1L91A5_wCxwfo5vqatBs_Vm-G"; //API key
    private int number_items = 0;
    private int timeout = 30000; // 30 secs time out

    /**
     * Default constructor
     */
    public CHRequest() {
    }

    /**
     * Constructor
     * @param number_items - int number of items
     */
    public CHRequest(int number_items) {

        this.number_items = number_items;
    }

    /**
     * Getter number of items
     * @return int number of items
     */
    public int getNumber_items() {

        return number_items;
    }

    /**
     * Setter number of items
     * @param number_items - int
     */
    public void setNumber_items(int number_items) {

        this.number_items = number_items;
    }

    /**
     * Method for a new search
     * @param search - String - the user's search request
     * @param type - int 0=company, 1=officer
     * @return list of Item - The results from the search
     * @throws Exception
     */
    public List<Item> newSearch(String search, int type) throws Exception {
        String address = this.address + "search";
        switch (type) {
            case 0: //SearchActivity for company
                address += "/companies";
                break;
            case 1:
                address += "/officers";
                break;
        }

        search = search.replace(" ", "+");

        address += "?q=" + search; //Actual search
        //Log.d("ADDRESS_TEST",address);
        //String address = this.address+"search?q=test";
        String password = "";
        URL url = new URL(address);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(timeout);

        String user_pass = key + ":" + password;

        //conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(user_pass.getBytes()));
        conn.addRequestProperty("Authorization", "Basic " + Base64.encodeToString(user_pass.getBytes(),Base64.DEFAULT));

        //Process response
        String line = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = input.readLine()) != null)
            sb.append(line);
        input.close();
        JSONObject test = new JSONObject(sb.toString());
        JSONArray t = test.getJSONArray("items");

        //Log.d("CATFISH",sb.toString());

        //Decode JSON to useful
        List<Item> items = new ArrayList<>();


        if (type == 0) {
            //SearchActivity for companies
            for (int i = 0; i < t.length(); i++) {
                CompanyItem item = new CompanyItem(t.getJSONObject(i).getString("title"));
                item.setId(t.getJSONObject(i).getString("company_number"));
                if (t.getJSONObject(i).has("date_of_creation")){
                    item.setDateCreation(t.getJSONObject(i).getString("date_of_creation"));
                }

                item.setAddress(t.getJSONObject(i).getString("address_snippet"));
                items.add(item);
            }
        } else if (type == 1) {
            //SearchActivity for officers
            for (int i = 0; i < t.length(); i++) {
                OfficerItem item = new OfficerItem(t.getJSONObject(i).getString("title"));

                String a = t.getJSONObject(i).getJSONObject("links").getString("self");
                String[] parts = a.split("/");
                a = parts[2];
                item.setId(a);
                item.setAddress(t.getJSONObject(i).getString("address_snippet"));
                if (t.getJSONObject(i).has("nationality")){
                    item.setNationality(t.getJSONObject(i).getString("nationality"));
                }
                items.add(item);
            }
        } else {
            items = null;
        }

        return items;
    }

    /**
     * Get officers for a company
     * @param company - the company in question
     * @return list of OfficerItem - officers of the company
     * @throws Exception
     */
    public List<OfficerItem> getOfficers(CompanyItem company) throws Exception {
        List<OfficerItem> officers = new ArrayList<>();

        String address = this.address + "company/" + company.getId() + "/officers";

        String password = "";
        URL url = new URL(address);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(timeout);

        String user_pass = key + ":" + password;

        //conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(user_pass.getBytes()));
        conn.addRequestProperty("Authorization", "Basic " + Base64.encodeToString(user_pass.getBytes(),Base64.DEFAULT));

        //Process response
        String line = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = input.readLine()) != null)
            sb.append(line);
        input.close();
        JSONObject test = new JSONObject(sb.toString());
        JSONArray t = test.getJSONArray("items");

        for (int i = 0; i < t.length(); i++) {
            OfficerItem o = new OfficerItem(t.getJSONObject(i).getString("name"));
            String a = t.getJSONObject(i).getJSONObject("links").getJSONObject("officer").getString("appointments");
            String[] parts = a.split("/");
            a = parts[2];
            o.setId(a);
            String oAddress = "";
            JSONObject s = t.getJSONObject(i).getJSONObject("address");

            if (s.has("address_line_1")){
                oAddress += s.getString("address_line_1") + ", ";
            }
            if (s.has("address_line_2")){
                oAddress += s.getString("address_line_2") + ", ";
            }
            if (s.has("locality")){
                oAddress += s.getString("locality") + ", ";
            }
            if (s.has("postal_code")){
                oAddress += s.getString("postal_code");
            }
            o.setAddress(oAddress);

            if (t.getJSONObject(i).has("officer_role")){
                o.setLink(t.getJSONObject(i).getString("officer_role"));
            }

            if (t.getJSONObject(i).has("nationality")){
                o.setNationality(t.getJSONObject(i).getString("nationality"));
            }

            officers.add(o);
        }

        return officers;
    }

    /**
     * Get officers for a company
     * @param officer - the officer in question
     * @return list of CompanyItem - companies associated with the officer
     * @throws Exception
     */
    public List<CompanyItem> getCompanies(OfficerItem officer) throws Exception {
        List<CompanyItem> companies = new ArrayList<>();

        String address = this.address + "officers/" + officer.getId() + "/appointments";

        String password = "";
        URL url = new URL(address);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(timeout);

        String user_pass = key + ":" + password;

       // conn.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(user_pass.getBytes()));
        conn.addRequestProperty("Authorization", "Basic " + Base64.encodeToString(user_pass.getBytes(),Base64.DEFAULT));

        //Process response
        String line = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while ((line = input.readLine()) != null)
            sb.append(line);
        input.close();
        JSONObject test = new JSONObject(sb.toString());
        JSONArray t = test.getJSONArray("items");

        for (int i = 0; i < t.length(); i++) {
            CompanyItem c = new CompanyItem(t.getJSONObject(i).getJSONObject("appointed_to").getString("company_name"));
            c.setId(t.getJSONObject(i).getJSONObject("appointed_to").getString("company_number"));
            if (t.getJSONObject(i).has("officer_role")){
                c.setLink(t.getJSONObject(i).getString("officer_role"));
            }
            companies.add(c);
        }

        return companies;
    }


}
