package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import hu.unideb.inf.obdbypm.R;

public class FaultCodesActivity extends AppCompatActivity {
    private ArrayList<String> faultCodes;
    private HashMap<String, String> faultCodesMap;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault_codes);

        //Read from file
        if (faultCodesMap == null)
            LoadText(R.raw.fault_codes);

        //Init fault codes for testing
        faultCodes = new ArrayList<String>();
        faultCodes.add("P0001");
        faultCodes.add("P1002");
        faultCodes.add("C0003");
        faultCodes.add("C1004");
        faultCodes.add("B0005");
        faultCodes.add("B2006");
        faultCodes.add("U0007");
        faultCodes.add("U1008");
        faultCodes.add("Z0010");

        listView = (ListView) findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
}
