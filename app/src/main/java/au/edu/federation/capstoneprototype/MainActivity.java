package au.edu.federation.capstoneprototype;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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

        FloatingActionButton addButton = findViewById(R.id.add_beacon);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("Prototype", "opening add beacon activity");
                Intent intent = new Intent(getApplicationContext(), AddBeacon.class);
                startActivityForResult(intent, 1);
            }
        });

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