package hu.unideb.inf.obdbypm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.ServiceBookRecord;
import hu.unideb.inf.obdbypm.statics.Common;

public class AddingActivity extends AppCompatActivity {

    private View btnAddCar;
    private View btnAddServiceBookRecord;
    private View btnOK;
    private Spinner spinner;
    private ViewGroup layoutAddCar;
    private ViewGroup layoutAddServiceBookRecord;
    private ViewGroup layoutButtons;
    private EditText editCar;
    private EditText editServiceBookRecord;
    private View btnCancel;
    private List<Car> cars;
    private boolean havingCar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        btnAddCar = findViewById(R.id.btn_add_car);
        btnOK = findViewById(R.id.btn_ok);
        layoutAddServiceBookRecord = (ViewGroup) findViewById(R.id.ll_ser);
        layoutAddCar = (ViewGroup) findViewById(R.id.ll_car);
        layoutButtons = (ViewGroup) findViewById(R.id.ll_buttons);
        btnCancel = findViewById(R.id.btn_cancel);
        spinner = (Spinner) findViewById(R.id.spinner);
        btnAddServiceBookRecord = findViewById(R.id.btn_add_service_book_record);
        editCar = (EditText) findViewById(R.id.txt_name);
        editServiceBookRecord = (EditText) findViewById(R.id.txt_service_book_record_name);

        btnAddServiceBookRecord.setOnClickListener(onAddServiceBookRecordListener());
        btnAddCar.setOnClickListener(onAddCarListner());
        btnCancel.setOnClickListener(onCancelListener());
        btnOK.setOnClickListener(onConfirmListener());
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

    private View.OnClickListener onConfirmListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutAddCar.getVisibility() == View.VISIBLE) {
                    if (editCar.getText().toString().trim().equals("")) {
                        Toast.makeText(getBaseContext(), "Please input car name", Toast.LENGTH_SHORT).show();
                    } else {
                        Car car = new Car();
                        car.setName(editCar.getText().toString().trim());
                        car.setPerson(Common.CommonInformations.userLoggedIn);

                        //save new object to db
                        DatabaseManager.getInstance().addCar(car);
                    }
                } else if (layoutAddServiceBookRecord.getVisibility() == View.VISIBLE) {
                    if (editServiceBookRecord.getText().toString().trim().equals("")) {
                        Toast.makeText(getBaseContext(), "Please input service book record name", Toast.LENGTH_SHORT).show();
                    } else {
                        ServiceBookRecord serviceBookRecord = new ServiceBookRecord();
                        Car car = (Car) spinner.getSelectedItem();
                        car.setPerson(Common.CommonInformations.userLoggedIn);
                        serviceBookRecord.setName(editServiceBookRecord.getText().toString().trim());
                        serviceBookRecord.setCar(car);


                        //save to database
                        DatabaseManager.getInstance().newServiceBookRecordAppend(serviceBookRecord);
                        DatabaseManager.getInstance().updateServiceBookRecord(serviceBookRecord);
                    }
                }
                goneLayouts();
            }
        };
    }

    private View.OnClickListener onCancelListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goneLayouts();
            }
        };
    }

    private View.OnClickListener onAddCarListner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goneLayouts();
                layoutAddCar.setVisibility(View.VISIBLE);
                layoutButtons.setVisibility(View.VISIBLE);
            }
        };
    }

    private View.OnClickListener onAddServiceBookRecordListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!havingCar) {
                    Toast.makeText(getBaseContext(), "None car in DB, please add car first", Toast.LENGTH_SHORT).show();
                } else {
                    goneLayouts();
                    List<Car> allCars = DatabaseManager.getInstance().getAllCars();
                    cars = new ArrayList<Car>();
                    for (int i = 0; i < allCars.size(); i++)
                        if (Common.CommonInformations.userLoggedIn != null && allCars.get(i).getPerson().getId() == Common.CommonInformations.userLoggedIn.getId() )
                            cars.add(allCars.get(i));

                    if (cars.size() == 0) {
                        havingCar = false;
                    } else {
                        //set spinner adapter
                        ArrayAdapter<Car> adapter = new ArrayAdapter<>(AddingActivity.this,
                                android.R.layout.simple_dropdown_item_1line, cars);
                        spinner.setAdapter(adapter);
                    }
                    layoutAddServiceBookRecord.setVisibility(View.VISIBLE);
                    layoutButtons.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void goneLayouts() {
        layoutAddCar.setVisibility(View.GONE);
        layoutAddServiceBookRecord.setVisibility(View.GONE);
        layoutButtons.setVisibility(View.GONE);
        editCar.setText("");
        editServiceBookRecord.setText("");
    }
}