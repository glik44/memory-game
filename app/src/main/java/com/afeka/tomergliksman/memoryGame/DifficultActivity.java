package com.afeka.tomergliksman.memoryGame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Locale;

public class DifficultActivity extends AppCompatActivity {

    private String name;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficult);
        setTitle("Choose Level");

        Bundle extras = getIntent().getExtras();
        this.name = extras.getString("name");
        this.date = extras.getString("birthday");

        TextView my_name = findViewById(R.id.my_name);
        my_name.setText("Name: "+this.name);
        TextView my_age = findViewById(R.id.my_age);
        my_age.setText("Age: "+getAge());

    }

    private String getAge() {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        try {
            dob.setTime(sdf.parse(this.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

    public void DifficultClick(View view) {
        int difficult = 0;
        switch (view.getId()) {
            case R.id.Easy:
                difficult = 1;
                break;
            case R.id.Medium:
                difficult = 2;
                break;
            case R.id.Hard:
                difficult = 3;
                break;
        }

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("name", this.name);
        intent.putExtra("difficult", difficult);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }
}
