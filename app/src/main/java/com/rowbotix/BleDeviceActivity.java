package com.rowbotix;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.polidea.rxandroidble3.RxBleClient;
import com.rowbotix.bledevices.BleDeviceHelper;
import com.rowbotix.databinding.ActivityBleDeviceBinding;
import com.rowbotix.bledevices.BluetoothHelper;
import com.rowbotix.bledevices.BleDeviceInfo;

import java.util.ArrayList;

public class BleDeviceActivity extends AppCompatActivity {

    private static final String TAG = "BleDeviceActivity";

    private ActivityBleDeviceBinding binding;
    private ListView listView;
    private ArrayAdapter<BleDeviceInfo> deviceAdapter;
    private ArrayList<BleDeviceInfo> discoveredDevices = new ArrayList<>();
    private BluetoothHelper bluetoothHelper;

    private RxBleClient rxBleClient;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBleDeviceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        binding.toolbarLayout.setTitle(getTitle());

        listView = findViewById(R.id.list_view);

        rxBleClient = RxBleClient.create(this);

        // Create the adapter for the ListView
        deviceAdapter = new ArrayAdapter<BleDeviceInfo>(this, android.R.layout.simple_list_item_1, discoveredDevices) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                BleDeviceInfo deviceInfo = discoveredDevices.get(position);
                textView.setText(deviceInfo.getName());
                return view;
            }
        };

        // Set the adapter for the ListView
        listView.setAdapter(deviceAdapter);
        bluetoothHelper = new BluetoothHelper(this, deviceAdapter, discoveredDevices, rxBleClient);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            BleDeviceInfo selectedDevice = deviceAdapter.getItem(position);
            if (selectedDevice != null && selectedDevice.getAddress() != null) {
                BluetoothDevice bluetoothDevice = bluetoothHelper.getBleDevice(selectedDevice.getAddress());
                if (bluetoothDevice != null) {
                    BleDeviceHelper bleDeviceConnector = new BleDeviceHelper(this, bluetoothDevice);
                    bleDeviceConnector.connect();
                } else {
                    Log.d(TAG, "bluetooth device: " + bluetoothDevice.getName());
                    Log.i(TAG, "connection error: device or address is null");
                }
            } else {
                Log.d(TAG, "selectedDevice: " + selectedDevice.getName());
                Log.d(TAG, "address: " + selectedDevice.getAddress());
                Log.i(TAG, "connection error: device or address is null");
            }
        });

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    bluetoothHelper.startScan();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothHelper.stopScan(); // Stop scanning when the activity is destroyed
    }
}
