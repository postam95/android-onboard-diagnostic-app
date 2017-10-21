package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.statics.Common;

public class AddingCarActivity extends AppCompatActivity {

    private EditText brand;
    private EditText type;
    private EditText licenseNumber;
    private AppCompatActivity thisOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_car);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        thisOne = this;

        brand = (EditText) findViewById(R.id.editTextBrand);
        type = (EditText) findViewById(R.id.editTextType);
        licenseNumber = (EditText) findViewById(R.id.editTextLicenseNumber);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (brand.getText().toString().trim().equals("") || type.getText().toString().trim().equals("") || licenseNumber.toString().trim().equals("")) {
                    Snackbar.make(view,"Fill in the form completely!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Car car = new Car();
                    car.setPerson(Common.CommonInformations.userLoggedIn);
                    car.setBrand(brand.getText().toString().trim());
                    car.setType(type.getText().toString().trim());
                    car.setLicenseNumber(licenseNumber.getText().toString().trim());

                    DatabaseManager.getInstance().addCar(car);

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
