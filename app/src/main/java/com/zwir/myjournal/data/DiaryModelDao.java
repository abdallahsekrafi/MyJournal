package com.zwir.myjournal.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
@TypeConverters(DateConverter.class)
public interface DiaryModelDao {

    @Query("select * from Diary")
    LiveData<List<Diary>> getAllDiaryItems();

    @Query("select * from Diary where id = :id")
    Diary getDiaryById(String id);

    @Insert(onConflict = REPLACE)
    void addDiary(Diary diary);

    @Delete
    void deleteDiary(Diary diary);
    @Update
    void updateDiary(Diary diary);

}
