package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.adapters.ExpandableListAdapter;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;

public class ServiceBookActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ExpandableListView listView;
    private TextView notice;
    private List<Car> cars;
    private ExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_book);

        DatabaseManager.init(this);

        notice = (TextView)findViewById(R.id.notice);
        listView = (ExpandableListView)findViewById(R.id.list_item);
        progressBar = (ProgressBar)findViewById(R.id.progress);

        cars = new ArrayList<>();
        //set data to views
        adapter = new ExpandableListAdapter(this, cars);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Main", "resume");
        getDataFromDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent i = new Intent(this, AddingActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getDataFromDB() {
        if (cars != null)
            cars.clear();

        //get all data to Lists
        ArrayList<Car> catArrayList = DatabaseManager.getInstance().getAllCars();
        for (int i = 0; i < catArrayList.size(); i++) {
            cars.add(catArrayList.get(i));
        }

        if (cars.size() == 0) {
            //no data in database
            listView.setVisibility(View.GONE);
            notice.setText("Your service book is empty!");
            notice.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}