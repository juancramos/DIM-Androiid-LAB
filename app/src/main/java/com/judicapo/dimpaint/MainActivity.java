package com.judicapo.dimpaint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;

public class MainActivity extends AppCompatActivity {

    UserPaint userPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userPaint = findViewById(R.id.userPaint);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        userPaint.init(metrics);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_design:
                userPaint.clear();
                if (checked)
                    userPaint.viewType = ViewType.DESIGN;
                    break;
            case R.id.radio_vector:
                userPaint.clear();
                if (checked)
                    userPaint.viewType = ViewType.VECTOR;
                    break;
            case R.id.radio_scalar:
                userPaint.clear();
                if (checked)
                    userPaint.viewType = ViewType.SCALAR;
                break;
            case R.id.radio_timer:
                if (checked) {
                    userPaint.viewType = ViewType.TIMER;
                    userPaint.startTimer();
                }
                break;
        }
    }
}
