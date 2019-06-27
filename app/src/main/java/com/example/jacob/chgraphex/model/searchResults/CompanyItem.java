package com.example.jacob.chgraphex.model.searchResults;

import java.io.Serializable;

public class CompanyItem extends Item implements Serializable {

    private String dateCreation = "";

    /**
     * Constructor
     * @param title - String
     */
    public CompanyItem(String title) {
        super(title);
    }

    /**
     * Constructor
     * @param title - String
     * @param companyNumber - String - id of the company
     */
    public CompanyItem(String title, String companyNumber) {
        super(title);
        setId(companyNumber);
    }

    /**
     * Getter for date of creation
     * @return String date
     */
    public String getDateCreation() {
        return dateCreation;
    }

    /**
     * Setter for date of creation
     * @param dateCreation - String date
     */
    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }
}

