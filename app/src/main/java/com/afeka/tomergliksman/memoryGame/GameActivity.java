package com.afeka.tomergliksman.memoryGame;

import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afeka.tomergliksman.memoryGame.classes.Bord;
import com.afeka.tomergliksman.memoryGame.classes.Card;

import static java.lang.Thread.sleep;

public class GameActivity extends AppCompatActivity {

    private final static int EASY_ROWS = 3;
    private final static int EASY_COLS = 4;

    private final static int MEDIUM_ROWS = 4;
    private final static int MEDIUM_COLS = 4;

    private final static int HARD_ROWS = 5;
    private final static int HARD_COLS = 4;

    final static int EASY_TIMER = 30;
    final static int MEDIUM_TIMER = 45;
    final static int HARD_TIMER = 60;

    private GridLayout gameGrid;
    private int timer;
    private Bord gameBord;
    private Button[][] buttons;
    private int difficult;
    private String playerName;
    private int rows;
    private int cols;
    private boolean isFirst;
    private Button firstPress;
    private int numOfPairs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setTitle("Play!");

        Bundle extras = getIntent().getExtras();
        this.playerName = extras.getString("name");
        this.difficult = extras.getInt("difficult");

        this.gameGrid = (GridLayout) findViewById(R.id.grid);
        setName();
        setLevel();
        this.isFirst = true;
        createBord();
        startTimer();
    }

    private void createBord() {
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        final int screenWidth = size.x;
        final int screenHeight = size.y;
        int buttonsWidth = (int) (screenWidth * 0.8 / cols);
        int buttonsHeight = (int) (screenHeight * 0.6 / rows);

        gameGrid.removeAllViews();
        gameGrid.setColumnCount(cols);
        gameGrid.setRowCount(rows);

        this.gameBord = new Bord(rows,cols);
        this.buttons = new Button[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.buttons[i][j] = new Button(this);
                this.buttons[i][j].setBackgroundResource(gameBord.getBord()[i][j].getImage());
                this.buttons[i][j].setLayoutParams(new LinearLayout.LayoutParams(buttonsWidth, buttonsHeight));
                this.buttons[i][j].setTag(gameBord.getBord()[i][j]);
                this.buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Card currentCard = (Card) view.getTag();
                        currentCard.setShow(true);
                        view.setEnabled(false);
                        view.setBackgroundResource(currentCard.getImage());
                        if (isFirst) {
                            firstPress = (Button) view;
                            isFirst = false;
                        } else {
                            if (currentCard.equals(firstPress.getTag())) {
                                numOfPairs--;
                            } else {
                                currentCard.setShow(false);
                                ((Card) firstPress.getTag()).setShow(false);
                                view.setEnabled(true);
                                firstPress.setEnabled(true);
                            }
                            isFirst = true;
                        }
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                drawBord();
                            }
                        }, 1000);
                    }
                });
                gameGrid.addView(buttons[i][j]);
            }
        }
    }

    private void drawBord() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.buttons[i][j].setBackgroundResource(gameBord.getBord()[i][j].getImage());
            }
        }
    }

    private void setName() {
        ((TextView) findViewById(R.id.name)).setText("" + this.playerName);
    }


    private void setLevel() {
        String level = "";
        switch (this.difficult) {
            case 1:
                this.rows = EASY_ROWS;
                this.cols = EASY_COLS;
                this.timer = EASY_TIMER;
                level = "Easy";
                break;
            case 2:
                this.rows = MEDIUM_ROWS;
                this.cols = MEDIUM_COLS;
                this.timer = MEDIUM_TIMER;
                level = "Medium";;
                break;
            case 3:
                this.rows = HARD_ROWS;
                this.cols = HARD_COLS;
                this.timer = HARD_TIMER;
                level = "Hard";
                break;
        }
        this.numOfPairs = (rows*cols)/2;
        ((TextView) findViewById(R.id.difficult)).setText("" + level);
    }

    private void startTimer() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tick(timer);
                if (timer != 0) {
                    timer--;
                    startTimer();
                } else {
//                    gameManager.endGame();
//                    createEndOfGameActivity();
                }
            }
        }, 1000);

    }

    private void tick(int seconds) {
        ((TextView) findViewById(R.id.timer)).setText("" + seconds);

    }
}
