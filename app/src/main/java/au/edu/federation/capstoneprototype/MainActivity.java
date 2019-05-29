package au.edu.federation.capstoneprototype;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ArrayList<Beacon> savedDevices;
    ArrayList<Beacon> visibleDevices;
    public static int REQUEST_BLUETOOTH = 1;
    BluetoothAdapter btAdapter;
    public List<Beacon> list_beacons= new ArrayList<>();
    ListView saved;
    BeaconAdapter adapter;
    boolean searching = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        adapter = new BeaconAdapter(this, list_beacons, false);
        saved = findViewById(R.id.saved_dynamic);
        saved.setAdapter(adapter);
        FloatingActionButton addButton = findViewById(R.id.add_beacon);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getPackageName(), "Searching...");
                if(!searching){
                    btAdapter.startDiscovery();
                    searching = true;
                }

            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                list_beacons.add( new Beacon(device.getName(), device.getAddress(), String.valueOf(System.currentTimeMillis() / 1000L)));
                Log.i("BT", device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };
}