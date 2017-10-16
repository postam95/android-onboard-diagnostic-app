package hu.unideb.inf.obdbypm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import hu.unideb.inf.obdbypm.activities.FaultCodesActivity;
import hu.unideb.inf.obdbypm.activities.LiveDataActivity;
import hu.unideb.inf.obdbypm.activities.HeadupDisplayActivity;
import hu.unideb.inf.obdbypm.activities.RegistrationActivity;
import hu.unideb.inf.obdbypm.activities.ServiceBookActivity;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Car;
import hu.unideb.inf.obdbypm.models.Person;
import hu.unideb.inf.obdbypm.statics.Common;

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
                    welcomeMessage.setText("Welcome to the OBDII Application, " + users.get(i).getName() + "!");
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
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Wrong e-mail address or password!" , Toast.LENGTH_LONG)
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

    }

    public void onClickNavigateToBluetoothChooseBtn(View v)
    {
        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                //String deviceAddress = devices.get(position);
                // TODO save deviceAddress
            }
        });

        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

}
