package com.afeka.tomergliksman.memoryGame.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ScoreTable {
    final static int MAX_TABLE_SIZE = 10;
    private Context context;
    private ArrayList<Score> scoreTable;
    private int currentMinScore;


    public ScoreTable(Context context) {
        this.context = context;
        this.scoreTable =new ArrayList<Score>();
        this.loadTable(context);
        this.currentMinScore = 0;
    }

    public void saveEntityToMemory(SharedPreferences.Editor editorTable, Score score, int index) {
        String jsonStringScore = new Gson().toJson(score);
        editorTable.putString(this.getClass().getSimpleName() + index, jsonStringScore);
    }

    public void saveTableToMemory() {
        SharedPreferences.Editor editorTable = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE).edit();
        for (int i = 0; i < scoreTable.size(); i++) {
            this.saveEntityToMemory(editorTable, this.scoreTable.get(i), i);
        }
        editorTable.apply();

    }

    public void updateScoreTable(Score newScore) {

        if (scoreTable.size() < MAX_TABLE_SIZE) {
            scoreTable.add(newScore);

        } else if (newScore.getScore() > currentMinScore) {
            scoreTable.remove(scoreTable.size() - 1);
            scoreTable.add(newScore);
        }
        Collections.sort(scoreTable);
        this.currentMinScore = scoreTable.get(scoreTable.size() - 1).getScore();

    }

    private  void loadTable(Context context) {

        SharedPreferences sp = context.getSharedPreferences("HighScoreTable", MODE_PRIVATE);
        Map<String,?> scores = sp.getAll();

        //get the high scores
        for (Map.Entry<String, ?> entry : scores.entrySet()){
            String json = entry.getValue().toString();
            scoreTable.add(new Gson().fromJson(json,Score.class));
        }

    }

    public  ArrayList<Score> getScoreTable()
    {
        return this.scoreTable;
    }
}


