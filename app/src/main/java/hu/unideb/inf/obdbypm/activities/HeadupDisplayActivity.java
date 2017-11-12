package hu.unideb.inf.obdbypm.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HeadupDisplayActivity extends AppCompatActivity {
    private ArrayList<ObdCommandTask> livedataObdCommandList;
    private BluetoothDevice device = null;
    private TextView currentSpeed;
    private TextView revCounter;
    private TextView consumption;
    private TextView oilTemp;
    private TextView fuelLevel;
    private static boolean isRunning;
    Observable<ObdCommandResult> observable;
    Observer<ObdCommandResult> observer;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_headup_display);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.containerHUD);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        this.mContentView.setScaleX(-1);

        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        revCounter = (TextView) findViewById(R.id.revCounter);
        fuelLevel = (TextView) findViewById(R.id.fuelLevel);
        consumption = (TextView) findViewById(R.id.consumption);
        oilTemp = (TextView) findViewById(R.id.oilTemp);

        initList();
    }

    private void initList() {
        livedataObdCommandList = new ArrayList<ObdCommandTask>();
        livedataObdCommandList.add(new ObdCommandTask(1, new SpeedCommand()));
        livedataObdCommandList.add(new ObdCommandTask(2, new RPMCommand()));
        livedataObdCommandList.add(new ObdCommandTask(3, new ConsumptionRateCommand()));
        livedataObdCommandList.add(new ObdCommandTask(4, new FuelLevelCommand()));
        livedataObdCommandList.add(new ObdCommandTask(5, new OilTempCommand()));
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
                socket = null;
                socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                socket.connect();

                new ObdResetCommand().run(socket.getInputStream(), socket.getOutputStream());
                new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                ObdCommandResult obdCommandResult = new ObdCommandResult();
                while (!Thread.currentThread().isInterrupted()) {
                    for (int i = 1; i < 6; i++) {
                        try {
                            livedataObdCommandList.get(i-1).getCommand().run(socket.getInputStream(), socket.getOutputStream());
                            obdCommandResult.setId(i);
                            obdCommandResult.setValue(livedataObdCommandList.get(i-1).getCommand().getFormattedResult());
                            e.onNext(obdCommandResult);
                        } catch (UnsupportedCommandException ex) {
                        } catch (MisunderstoodCommandException exx) {
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
            public void onSubscribe(Disposable d) {
            }

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
                        consumption.setText(value.getValue());
                        break;
                    case 4:
                        fuelLevel.setText(value.getValue());
                        break;
                    case 5:
                        oilTemp.setText(value.getValue());
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Override
    protected void onPause() {
        isRunning = false;
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
