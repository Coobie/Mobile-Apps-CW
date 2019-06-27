package com.example.jacob.chgraphex.model.history;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class GItemViewModel extends AndroidViewModel {

    private GItemRepository mRepository;

    private LiveData<List<GItem>> mAllGItems;

    public GItemViewModel(Application application) {
        super(application);
        mRepository = new GItemRepository(application);
        mAllGItems = mRepository.getAllGItems();
    }

    public LiveData<List<GItem>> getAllGItems() {
        return mAllGItems;
    }

    public void insert(GItem gItem) {
        mRepository.insert(gItem);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
}
