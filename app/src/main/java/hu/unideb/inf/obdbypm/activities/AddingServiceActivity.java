package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.ServiceBookRecord;
import hu.unideb.inf.obdbypm.statics.Common;

public class AddingServiceActivity extends AppCompatActivity {

    private Spinner spinnerCars;
    private EditText serviceCompany;
    private EditText currentMileage;
    private CheckBox oilChange;
    private CheckBox chassisRepair;
    private CheckBox oilFilterChange;
    private CheckBox airFilterChange;
    private CheckBox tiresRotate;
    private CheckBox brakesAdjust;
    private CheckBox wheelsAlign;
    private CheckBox tiresReplace;
    private CheckBox transmission;
    private CheckBox engineTuneup;
    private CheckBox coolingSystem;
    private EditText comment;
    private EditText totalCost;
    private AppCompatActivity thisOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_service);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        thisOne = this;

        spinnerCars = (Spinner) findViewById(R.id.spinnerCars);
        serviceCompany = (EditText) findViewById(R.id.editTextServiceCompany);
        currentMileage = (EditText) findViewById(R.id.editTextCurrentMileage);
        oilChange = (CheckBox) findViewById(R.id.checkBoxOilChange);
        chassisRepair = (CheckBox) findViewById(R.id.checkBoxChassisRepair);
        oilFilterChange = (CheckBox) findViewById(R.id.checkBoxOilFilterChange);
        airFilterChange = (CheckBox) findViewById(R.id.checkBoxAirFilterChange);
        tiresRotate = (CheckBox) findViewById(R.id.checkBoxTiresRotate);
        brakesAdjust = (CheckBox) findViewById(R.id.checkBoxBrakeAdjust);
        wheelsAlign = (CheckBox) findViewById(R.id.checkBoxWheelsAlign);
        tiresReplace = (CheckBox) findViewById(R.id.checkBoxTiresReplace);
        transmission = (CheckBox) findViewById(R.id.checkBoxTransmission);
        engineTuneup = (CheckBox) findViewById(R.id.checkBoxEgineTuneup);
        coolingSystem = (CheckBox) findViewById(R.id.checkBoxCoolingSystem);
        comment = (EditText) findViewById(R.id.editTextComment);
        totalCost = (EditText) findViewById(R.id.editTextTotalCost);

        List<Car> allCars = DatabaseManager.getInstance().getAllCars();
        List<Car> cars = new ArrayList<Car>();
        for (int i = 0; i < allCars.size(); i++)
            if (Common.CommonInformations.userLoggedIn != null && allCars.get(i).getPerson().getId() == Common.CommonInformations.userLoggedIn.getId() )
                cars.add(allCars.get(i));

        if (cars.size() != 0) {
            //set spinner adapter
            ArrayAdapter<Car> adapter = new ArrayAdapter<>(AddingServiceActivity.this,
                    android.R.layout.simple_dropdown_item_1line, cars);
            spinnerCars.setAdapter(adapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (serviceCompany.equals("") || currentMileage.equals("") || comment.equals("") || totalCost.equals("")) {
                    Snackbar.make(view,"Fill in the form completely!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    ServiceBookRecord serviceBookRecord = new ServiceBookRecord();
                    Car car = (Car) spinnerCars.getSelectedItem();

                    try{
                        serviceBookRecord.setCurrentMileage(Integer.parseInt(currentMileage.getText().toString().trim()));
                    } catch (NumberFormatException e){
                        Snackbar.make(view,"Fill in the form correctly!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }
                    serviceBookRecord.setCar(car);
                    serviceBookRecord.setServiceDate(new Date());
                    serviceBookRecord.setServiceCompany(serviceCompany.getText().toString().trim());
                    serviceBookRecord.setAirFilterChange(airFilterChange.isChecked());
                    serviceBookRecord.setBrakesAdjust(brakesAdjust.isChecked());
                    serviceBookRecord.setChassisRepair(chassisRepair.isChecked());
                    serviceBookRecord.setEngineTuneup(engineTuneup.isChecked());
                    serviceBookRecord.setFlushCoolingSystem(coolingSystem.isChecked());
                    serviceBookRecord.setOilChange(oilChange.isChecked());
                    serviceBookRecord.setOilFilterChange(oilFilterChange.isChecked());
                    serviceBookRecord.setTiresReplace(tiresReplace.isChecked());
                    serviceBookRecord.setTiresRotate(tiresRotate.isChecked());
                    serviceBookRecord.setTransmissionFluidChange(transmission.isChecked());
                    serviceBookRecord.setWheelsAlign(wheelsAlign.isChecked());
                    serviceBookRecord.setComment(comment.getText().toString().trim());
                    serviceBookRecord.setTotalCost(totalCost.getText().toString().trim());

                    DatabaseManager.getInstance().newServiceBookRecordAppend(serviceBookRecord);
                    DatabaseManager.getInstance().updateServiceBookRecord(serviceBookRecord);


                    Intent intent = new Intent(thisOne, ServiceBookActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

