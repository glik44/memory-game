package com.afeka.tomergliksman.memoryGame.classes;

import com.afeka.tomergliksman.memoryGame.R;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Bord {

    private static int[] images = {
            R.drawable.alfa_romeo,
            R.drawable.audi,
            R.drawable.bmw,
            R.drawable.chevrolet,
            R.drawable.ferrari,
            R.drawable.ford,
            R.drawable.hyundai,
            R.drawable.marchedrs,
            R.drawable.mazda,
            R.drawable.volkswagen,
            R.drawable.citroen,
            R.drawable.fiat,
            R.drawable.kia,
            R.drawable.lexus,
            R.drawable.mini,
            R.drawable.mitsubishi,
            R.drawable.volvo};


    private int rows;
    private int cols;
    private Card[][] bord;
    private Card[] cards;

    public Bord(int rows, int cols){
        this.bord = new Card[rows][cols];
        this.cards = new Card[rows*cols];
        this.rows = rows;
        this.cols = cols;
        createBord();
    }

    private void pickCards(){
        for(int i = 0 ,imageIndex = 0; i < this.cards.length; i+=2, imageIndex++){
            this.cards[i] = new Card(i,images[imageIndex]);
            this.cards[i + 1] = new Card(i, images[imageIndex]);
        }

        Collections.shuffle(Arrays.asList(this.cards));

    }
    private void createBord(){
        int cardIndex = 0;
        pickCards();
        for(int i = 0 ; i < this.rows; i++){
            for(int j = 0; j < this.cols; j++){
                this.bord[i][j] = this.cards[cardIndex];
                cardIndex++;
            }
        }
    }

    public Card[][] getBord(){
        return this.bord;
    }


}
