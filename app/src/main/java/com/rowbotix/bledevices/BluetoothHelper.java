package com.rowbotix.bledevices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.polidea.rxandroidble3.RxBleClient;
import com.polidea.rxandroidble3.exceptions.BleScanException;

import java.util.ArrayList;

public class BluetoothHelper {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;
    private static final int REQUEST_BLUETOOTH_SCAN = 3;

    private final AppCompatActivity activity;
    private final BleDeviceScanner scanner;

    private final ArrayAdapter<BleDeviceInfo> scannedDevicesAdapter;
    private final ArrayList<BleDeviceInfo> scannedDevicesList;
    private final BluetoothAdapter bluetoothAdapter;
    private final RxBleClient rxBleClient;

    public BluetoothHelper(AppCompatActivity activity, ArrayAdapter<BleDeviceInfo> scannedDevicesAdapter, ArrayList<BleDeviceInfo> scannedDevicesList, RxBleClient rxBleClient) {
        this.activity = activity;
        this.scanner = new BleDeviceScanner(rxBleClient);
        this.scannedDevicesAdapter = scannedDevicesAdapter;
        this.scannedDevicesList = scannedDevicesList;
        this.rxBleClient = rxBleClient;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void startScan() {
        if (bluetoothAdapter == null) {
        // Device doesn't support Bluetooth
        Toast.makeText(activity, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        activity.finish();
        return;
        }
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }

        // Check for Bluetooth scan permission
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN);
            return;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Request to enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Bluetooth was successfully enabled by the user
                            startScan();
                        } else {
                            // Bluetooth enable request was canceled or failed
                            Toast.makeText(activity, "Bluetooth is required to use this app", Toast.LENGTH_SHORT).show();
                        }
                    }).launch(enableBtIntent);
            return;
        }

        // Start scanning for BLE devices
        scanner.startScan(new BleDeviceScanner.BluetoothScanCallback() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                @SuppressLint("MissingPermission")
                BleDeviceInfo deviceInfo = new BleDeviceInfo(device.getName(), device.getAddress());
                activity.runOnUiThread(() -> {
                    if (deviceInfo.getName() != null && !scannedDevicesList.contains(deviceInfo)) {
                        scannedDevicesList.add(deviceInfo);
                        //progressScan.setVisibility(View.GONE);
                        scannedDevicesAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof BleScanException) {
                    BleScanException bleScanException = (BleScanException) throwable;
                    int reason = bleScanException.getReason();
                    // Handle BLE scan exception
                } else {
                    // Handle other errors
                }
            }

            @Override
            public void onTimeOut() {
                stopScan();
            }
        });
    }

    public void stopScan() {
        scanner.stopScan();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    Toast.makeText(activity, "Location permission is required for Bluetooth scanning", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_ENABLE_BT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startScan();
                } else {
                    Toast.makeText(activity, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public BluetoothDevice getBleDevice(String address) {
        return rxBleClient.getBleDevice(address).getBluetoothDevice();
    }
}
