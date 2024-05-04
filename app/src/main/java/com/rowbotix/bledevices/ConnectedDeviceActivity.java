package com.rowbotix.bledevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rowbotix.R;

import java.util.List;

public class ConnectedDeviceActivity extends AppCompatActivity {

    private TextView textDeviceName;
    private TextView textDeviceAddress;
    private ListView listServices;
    private Button buttonDisconnect;
    private BleDeviceHelper bleDeviceHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connected_device_layout);

        // Initialize views
        textDeviceName = findViewById(R.id.text_device_name);
        textDeviceAddress = findViewById(R.id.text_device_address);
        listServices = findViewById(R.id.list_services);
        buttonDisconnect = findViewById(R.id.button_disconnect);

        // Get device info from intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String deviceName = extras.getString("deviceName");
            String deviceAddress = extras.getString("deviceAddress");
            textDeviceName.setText(deviceName);
            textDeviceAddress.setText(deviceAddress);
        }

        // Get list of service UUIDs from intent extras
        List<BluetoothGattService> services = (List<BluetoothGattService>) getIntent().getSerializableExtra("serviceUuids");
        if (services != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getServiceNames(services));
            listServices.setAdapter(adapter);
        }

        // Initialize BleDeviceHelper
        BluetoothDevice bluetoothDevice = getIntent().getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); //this is null
        bleDeviceHelper = new BleDeviceHelper(getApplicationContext(), bluetoothDevice );

        // Disconnect button click listener
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleDeviceHelper.disconnect();
                finish();
            }
        });
    }

    // Helper method to extract service names from service UUIDs
    private String[] getServiceNames(List<BluetoothGattService> services) {
        String[] names = new String[services.size()];
        for (int i = 0; i < services.size(); i++) {
            names[i] = services.get(i).getUuid().toString();
        }
        return names;
    }
}
