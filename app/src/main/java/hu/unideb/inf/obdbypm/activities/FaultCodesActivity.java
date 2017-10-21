package hu.unideb.inf.obdbypm.activities;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
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
import java.util.UUID;

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
    private String faultCodesInString = null;
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

        //Read from file
        if (faultCodesMap == null)
            LoadText(R.raw.fault_codes);

        //Init fault codes for testing
        faultCodes = new ArrayList<String>();

        listView = (ListView) findViewById(R.id.list);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, faultCodes);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        getFaultCodeDescription(faultCodes.get(position)) , Toast.LENGTH_LONG)
                        .show();

            }

        });



        gtct = new GetTroubleCodesTask();
        gtct.execute(Connection.deviceAddress);

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
        // The InputStream opens the resourceId and sends it to the buffer
        InputStream is = this.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        faultCodesMap = new HashMap<String, String>();

        try {
            // While the BufferedReader readLine is not null
            while ((readLine = br.readLine()) != null) {
                String[] splits = readLine.split(" ", 2);
                faultCodesMap.put(splits[0], splits[1]);
                Log.d("TEXT", readLine);
            }

            // Close the InputStream and BufferedReader
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
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
    }

    private class GetTroubleCodesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            //TODO
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            //Get the current thread's token
            synchronized (this) {
                //Log.d(TAG, "Starting service..");
                // get the remote Bluetooth device

                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                device = btAdapter.getRemoteDevice(Connection.deviceAddress);

                //Log.d(TAG, "Stopping Bluetooth discovery.");
                btAdapter.cancelDiscovery();

                //Log.d(TAG, "Starting OBD connection..");

                // Instantiate a BluetoothSocket for the remote device and connect it.
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                socket = null;

                try {
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    // Let's configure the connection.
                    //Log.d(TAG, "Queueing jobs for connection configuration..");

                    //onProgressUpdate(1);

                    new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());


                    //onProgressUpdate(2);

                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());

                    //onProgressUpdate(3);

                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());

                    //onProgressUpdate(4);

                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());

                    //onProgressUpdate(5);

                    ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                    tcoc.run(socket.getInputStream(), socket.getOutputStream());
                    result = tcoc.getFormattedResult();

                    //onProgressUpdate(6);

                } catch (IOException e) {
                    e.printStackTrace();
                    //Log.e("DTCERR", e.getMessage());
                    //mHandler.obtainMessage(OBD_COMMAND_FAILURE_IO).sendToTarget();
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //Log.e("DTCERR", e.getMessage());
                    //mHandler.obtainMessage(OBD_COMMAND_FAILURE_IE).sendToTarget();
                    return null;
                } catch (UnableToConnectException e) {
                    e.printStackTrace();
                    //Log.e("DTCERR", e.getMessage());
                    //mHandler.obtainMessage(OBD_COMMAND_FAILURE_UTC).sendToTarget();
                    return null;
                } catch (MisunderstoodCommandException e) {
                    e.printStackTrace();
                    //Log.e("DTCERR", e.getMessage());
                    //mHandler.obtainMessage(OBD_COMMAND_FAILURE_MIS).sendToTarget();
                    return null;
                } catch (NoDataException e) {
                    //Log.e("DTCERR", e.getMessage());
                    //mHandler.obtainMessage(OBD_COMMAND_FAILURE_NODATA).sendToTarget();
                    return null;
                } catch (Exception e) {
                    //Log.e("DTCERR", e.getMessage());
                    //mHandler.obtainMessage(OBD_COMMAND_FAILURE).sendToTarget();
                } finally {

                    // close socket
                    closeSocket(socket);
                }

            }

            return result;
        }

        public void closeSocket(BluetoothSocket sock) {
            if (sock != null)
                // close socket
                try {
                    sock.close();
                } catch (IOException e) {
                    //Log.e(TAG, e.getMessage());
                }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
            //mHandler.obtainMessage(DATA_OK, result).sendToTarget();
            faultCodesInString = result;
            setContentView(R.layout.activity_fault_codes);
            convertFaultCodesFromStringToList(result);
        }
    }

    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since this results in erroneous error codes
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }
}
