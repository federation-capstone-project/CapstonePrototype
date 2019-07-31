package au.edu.federation.capstoneprototype;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ClassFragment extends Fragment {
    BluetoothAdapter btAdapter;
    ClassAdapter adapter;
    public List<Class> list_known = new ArrayList<>();
    public List<Class> list_classes = new ArrayList<>();
    ListView saved;
    boolean searching = false;
    int REQUEST_ENABLE_BT = 0;
    SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_class, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceDiscovery();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        if (searching) {
            btAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Classes");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Log.d(getActivity().getPackageName(), "Device does not feature bluetooth");
        }
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
        adapter = new ClassAdapter(getActivity(), list_classes);
        saved = getView().findViewById(R.id.saved_dynamic);
        saved.setAdapter(adapter);
        saved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class selected = (Class) saved.getItemAtPosition(i);
                final Class current_class = list_classes.get(selected.getClassId());
                if (current_class.isPresent()) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Check out of class!")
                            .setMessage(getString(R.string.class_format_full, current_class.getName(), current_class.getTeacher(), current_class.getTime(), current_class.getDay()))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ImageView classStar = getView().findViewById(R.id.class_star);
                                    classStar.setVisibility(View.INVISIBLE);
                                    current_class.setPresent(false);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Check into class!")
                            .setMessage(getString(R.string.class_format_full, current_class.getName(), current_class.getTeacher(), current_class.getTime(), current_class.getDay()))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ImageView classStar = getView().findViewById(R.id.class_star);
                                    classStar.setVisibility(View.VISIBLE);
                                    current_class.setPresent(true);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }
        });
        FloatingActionButton addButton = getView().findViewById(R.id.add_beacon);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceDiscovery();
            }
        });
        list_known.add(new Class(0, "Medical History", "Mr Hall", "09:00", "Monday", "Lecture Room 70", "FE:90:6F:57:2A:FB", false));
        list_known.add(new Class(1, "Life of the Flex (of muscles)", "Mr Copsey", "09:00", "Tuesday", "Lecture Room 70", "C0:28:8D:4E:27:B2", false));

    }

    public void deviceDiscovery(){
        if(prefs.getBoolean("bluetooth_auto", true)){
            if (!searching) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                Log.d(getActivity().getPackageName(), "Searching...");
                btAdapter.startDiscovery();
                searching = true;
            }
        }

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for (Class known : list_known) {
                    if (known.getMacAddress().equals(device.getAddress())) {
                        list_classes.add(known);
                    }
                }

                Log.i("BT", device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(getActivity().getPackageName(), "Discovery Started");
                Toast.makeText(getContext(), "Searching...", Toast.LENGTH_SHORT).show();
                searching = true;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(getActivity().getPackageName(), "Discovery Finished");
                Toast.makeText(getContext(), "Searching Finished", Toast.LENGTH_SHORT).show();
                searching = false;
            }
        }
    };

}