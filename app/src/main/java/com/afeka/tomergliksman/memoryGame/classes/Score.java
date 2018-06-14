package com.afeka.tomergliksman.memoryGame.classes;

import android.location.Location;
import android.support.annotation.NonNull;
import com.google.android.gms.maps.model.LatLng;

public class Score implements Comparable<Score>{

    private String playerName;
    private int score;
    private int level;
    private LatLng playerLocation;


    public Score(int score, int level,String playerName) {
        this.score = score;
        this.level=level;
        this.playerName= playerName;
    }

    public int getScore()
    {
        return score;
    }

    @Override
    public int compareTo(@NonNull Score other) {
        if(this.score<other.score)
            return -1;
        else if(this.score>other.score)
            return 1;
        return 0;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public LatLng getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(Location location ) {
        try {
            if(location!=null)
                this.playerLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (NullPointerException e){
        }
    }
}
