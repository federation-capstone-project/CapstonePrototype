package au.edu.federation.capstoneprototype;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter btAdapter;
    public List<Beacon> list_beacons = new ArrayList<>();
    public List<Class> classes = new ArrayList<>();
    ListView saved;
    BeaconAdapter adapter;
    boolean searching = false;
    int REQUEST_ENABLE_BT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            // Device does not support Bluetooth
        }
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        adapter = new BeaconAdapter(this, list_beacons, false);
        saved = findViewById(R.id.saved_dynamic);
        saved.setAdapter(adapter);
        saved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Beacon selected = (Beacon) saved.getItemAtPosition(i);
                Class current_class = classes.get(selected.getID());
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Check into class!")
                        .setMessage(getString(R.string.class_format_full, current_class.getName(), current_class.getTeacher(), current_class.getTime(), current_class.getDay()))
                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        FloatingActionButton addButton = findViewById(R.id.add_beacon);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searching) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                    Log.d(getPackageName(), "Searching...");
                    btAdapter.startDiscovery();
                    searching = true;
                }
            }
        });
        classes.add(new Class(0,"Transgender History", "Mr Hall","09:00", "Monday", "Lecture Room 70", "FE:90:6F:57:2A:FB"));
        classes.add(new Class(1,"Life of the Flex (of muscles)", "Mr Copsey","09:00", "Tuesday", "Lecture Room 70", "C0:28:8D:4E:27:B2"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for (Class known : classes){
                    if(known.getMacAddress().equals(device.getAddress())){
                        list_beacons.add(new Beacon(known.getClassId(),known.getName(), known.getMacAddress(), String.valueOf(System.currentTimeMillis() / 1000L)));
                    }
                }

                Log.i("BT", device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };
}