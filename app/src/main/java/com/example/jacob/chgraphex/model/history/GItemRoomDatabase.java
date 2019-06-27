package com.example.jacob.chgraphex.model.history;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.jacob.chgraphex.model.searchResults.Item;

@Database(entities = {GItem.class}, version = 1)
public abstract class GItemRoomDatabase extends RoomDatabase {

    public abstract GItemDao gItemDao();

    private static volatile GItemRoomDatabase INSTANCE;

    static GItemRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GItemRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GItemRoomDatabase.class, "gItem_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    //new PopulateDbAsync(INSTANCE).execute();
                }
            };

}

class PopulateDbAsync extends AsyncTask<Item, Void, Void> {

    private final GItemDao mDao;

    PopulateDbAsync(GItemRoomDatabase db) {
        mDao = db.gItemDao();
    }

    @Override
    protected Void doInBackground(final Item... params) {
        //mDao.deleteAll();
        GItem gItem = new GItem("Hello", "TEST", 0);
        mDao.insert(gItem);
        return null;
    }
}
