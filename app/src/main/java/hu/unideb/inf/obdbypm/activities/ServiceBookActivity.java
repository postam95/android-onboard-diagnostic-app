package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.unideb.inf.obdbypm.MainActivity;
import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.adapters.ExpandableListAdapter;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.ServiceBookRecord;
import hu.unideb.inf.obdbypm.statics.Common;

public class ServiceBookActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private TextView notice;
    private List<Car> cars;
    private ExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_book);

        //set data to views
        notice = (TextView)findViewById(R.id.notice);
        listView = (ExpandableListView)findViewById(R.id.list_item);

        cars = new ArrayList<>();
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
        getMenuInflater().inflate(R.menu.menu_service_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addCar) {
            Intent i = new Intent(this, AddingCarActivity.class);
            startActivity(i);
            return true;
        }
        else if (item.getItemId() == R.id.addService) {
            Intent i = new Intent(this, AddingServiceActivity.class);
            startActivity(i);
            return true;
        }
        else if (item.getItemId() == android.R.id.home){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDataFromDB() {
        if (cars != null)
            cars.clear();

        //get all data to Lists
        ArrayList<Car> carArrayList = DatabaseManager.getInstance().getAllCars();
        for (int i = 0; i < carArrayList.size(); i++)
            if (Common.CommonInformations.userLoggedIn != null && carArrayList.get(i).getPerson().getId() == Common.CommonInformations.userLoggedIn.getId() )
                cars.add(carArrayList.get(i));

        ArrayList<ServiceBookRecord> services = DatabaseManager.getInstance().getAllServiceBookRecords();

        if (cars.size() == 0) {
            //no data in database
            listView.setVisibility(View.GONE);
            notice.setText("Your service book is empty!");
            notice.setVisibility(View.VISIBLE);
        } else {
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            notice.setVisibility(View.GONE);
        }
    }

    public void editCarNavigation(Car car) {
        Intent intent = new Intent(ServiceBookActivity.this, AddingCarActivity.class);
        Bundle b = new Bundle();
        b.putInt("key", 1); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        finish();
    }
}