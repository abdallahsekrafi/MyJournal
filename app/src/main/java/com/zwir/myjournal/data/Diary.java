package com.zwir.myjournal.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.zwir.myjournal.R;

import java.io.Serializable;
import java.util.Date;
@Entity
public class Diary implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String title;
    private String summary;
    @TypeConverters(DateConverter.class)
    private Date createdDate;
    private String bookMark;
    @Ignore
    public static final String blueBM="blue";
    @Ignore
    public static final String yellowBM="yellow";
    @Ignore
    public static final String orangeBM="orange";
    @Ignore
    public static final String redBM="red";

    public Diary(String title, String summary, Date createdDate, String bookMark) {
        this.title = title;
        this.summary = summary;
        this.createdDate = createdDate;
        this.bookMark = bookMark;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getBookMark() {
        return bookMark;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setBookMark(String bookMark) {
        this.bookMark = bookMark;
    }

    public int getDrawableBookMark(String bookMark) {
        int bookMarkDrawableId=R.drawable.blue_bm;
        switch (bookMark){
            case blueBM: bookMarkDrawableId= R.drawable.blue_bm;
            break;
            case yellowBM: bookMarkDrawableId= R.drawable.yellow_bm;
                break;
            case orangeBM: bookMarkDrawableId= R.drawable.orange_bm;
                break;
            case redBM: bookMarkDrawableId= R.drawable.red_bm;
                break;

        }
        return bookMarkDrawableId;
    }
    public static Integer[] getBookMarkArray() {
        return new Integer[]{R.drawable.blue_bm, R.drawable.yellow_bm,
                R.drawable.orange_bm, R.drawable.red_bm};
    }

}
