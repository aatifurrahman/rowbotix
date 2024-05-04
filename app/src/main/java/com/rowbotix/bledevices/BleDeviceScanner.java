package com.rowbotix.bledevices;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble3.RxBleClient;
import com.polidea.rxandroidble3.scan.ScanSettings;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class BleDeviceScanner {
    private static final String TAG = "BluetoothScanner";

    private final RxBleClient rxBleClient;
    private Disposable scanDisposable;

    public BleDeviceScanner(RxBleClient rxBleClient) {
        this.rxBleClient = rxBleClient;
    }

    public void startScan(BluetoothScanCallback callback) {
        scanDisposable = rxBleClient.scanBleDevices(
                        new ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                                .build()
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(callback::onTimeOut)
                .subscribe(
                        scanResult -> {
                            BluetoothDevice device = scanResult.getBleDevice().getBluetoothDevice();
                            callback.onDeviceFound(device);
                        },
                        throwable -> {
                            Log.e(TAG, "Scan error: " + throwable.getMessage());
                            callback.onError(throwable);
                        }
                );
        // Stop scanning after a duration (e.g., 10 seconds)
        Observable.timer(10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aLong -> callback.onTimeOut(),
                        Throwable::printStackTrace
                );
    }

    public void stopScan() {
        if (scanDisposable != null && !scanDisposable.isDisposed()) {
            scanDisposable.dispose();
        }
    }

    public interface BluetoothScanCallback {
        void onDeviceFound(BluetoothDevice device);

        void onError(Throwable throwable);

        void onTimeOut();
    }
}
