package hu.unideb.inf.obdbypm.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import hu.unideb.inf.obdbypm.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LiveDataActivity extends AppCompatActivity {
    private static String TAG = "LiveActivity";
    private TextView currentSpeed;
    private TextView revCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);

        currentSpeed = (TextView) findViewById(R.id.txtCurrentSpeed);
        revCounter = (TextView) findViewById(R.id.txtRevCounter);

        Observable.create(new ObservableOnSubscribe<Integer>() {
                        @Override
                        public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                            e.onNext(1);
                            Log.e(TAG, "na csuma!: " + Thread.currentThread().getName());
                            e.onNext(2);

                            e.onComplete();
                        }
                    })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    Observer<Integer> observer = new Observer<Integer>() {

        @Override
        public void onSubscribe(Disposable d) {
            Log.e(TAG, "onSubscribe: " + Thread.currentThread().getName());
        }

        @Override
        public void onNext(Integer value) {
            Log.e(TAG, "onNext: " + value + Thread.currentThread().getName());
            if (value == 1)
                currentSpeed.setText(value.toString());
            else
                revCounter.setText(value.toString());
        }


        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "onError: ");
        }

        @Override
        public void onComplete() {
            Log.e(TAG, "onComplete: All Done!: " + Thread.currentThread().getName());
        }

    };

    @Override
    protected void onPause() {
        super.onPause();
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
}
