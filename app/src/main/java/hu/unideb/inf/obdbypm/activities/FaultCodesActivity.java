package hu.unideb.inf.obdbypm.activities;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.ResetTroubleCodesCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnableToConnectException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.UUID;

import hu.unideb.inf.obdbypm.MainActivity;
import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.obd.Connection;

public class FaultCodesActivity extends AppCompatActivity {
    private ArrayList<String> faultCodes;
    ArrayAdapter<String> adapter;
    private HashMap<String, String> faultCodesMap;
    private ListView listView;
    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;
    public String deviceAddress;
    private GetTroubleCodesTask gtct;
    private ClearTroubleCodesTask ctct;
    private String faultCodesInString = null;
    private ProgressBar progressBar;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int NO_BLUETOOTH_DEVICE_SELECTED = 0;
    private static final int CANNOT_CONNECT_TO_DEVICE = 1;
    private static final int NO_DATA = 3;
    private static final int DATA_OK = 4;
    private static final int CLEAR_DTC = 5;
    private static final int OBD_COMMAND_FAILURE = 10;
    private static final int OBD_COMMAND_FAILURE_IO = 11;
    private static final int OBD_COMMAND_FAILURE_UTC = 12;
    private static final int OBD_COMMAND_FAILURE_IE = 13;
    private static final int OBD_COMMAND_FAILURE_MIS = 14;
    private static final int OBD_COMMAND_FAILURE_NODATA = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault_codes);

        progressBar = (ProgressBar) findViewById(R.id.activityIndicator);

        if (faultCodesMap == null)
            LoadText(R.raw.fault_codes);

        gtct = new GetTroubleCodesTask();
        gtct.execute(Connection.deviceAddress);

    }

    private void refreshListview()  {
        faultCodes = new ArrayList<>();
        convertFaultCodesFromStringToList(faultCodesInString);
        listView = (ListView) findViewById(R.id.list);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, faultCodes);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition     = position;
                String  itemValue    = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        getFaultCodeDescription(faultCodes.get(position)) , Toast.LENGTH_LONG)
                        .show();
            }

        });
    }

    private void clearListview()  {
        faultCodes = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.list);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, faultCodes);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition     = position;
                String  itemValue    = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        getFaultCodeDescription(faultCodes.get(position)) , Toast.LENGTH_LONG)
                        .show();
            }

        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fault_codes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.clearFaultCodes) {
            ctct = new ClearTroubleCodesTask();
            ctct.execute();
            return true;
        }
        else if (item.getItemId() == R.id.getFaultCodes) {
            gtct = new GetTroubleCodesTask();
            gtct.execute();
            return true;
        }
        else if (item.getItemId() == R.id.chooseBluetooth){
            Connection.showAndGetPairedBluetoothDevices(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private Handler mHandler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case NO_BLUETOOTH_DEVICE_SELECTED:
                    Toast.makeText(getBaseContext(), "Error! No bluetooth device selected!", Toast.LENGTH_SHORT).show();
                    break;
                case CANNOT_CONNECT_TO_DEVICE:
                    Toast.makeText(getBaseContext(), "Error! Problem occurred during the connection!", Toast.LENGTH_SHORT).show();
                    break;
                case OBD_COMMAND_FAILURE:
                    Toast.makeText(getBaseContext(), "Error! Problem occurred during the communication! Maybe some fault codes are still active!", Toast.LENGTH_SHORT).show();
                    break;
                case OBD_COMMAND_FAILURE_IO:
                case OBD_COMMAND_FAILURE_IE:
                case OBD_COMMAND_FAILURE_MIS:
                case OBD_COMMAND_FAILURE_UTC:
                    Toast.makeText(getBaseContext(), "Error! Problem occurred during the communication!", Toast.LENGTH_SHORT).show();
                    break;
                case OBD_COMMAND_FAILURE_NODATA:
                case NO_DATA:
                    Toast.makeText(getBaseContext(), "No fault codes found!", Toast.LENGTH_SHORT).show();
                    break;
                case DATA_OK:
                    Toast.makeText(getBaseContext(), "Successful data downloading!", Toast.LENGTH_SHORT).show();
                    refreshListview();
                    break;

            }
            return true;
        }
    });

    private String getFaultCodeDescription(String code)   {
        String codeDesc = faultCodesMap.get(code);

        if (codeDesc != null)
            return codeDesc;
        else if (code.startsWith("P1") ||
                code.startsWith("P30") || code.startsWith("P33"))
            return "Manufacturer-specific powertrain issue";
        else if (code.startsWith("C1") || code.startsWith("C2"))
            return "Manufacturer-specific chassis issue";
        else if (code.startsWith("B1") || code.startsWith("B2"))
            return "Manufacturer-specific body issue";
        else if (code.startsWith("U1") || code.startsWith("U2"))
            return "Manufacturer-specific network communication issue";
        else
            return "Unknown issue";
    }


    public void LoadText(int resourceId) {
        InputStream is = this.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        faultCodesMap = new HashMap<String, String>();

        try {
            while ((readLine = br.readLine()) != null) {
                String[] splits = readLine.split(" ", 2);
                faultCodesMap.put(splits[0], splits[1]);
                Log.d("TEXT", readLine);
            }

            is.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void convertFaultCodesFromStringToList(String faulCodesInString) {
        String[] fragments = faulCodesInString.split("\n");
        for (String s: fragments)
            faultCodes.add(s);
    }


    private class ClearTroubleCodesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            synchronized (this) {
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                device = btAdapter.getRemoteDevice(Connection.deviceAddress);
                btAdapter.cancelDiscovery();
                socket = null;

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                    ResetTroubleCodesCommand clear = new ResetTroubleCodesCommand();
                    clear.run(socket.getInputStream(), socket.getOutputStream());
                    String resultClear = clear.getFormattedResult();
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IO).sendToTarget();
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IE).sendToTarget();
                    return null;
                } catch (UnableToConnectException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_UTC).sendToTarget();
                    return null;
                } catch (MisunderstoodCommandException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_MIS).sendToTarget();
                    return null;
                } catch (NoDataException e) {
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_NODATA).sendToTarget();
                    return null;
                } catch (Exception e) {
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE).sendToTarget();
                } finally {
                    closeSocket(socket);
                }

            }

            return result;
        }

        public void closeSocket(BluetoothSocket sock) {
            if (sock != null)
                try {
                    sock.close();
                } catch (IOException e) {}
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("")){
                faultCodesInString = result;
                mHandler.obtainMessage(DATA_OK).sendToTarget();
            }
            else    {
                clearListview();
            }
            setContentView(R.layout.activity_fault_codes);
        }
    }

    private class GetTroubleCodesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            synchronized (this) {
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                device = btAdapter.getRemoteDevice(Connection.deviceAddress);
                btAdapter.cancelDiscovery();
                socket = null;

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                    ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                    tcoc.run(socket.getInputStream(), socket.getOutputStream());
                    result = tcoc.getFormattedResult();
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IO).sendToTarget();
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IE).sendToTarget();
                    return null;
                } catch (UnableToConnectException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_UTC).sendToTarget();
                    return null;
                } catch (MisunderstoodCommandException e) {
                    e.printStackTrace();
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_MIS).sendToTarget();
                    return null;
                } catch (NoDataException e) {
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_NODATA).sendToTarget();
                    return null;
                } catch (Exception e) {
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE).sendToTarget();
                } finally {
                    closeSocket(socket);
                }

            }

            return result;
        }

        public void closeSocket(BluetoothSocket sock) {
            if (sock != null)
                try {
                    sock.close();
                } catch (IOException e) {}
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("")){
                faultCodesInString = result;
                mHandler.obtainMessage(DATA_OK).sendToTarget();
            }
            else    {
                clearListview();
            }
            setContentView(R.layout.activity_fault_codes);
        }
    }

    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }
}
