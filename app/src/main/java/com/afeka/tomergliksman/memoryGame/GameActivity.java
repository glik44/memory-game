package com.afeka.tomergliksman.memoryGame;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afeka.tomergliksman.memoryGame.Services.PlayerLocation;
import com.afeka.tomergliksman.memoryGame.Services.RotationService;
import com.afeka.tomergliksman.memoryGame.classes.Bord;
import com.afeka.tomergliksman.memoryGame.classes.Card;
import com.afeka.tomergliksman.memoryGame.classes.Score;

import tyrantgit.explosionfield.ExplosionField;

import static com.afeka.tomergliksman.memoryGame.Strings.NAME;
import static com.afeka.tomergliksman.memoryGame.Strings.BIRTHDAY;
import static com.afeka.tomergliksman.memoryGame.Strings.DIFFICULT;


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
    private String birthday;
    private int rows;
    private int cols;
    private boolean isFirst;
    private boolean playerWin;
    private Button firstPress;
    private int numOfPairs;
    private AlertDialog dialog;
    Handler timerHandler = new Handler();
    public RotationService.SensorServiceBinder binder;
    private boolean isServiceBound = false;
    private PlayerLocation playerLocation;
    ExplosionField explosionField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setTitle("Play!");
        Bundle extras = getIntent().getExtras();
        this.playerName = extras.getString(NAME);
        this.difficult = extras.getInt(DIFFICULT);
        this.birthday = extras.getString(BIRTHDAY);
        playerWin = false;
        this.gameGrid = (GridLayout) findViewById(R.id.grid);
        setName();
        setLevel();
        this.isFirst = true;
        createBord();
        playerLocation = new PlayerLocation(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, RotationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(RotationService.SENSOR_SERVICE_BROADCAST_ACTION));
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
        }
        unbindService(serviceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        timerHandler.removeCallbacksAndMessages(null);
        playerLocation.removeUpdates();
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

        this.gameBord = new Bord(rows, cols);
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
                        }
                        else {
                            setEnableAll(false);
                            if (currentCard.equals(firstPress.getTag())) {
                                numOfPairs--;
                                if (numOfPairs == 0) {
                                    playerWin = true;
                                    endGame("win", timer);
                                }
                            }
                            else {
                                currentCard.setShow(false);
                                ((Card) firstPress.getTag()).setShow(false);
                                view.setEnabled(true);
                                firstPress.setEnabled(true);
                            }
                            isFirst = true;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    drawBord();
                                    setEnableAll(true);
                                }
                            }, 600);
                        }
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

    private void drawBord(int image) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.buttons[i][j].setBackgroundResource(image);
            }
        }
    }

    public void setEnableAll(boolean enabledValue) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(!((Card)this.buttons[i][j].getTag()).isShow())
                    this.buttons[i][j].setEnabled(enabledValue);
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
                level = "Medium";
                ;
                break;
            case 3:
                this.rows = HARD_ROWS;
                this.cols = HARD_COLS;
                this.timer = HARD_TIMER;
                level = "Hard";
                break;
        }
        this.numOfPairs = (rows * cols) / 2;
        ((TextView) findViewById(R.id.difficult)).setText("" + level);
    }

    private void startTimer() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tick(timer);
                if (timer != 0) {
                    timer--;
                    startTimer();
                } else {
                    if(!playerWin)
                        endGame("lose", timer);
                }
            }
        }, 1000);

    }

    private void tick(int seconds) {
        ((TextView) findViewById(R.id.timer)).setText("" + seconds);
    }

    public void gameEndDialog(String winOrLose){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game End")
        .setMessage("You " + winOrLose+"!\nPlay again?")
        .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                restartGame();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exitGame();
            }
        });
        this.dialog = builder.show();
    }

    private void exitGame(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void restartGame(){
        Intent intent = new Intent(this, DifficultActivity.class);
        intent.putExtra(NAME, playerName);
        intent.putExtra(BIRTHDAY, birthday);
        startActivity(intent);;
    }

    private void handleRotation(boolean enable) {
        if (!enable) {
            drawBord(R.drawable.question_mark);
            setEnableAll(false);
        }

        if (enable) {
            drawBord();
            setEnableAll(true);
        }
    }

    public void endGame(String winOrLose ,int time) {
        if (winOrLose.equals("win")) {
            Score newScore = new Score(calculateScore(time), this.difficult, this.playerName );
            newScore.setPlayerLocation(playerLocation.getCurrentLocation());
            MainActivity.scoreTable.updateScoreTable(newScore);
            MainActivity.scoreTable.saveTableToMemory();
            gameEndDialog(winOrLose);
        }
        else {
            explosionField = ExplosionField.attach2Window(this);
            explosionField.explode(gameGrid);
            gameEndDialog(winOrLose);
        }
    }

    private int calculateScore(int timeLeft) {
        switch (this.difficult) {
            case 1:
                return timeLeft*100;
            case 2:
                return timeLeft*200;
            case 3:
                return timeLeft*400;
        }
        return 0;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            binder = (RotationService.SensorServiceBinder) service;
            isServiceBound = true;
            notifyBoundService(RotationService.SensorServiceBinder.START_LISTENING);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;

        }

        void notifyBoundService(String massageFromActivity) {
            if (isServiceBound && binder != null) {
                binder.notifyService(massageFromActivity);
            }
        }
    };

    // Handling the received Intents for the "my-integer" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //          // Extract data included in the Intent
            boolean state = intent.getBooleanExtra(RotationService.SENSOR_SERVICE_VALUES_KEY, true);
            Log.d("on recive from srervice", "onReceive: " + state);
            handleRotation(state);
        }
    };
}

