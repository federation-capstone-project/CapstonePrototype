package au.edu.federation.capstoneprototype;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
        savedDevices.add (new Beacon(
                "Tile",
                "accessory",
                "01:02:03:04:05",
                "tuesday"));
        savedDevices.add (new Beacon(
                "Spiderman",
                "speaker",
                "02:03:04:05:06",
                "today"
        ));

        visibleDevices = new ArrayList<Beacon>();

        BeaconAdapter savedAdapter = new BeaconAdapter (this, savedDevices, false);
        ListView saved = findViewById(R.id.saved_dynamic);
        saved.setAdapter(savedAdapter);
    }
}