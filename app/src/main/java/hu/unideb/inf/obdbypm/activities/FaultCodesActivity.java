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
    private static boolean isRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fault_codes);

        if (!isRead)
            LoadText(R.raw.fault_codes);

        faultCodes = new ArrayList<String>();
        faultCodes.add("P0001");
        faultCodes.add("P0002");
        faultCodes.add("P0003");
        faultCodes.add("P0004");
        faultCodes.add("P0005");
        faultCodes.add("P0006");
        faultCodes.add("P0007");
        faultCodes.add("P0008");
        faultCodes.add("P0010");
        faultCodes.add("P0011");
        faultCodes.add("P0012");
        faultCodes.add("P0013");
        faultCodes.add("P0014");
        faultCodes.add("P0015");
        faultCodes.add("P0016");
        faultCodes.add("P0017");

        listView = (ListView) findViewById(R.id.list);

        // ListView Item Click Listener
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
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();

            }

        });

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
                        faultCodesMap.get(faultCodes.get(position)) , Toast.LENGTH_LONG)
                        .show();

            }

        });
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
