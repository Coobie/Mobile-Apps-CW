package com.example.jacob.chgraphex.model.history;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class GItemRepository {

    private GItemDao mGItemDao;
    private LiveData<List<GItem>> mAllGItems;

    GItemRepository(Application application) {
        GItemRoomDatabase db = GItemRoomDatabase.getDatabase(application);
        mGItemDao = db.gItemDao();
        mAllGItems = mGItemDao.getAllGItems();
    }

    LiveData<List<GItem>> getAllGItems() {
        return mAllGItems;
    }


    public void insert(GItem gItem) {
        new insertAsyncTask(mGItemDao).execute(gItem);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(mGItemDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<GItem, Void, Void> {

        private GItemDao mAsyncTaskDao;

        insertAsyncTask(GItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private GItemDao mAsyncTaskDao;

        deleteAllAsyncTask(GItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
