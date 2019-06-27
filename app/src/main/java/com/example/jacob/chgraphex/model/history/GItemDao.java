package com.example.jacob.chgraphex.model.history;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface GItemDao {

    @Insert
    void insert(GItem gItem);

    @Query("DELETE FROM gItem_table")
    void deleteAll();

    @Query("SELECT * from gItem_table ORDER BY id DESC")
    LiveData<List<GItem>> getAllGItems();
}
