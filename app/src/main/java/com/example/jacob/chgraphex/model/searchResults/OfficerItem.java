package com.example.jacob.chgraphex.model.searchResults;

public class OfficerItem extends Item {

    private String nationality = "";

    /**
     * Constructor
     * @param title
     */
    public OfficerItem(String title) {
        super(title);
    }

    /**
     * Constructor
     * @param title
     * @param officerNumber - String id of officer
     */
    public OfficerItem(String title, String officerNumber) {
        super(title);
        setId(officerNumber);
    }

    /**
     * Getter for the nationality of the officer
     * @return String - nationality
     */
    public String getNationality() {
        return nationality;
    }

    /**
     * Setter for the nationality of the officer
     * @param nationality
     */
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
}
