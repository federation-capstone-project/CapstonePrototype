package au.edu.federation.capstoneprototype;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Beacon> savedDevices;
    ArrayList<Beacon> visibleDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.w("Prototype", "Bluetooth adapter not found");
        }

        savedDevices = new ArrayList<Beacon>();
        visibleDevices = new ArrayList<Beacon>();
    }
}