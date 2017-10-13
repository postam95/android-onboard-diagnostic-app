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

import java.util.ArrayList;
import java.util.Set;

import hu.unideb.inf.obdbypm.activities.FaultCodesActivity;
import hu.unideb.inf.obdbypm.activities.LiveDataActivity;
import hu.unideb.inf.obdbypm.activities.HeadupDisplayActivity;
import hu.unideb.inf.obdbypm.activities.RegistrationActivity;
import hu.unideb.inf.obdbypm.activities.ServiceBookActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent intent = new Intent(this, ServiceBookActivity.class);
        startActivity(intent);
    }

    public void onClickNavigateToRegistrationBtn(View v)
    {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
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
