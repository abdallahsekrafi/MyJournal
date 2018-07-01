package com.zwir.myjournal.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.zwir.myjournal.progress.CustomProgressDialog;

import java.util.List;

public class DiaryListViewModel extends AndroidViewModel {

    private final LiveData<List<Diary>> itemAndPersonList;

    private AppDatabase appDatabase;

    public DiaryListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());

        itemAndPersonList = appDatabase.itemAndDiaryModel().getAllDiaryItems();
    }


    public LiveData<List<Diary>> getItemAndPersonList() {
        return itemAndPersonList;
    }
    public void deleteItem(Diary diary) {
        new deleteAsyncTask(appDatabase).execute(diary);
    }
    public void saveItem(Diary diary) {
        new saveAsyncTask(appDatabase).execute(diary);
    }
    public void updateItem(Diary diary) {
        new updateAsyncTask(appDatabase).execute(diary);
    }

    private static class deleteAsyncTask extends AsyncTask<Diary, Void, Void> {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Diary... params) {
            db.itemAndDiaryModel().deleteDiary(params[0]);
            return null;
        }

    }
    private static class saveAsyncTask extends AsyncTask<Diary, Void, Void> {

        private AppDatabase db;

        saveAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Diary... params) {
            db.itemAndDiaryModel().addDiary(params[0]);
            return null;
        }

    }
    private static class updateAsyncTask extends AsyncTask<Diary, Void, Void> {

        private AppDatabase db;

        updateAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Diary... params) {
            db.itemAndDiaryModel().updateDiary(params[0]);
            return null;
        }

    }
}
