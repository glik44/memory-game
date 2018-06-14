package com.afeka.tomergliksman.memoryGame;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afeka.tomergliksman.memoryGame.classes.ScoreTable;
import static com.afeka.tomergliksman.memoryGame.Strings.NAME;
import static com.afeka.tomergliksman.memoryGame.Strings.BIRTHDAY;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Calendar myCalendar;
    private EditText edittext;
    public static ScoreTable scoreTable;
    private Application application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCalendar = Calendar.getInstance();

        edittext= (EditText) findViewById(R.id.birthday);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        scoreTable = new ScoreTable(this);
        application = getApplication();
        application.registerActivityLifecycleCallbacks(new AppLifecycleTracker());
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public void startGame(View view){
        Intent intent = new Intent(this, DifficultActivity.class);
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String date = ((EditText)findViewById(R.id.birthday)).getText().toString();
        intent.putExtra(NAME, name);
        intent.putExtra(BIRTHDAY, date);
        //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void showScoreTable(View view)
    {
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

    private static class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {
        private static final String TAG = AppLifecycleTracker.class.getSimpleName();
        private int numStarted = 0;
        private boolean isApplicationInForeground;

        public AppLifecycleTracker() {
            Log.d(TAG, " in my ciostructor");
        }

        @Override
        public void onActivityStarted(Activity activity) {

            if (numStarted++ == 0) {
                isApplicationInForeground = false;
                Log.d(TAG, "onActivityStopped: app went to foreground");
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (--numStarted == 0) {
                isApplicationInForeground = false;
                Log.d(TAG, "onActivityStopped: app went to background");
                scoreTable.saveTableToMemory();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }
    }

}
