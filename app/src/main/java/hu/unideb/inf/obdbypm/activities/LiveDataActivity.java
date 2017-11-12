package hu.unideb.inf.obdbypm.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnableToConnectException;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.util.ArrayList;

import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.obd.Connection;
import hu.unideb.inf.obdbypm.obd.ObdCommandResult;
import hu.unideb.inf.obdbypm.obd.ObdCommandTask;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static hu.unideb.inf.obdbypm.activities.FaultCodesActivity.uuid;
import static hu.unideb.inf.obdbypm.obd.Connection.socket;

public class LiveDataActivity extends AppCompatActivity {
    private TextView currentSpeed;
    private TextView revCounter;
    private TextView throttlePosition;
    private TextView oilTemp;
    private TextView fuelLevel;
    private TextView engineCoolant;
    private static BluetoothDevice device = null;
    private static ArrayList<ObdCommandTask> livedataObdCommandList;
    private static boolean isRunning;
    Observable<ObdCommandResult> observable;
    Observer<ObdCommandResult> observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);

        currentSpeed = (TextView) findViewById(R.id.txtCurrentSpeed);
        revCounter = (TextView) findViewById(R.id.txtRevCounter);
        throttlePosition = (TextView) findViewById(R.id.txtThrottlePosition);
        oilTemp = (TextView) findViewById(R.id.txtOilTemp);
        fuelLevel = (TextView) findViewById(R.id.txtFuelLevel);
        engineCoolant = (TextView) findViewById(R.id.txtEngineCoolant);

        initList();
    }

    private void initList() {
        livedataObdCommandList = new ArrayList<ObdCommandTask>();
        livedataObdCommandList.add(new ObdCommandTask(1, new SpeedCommand()));
        livedataObdCommandList.add(new ObdCommandTask(2, new RPMCommand()));
        livedataObdCommandList.add(new ObdCommandTask(3, new ThrottlePositionCommand()));
        livedataObdCommandList.add(new ObdCommandTask(4, new OilTempCommand()));
        livedataObdCommandList.add(new ObdCommandTask(5, new FuelLevelCommand()));
        livedataObdCommandList.add(new ObdCommandTask(6, new EngineCoolantTemperatureCommand()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;

        observable = Observable.create(new ObservableOnSubscribe<ObdCommandResult>() {

            @Override
            public void subscribe(ObservableEmitter<ObdCommandResult> e) throws Exception {

                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                device = btAdapter.getRemoteDevice(Connection.deviceAddress);
                socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                socket.connect();

                new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());
                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                ObdCommandResult obdCommandResult = new ObdCommandResult();

                while (!Thread.currentThread().isInterrupted()) {
                    for (int i = 1; i < 5; i++) {
                        try {
                            livedataObdCommandList.get(i-1).getCommand().run(socket.getInputStream(), socket.getOutputStream());
                            obdCommandResult.setId(i);
                            obdCommandResult.setValue(livedataObdCommandList.get(i-1).getCommand().getFormattedResult());
                            e.onNext(obdCommandResult);
                        } catch (UnsupportedCommandException uce) {
                        } catch (MisunderstoodCommandException mce) {
                        } catch (NoDataException nde) {
                        } catch (UnableToConnectException utce) {
                            e.onError(utce);
                        } catch (InterruptedException ie) {
                            e.onError(ie);
                        } finally {
                            if (!isRunning)
                                socket.close();
                        }
                    }
                }
            }
        });

        observer = new Observer<ObdCommandResult>() {

            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(ObdCommandResult value) {
                switch (value.getId())  {
                    case 1:
                        currentSpeed.setText(value.getValue());
                        break;
                    case 2:
                        revCounter.setText(value.getValue());
                        break;
                    case 3:
                        throttlePosition.setText(value.getValue());
                        break;
                    case 4:
                        oilTemp.setText(value.getValue());
                        break;
                    case 5:
                        fuelLevel.setText(value.getValue());
                        break;
                    case 6:
                        engineCoolant.setText(value.getValue());
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getBaseContext(), "Connection has been closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
            }

        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_data, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.startLiveData) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        isRunning = false;
        super.onPause();
    }

}