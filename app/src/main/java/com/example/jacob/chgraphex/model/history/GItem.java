package com.example.jacob.chgraphex.model.history;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.jacob.chgraphex.model.searchResults.CompanyItem;
import com.example.jacob.chgraphex.model.searchResults.Item;
import com.example.jacob.chgraphex.model.searchResults.OfficerItem;

@Entity(tableName = "gItem_table")
public class GItem {

    @PrimaryKey (autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "gItem")
    private String mGItem;

    @NonNull
    private String title;

    @Nullable
    private String address;

    @NonNull
    private int type;

    @Nullable
    private String dataCreation;

    @Nullable
    private String nationality;

    /**
     * Constructor
     * @param gItem GItem
     * @param title String
     * @param type int - 0=company, 1=officer
     */
    public GItem(String gItem, String title, int type) {
        this.mGItem = gItem;
        this.title = title;
        this.type = type;
    }

    /**
     * Constructor that converts Item to GItem
     * @param i - Item
     */
    public GItem(Item i) {
        this.mGItem = i.getId();
        this.title = i.getTitle();

        if (!i.getAddress().equals("")){
            this.address = i.getAddress();
        }

        if (i instanceof OfficerItem){
            this.type = 1;

        } else if (i instanceof CompanyItem){
            this.type = 0;
        }

    }

    public String getGItem() {
        return this.mGItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getmGItem() {
        return mGItem;
    }

    public void setmGItem(@NonNull String mGItem) {
        this.mGItem = mGItem;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Nullable
    public String getDataCreation() {
        return dataCreation;
    }

    public void setDataCreation(@Nullable String dataCreation) {
        this.dataCreation = dataCreation;
    }

    @Nullable
    public String getNationality() {
        return nationality;
    }

    public void setNationality(@Nullable String nationality) {
        this.nationality = nationality;
    }
}
