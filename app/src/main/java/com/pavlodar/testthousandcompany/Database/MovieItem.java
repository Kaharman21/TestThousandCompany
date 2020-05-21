package com.pavlodar.testthousandcompany.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "Movie")
public class MovieItem {

    public MovieItem() {
    }


    @PrimaryKey
    @ColumnInfo(name = "movieid")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "voteAverage")
    private Double voteAverage;

    @ColumnInfo(name = "posterPath")
    private String posterPath;

    @ColumnInfo(name = "position")
    private int position;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
