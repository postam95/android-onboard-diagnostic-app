package hu.unideb.inf.obdbypm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hu.unideb.inf.obdbypm.R;

public class LiveDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
    }

    @Override
    protected void onStart() {
        //SECOND
        super.onStart();
    }

    @Override
    protected void onResume() {
        //LAST - ALWAYS RUNNING
        super.onResume();
    }
}
