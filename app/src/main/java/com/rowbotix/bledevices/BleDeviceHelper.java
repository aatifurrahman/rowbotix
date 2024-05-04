package com.rowbotix.bledevices;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.polidea.rxandroidble3.RxBleClient;
import com.polidea.rxandroidble3.RxBleConnection;
import com.polidea.rxandroidble3.RxBleDevice;
import com.polidea.rxandroidble3.RxBleDeviceServices;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class BleDeviceHelper {

    private static final String TAG  = "BleDeviceHelper";
    /*
    *
    *   00001800-0000-1000-8000-00805f9b34fb ===> 00002a00-0000-1000-8000-00805f9b34fb

        00001801-0000-1000-8000-00805f9b34fb ===> 00002a05-0000-1000-8000-00805f9b34fb

        00000180-0000-1000-8000-00805f9b34fb ===> 0000fef4-0000-1000-8000-00805f9b34fb
    * */
    public static UUID BLUETOOTH_LE_GEN_REMOTE_SERVICE = UUID.fromString("00000180-0000-1000-8000-00805f9b34fb");
    public static UUID BLUETOOTH_LE_NRF_CHAR_RW2 = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");  // read on microbit, write on adafruit
    public static UUID BLUETOOTH_LE_NRF_CHAR_RW3 = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");

    public static UUID SERVICE_1 = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"); //GAP Service
    public static UUID CHAR_1 = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb"); //Device Name

    public static UUID SERVICE_2 = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb"); //Gatt Service
    public static UUID CHAR_2 = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb"); //Service Changed

    public static UUID SERVICE_3 = UUID.fromString("00000180-0000-1000-8000-00805f9b34fb"); //Remote Control
    public static UUID CHAR_3 = UUID.fromString("0000fef4-0000-1000-8000-00805f9b34fb"); //Google LLC

    private final RxBleDevice rxBleDevice;
    private final Context context;
    private Disposable connectionDisposable;
    private RxBleConnection bleConnection;

    public BleDeviceHelper(Context context, BluetoothDevice device) {
        this.context = context;
        RxBleClient rxBleClient = RxBleClient.create(context);
        this.rxBleDevice = rxBleClient.getBleDevice(device.getAddress());
    }

    public void connect() {
        Log.i(TAG, "connect: connecting");
        connectionDisposable = rxBleDevice.establishConnection(false)
                .subscribe(this::onConnectionReceived, this::onConnectionError);
    }
    private void onConnectionError(Throwable throwable) {
        Log.i(TAG, "onConnectionError: "+ throwable.getMessage(), throwable);
    }

    private void onConnectionReceived(RxBleConnection connection) {
        this.bleConnection = connection;
        Log.i(TAG, "onConnectionReceived: connected");

        // Connection established, perform further operations
        connection.discoverServices()
                .subscribe(this::onServicesDiscovered, this::onServiceDiscoveryError);
    }

    private void onServiceDiscoveryError(Throwable throwable) {
        Log.e(TAG, "onServiceDiscoveryError: " + throwable.getMessage(), throwable);
    }
    @SuppressLint("CheckResult")
    private void onServicesDiscovered(RxBleDeviceServices services) {
        Log.i(TAG, "onServicesDiscovered: services discovered");
        Intent intent = new Intent(context, ConnectedDeviceActivity.class);
        // Pass the device name and address as extras
        intent.putExtra("deviceName", rxBleDevice.getName());
        intent.putExtra("deviceAddress", rxBleDevice.getMacAddress());

        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, rxBleDevice.getBluetoothDevice());


        // Pass the list of service UUIDs as extras
        intent.putExtra("serviceUuids", new ArrayList<>(services.getBluetoothGattServices()));
        // Start the ConnectedDeviceActivity
        context.startActivity(intent);
    }


    private boolean isConnected() {
        return rxBleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    @SuppressLint("CheckResult")
    public void writeData(String data) {
        if (isConnected()) {
            bleConnection.discoverServices()
                    .flatMap(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(SERVICE_3, CHAR_3))
                    .flatMap(characteristic -> bleConnection.writeCharacteristic(characteristic, data.getBytes(StandardCharsets.UTF_8)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bytes -> onWriteSuccess(),
                            this::onWriteFailure
                    );
        } else {
            Log.e("BleDeviceConnector", "Device is not connected");
        }
    }


    @SuppressLint("CheckResult")
    public void readData(UUID characteristicUuid) {
        if (isConnected()) {
            bleConnection.discoverServices()
                    .subscribe(
                            rxBleDeviceServices -> {
                                rxBleDeviceServices.getBluetoothGattServices().stream().
                                        forEach(ser -> {
                                            Log.d("BleDeviceConnector", "Service Discovered :" + ser.getUuid().toString());
                                            ser.getCharacteristics().stream().forEach(charUuid -> {
                                                Log.d("BleDeviceConnector", "Char Discovered :" + charUuid.getUuid().toString());
                                                readChar(charUuid.getUuid());
                                            });
                                        });
                            },
                            throwable -> Log.e("BleDeviceConnector", "Error reading services", throwable)
                    );
        } else {
            Log.e("BleDeviceConnector", "Device is not connected");
        }
    }

    @SuppressLint("CheckResult")
    private void readChar(UUID characteristicUuid) {
        bleConnection.readCharacteristic(characteristicUuid)
                .subscribe(
                        characteristicValue -> {
                            String data = new String(characteristicValue, StandardCharsets.UTF_8);
                            Log.d("BleDeviceConnector", "Read data from " + characteristicUuid + " " + data);
                        },
                        throwable -> Log.e("BleDeviceConnector", "Error reading data", throwable)
                );
    }

    public void disconnect() {
        if (connectionDisposable != null && !connectionDisposable.isDisposed()) {
            connectionDisposable.dispose();
        }
    }

    private void onWriteSuccess() {
        //noinspection ConstantConditions
        Toast.makeText(context, "Write success", Toast.LENGTH_SHORT).show();
    }

    private void onWriteFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Toast.makeText(context, "Write error: " + throwable, Toast.LENGTH_SHORT).show();
        Log.e("BleDeviceConnector", "onWriteFailure: " + throwable);
    }

    public RxBleConnection getConnection() {
        return bleConnection;
    }
}

