package hu.unideb.inf.obdbypm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import hu.unideb.inf.obdbypm.activities.FaultCodesActivity;
import hu.unideb.inf.obdbypm.activities.LiveDataActivity;
import hu.unideb.inf.obdbypm.activities.HeadupDisplayActivity;
import hu.unideb.inf.obdbypm.activities.RegistrationActivity;
import hu.unideb.inf.obdbypm.activities.ServiceBookActivity;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.Person;
import hu.unideb.inf.obdbypm.statics.Common;

import static hu.unideb.inf.obdbypm.obd.Connection.deviceAddress;
import static hu.unideb.inf.obdbypm.obd.Connection.showAndGetPairedBluetoothDevices;
import static hu.unideb.inf.obdbypm.obd.Connection.turnOffBluetooth;
import static hu.unideb.inf.obdbypm.obd.Connection.turnOnBluetooth;

public class MainActivity extends AppCompatActivity {
    private ImageButton loginButton;
    private TextView welcomeMessage;
    private TextView tvEmail;
    private TextView tvPassword;
    private EditText email;
    private EditText password;
    private TextView bluetoothMessage;
    private BluetoothAdapter btAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager.init(this);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        loginButton = (ImageButton) findViewById(R.id.loginBtn);
        welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        tvEmail = (TextView) findViewById(R.id.textviewEmail);
        tvPassword = (TextView) findViewById(R.id.textviewPassword);
        bluetoothMessage = (TextView) findViewById(R.id.bluetoothMessage);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

    }

    protected void onStart() {
        //SECOND
        super.onStart();
    }

    @Override
    protected void onResume() {
        //LAST - ALWAYS RUNNING
        super.onResume();
        if (Common.CommonInformations.userLoggedIn == null)
        {
            loginButton.setImageResource(R.drawable.login_image);
            welcomeMessage.setVisibility(View.GONE);
            tvPassword.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
        }
        else
        {
            loginButton.setImageResource(R.drawable.logout_image);
            welcomeMessage.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
        }

        if (btAdapter.isEnabled()) {
            bluetoothMessage.setText("Bluetooth is enabled");
            bluetoothMessage.setTextColor(Color.GREEN);
        }
        else    {
            bluetoothMessage.setText("Bluetooth is disabled");
            bluetoothMessage.setTextColor(Color.RED);
        }
    }

    public void onClickNavigateToLiveDataBtn(View v)
    {
        Intent intent = new Intent(this, LiveDataActivity.class);
        startActivity(intent);
    }

    public void onClickNavigateToFaultCodesBtn(View v)
    {
        Intent intent = new Intent(this, FaultCodesActivity.class);
        startActivity(intent);
    }

    public void onClickNavigateToHeadupDisplayBtn(View v)
    {
        Intent intent = new Intent(this, HeadupDisplayActivity.class);
        startActivity(intent);
    }

    public void onClickNavigateToServiceBookBtn(View v)
    {
        if (Common.CommonInformations.userLoggedIn == null)
            Toast.makeText(getBaseContext(), "First you have to log in!", Toast.LENGTH_SHORT).show();
        else{
            Intent intent = new Intent(this, ServiceBookActivity.class);
            startActivity(intent);
        }
    }

    public void onClickNavigateToRegistrationBtn(View v)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }


    public void onClickLoginBtn(View v)
    {
        List<Person> users = DatabaseManager.getInstance().getAllPersons();
        if (users.size() == 0){
            Toast.makeText(getApplicationContext(),
                    "Wrong e-mail address or password!" , Toast.LENGTH_LONG)
                    .show();
            return;
        }


        if (Common.CommonInformations.userLoggedIn == null)
        {

            for (int i = 0; i < users.size(); i++)
            {
                if (users.get(i).getEmailAddress().equals(email.getText().toString().trim()) &&
                        users.get(i).getPassword().equals(password.getText().toString().trim()))
                {
                    Common.CommonInformations.userLoggedIn = users.get(i);
                    welcomeMessage.setText("Welcome to the OBDIIbyPM App, " + users.get(i).getName() + "!");
                    loginButton.setImageResource(R.drawable.logout_image);
                    welcomeMessage.setVisibility(View.VISIBLE);
                    tvPassword.setVisibility(View.GONE);
                    tvEmail.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),
                            "You have logged in!" , Toast.LENGTH_LONG)
                            .show();
                    return;
                }

            }
        }
        else
        {
            Common.CommonInformations.userLoggedIn = null;
            loginButton.setImageResource(R.drawable.login_image);
            welcomeMessage.setVisibility(View.GONE);
            tvPassword.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);

            Toast.makeText(getApplicationContext(),
                    "You have logged out!" , Toast.LENGTH_LONG)
                    .show();
            return;
        }

        Toast.makeText(getApplicationContext(),
                "Wrong e-mail address or password!" , Toast.LENGTH_LONG)
                .show();
        return;

    }

    public void onClickBluetoothBtn(View v)
    {
        if (btAdapter.isEnabled())  {
            turnOffBluetooth();
            Toast.makeText(getBaseContext(), "Bluetooth has been turned off!", Toast.LENGTH_SHORT).show();
            bluetoothMessage.setText("Bluetooth is disabled");
            bluetoothMessage.setTextColor(Color.RED);
        }
        else    {
            turnOnBluetooth();
            Toast.makeText(getBaseContext(), "Bluetooth has been turned on!", Toast.LENGTH_SHORT).show();
            bluetoothMessage.setText("Bluetooth is enabled");
            bluetoothMessage.setTextColor(Color.GREEN);
        }
    }

    public void onClickNavigateToBluetoothChooseBtn(View v)
    {
        if (btAdapter.isEnabled()) {
            showAndGetPairedBluetoothDevices(this);
        }
        else
            Toast.makeText(getApplicationContext(),
                    "First you should turn on the bluetooth!" , Toast.LENGTH_LONG)
                    .show();

    }

}
