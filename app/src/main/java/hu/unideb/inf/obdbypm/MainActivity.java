package hu.unideb.inf.obdbypm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import static hu.unideb.inf.obdbypm.obd.Connection.connect;
import static hu.unideb.inf.obdbypm.obd.Connection.deviceAddress;
import static hu.unideb.inf.obdbypm.obd.Connection.initialize;
import static hu.unideb.inf.obdbypm.obd.Connection.showAndGetPairedBluetoothDevices;

public class MainActivity extends AppCompatActivity {
    private Button loginButton;
    private TextView welcomeMessage;
    private TextView tvEmail;
    private TextView tvPassword;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager.init(this);

        loginButton = (Button) findViewById(R.id.loginBtn);
        welcomeMessage = (TextView) findViewById(R.id.welcomeMessage);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextPassword);
        tvEmail = (TextView) findViewById(R.id.textviewEmail);
        tvPassword = (TextView) findViewById(R.id.textviewPassword);

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
            loginButton.setText("LOGIN");
            welcomeMessage.setVisibility(View.GONE);
            tvPassword.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
        }
        else
        {
            loginButton.setText("LOGOUT");
            welcomeMessage.setVisibility(View.VISIBLE);
            tvPassword.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
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
                    welcomeMessage.setText("Welcome to the OBDIIbyOPM App, " + users.get(i).getName() + "!");
                    loginButton.setText("LOGOUT");
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
            loginButton.setText("LOGIN");
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

    public void onClickConnectBtn(View v)
    {
        if (!connect())  {
            Toast.makeText(getApplicationContext(),
                    "Something went wrong during the connection." , Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (!initialize()){
            Toast.makeText(getApplicationContext(),
                    "Something went wrong during the connection." , Toast.LENGTH_LONG)
                    .show();
            return;
        }
    }

    public void onClickNavigateToBluetoothChooseBtn(View v)
    {
        showAndGetPairedBluetoothDevices(this);
    }

}
