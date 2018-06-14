package com.afeka.tomergliksman.memoryGame.classes;

import com.afeka.tomergliksman.memoryGame.R;

public class Card {

    private int id;
    private int image;
    private boolean isShow;

    public Card(int id, int image){
        this.id = id;
        this.image = image;
        this.isShow = false;
    }

    public int getImage(){
        if(isShow){
            return this.image;
        }
        return R.drawable.question_mark;
    }

    public void setShow(boolean show){
        this.isShow = show;
    }

    public boolean isShow(){
        return isShow;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Card)obj).id == this.id;
    }
}
