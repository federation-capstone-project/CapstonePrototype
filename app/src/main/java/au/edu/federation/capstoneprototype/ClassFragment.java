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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import au.edu.federation.capstoneprototype.Base.Class;
import au.edu.federation.capstoneprototype.Base.ClassOffline;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClassFragment extends Fragment {
    public List<String> seen_macs = new ArrayList<>();
    public List<Class> list_classes = new ArrayList<>();
    public boolean ServerConnected;
    BluetoothAdapter btAdapter;
    ClassAdapter adapter;
    ListView saved;
    SwipeRefreshLayout swipe_view;
    boolean searching = false;
    int REQUEST_ENABLE_BT = 0;
    SharedPreferences prefs;
    Class current_class;
    DatabaseHandler db;
    OffineDatabaseHandler db_offline;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
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
        getActivity().setTitle("Today - " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + "/" + (Calendar.getInstance().get(Calendar.YEAR))));
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        db = new DatabaseHandler(getContext());
        db_offline = new OffineDatabaseHandler(getContext());
        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.d(getActivity().getPackageName(), "Device does not feature bluetooth");
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
        adapter = new ClassAdapter(getActivity(), list_classes);
        saved = getView().findViewById(R.id.saved_dynamic);
        saved.setAdapter(adapter);
        list_classes.clear();
        for (Class cc : db.getAllClasses()) {
            Date newDate = Utils.string_date_full(cc.getFinish());
            if (Utils.compareTwoDates(newDate, Calendar.getInstance().getTime())) {
                if (newDate.after(Calendar.getInstance().getTime())) {
                    list_classes.add(cc);
                    adapter.notifyDataSetChanged();
                }
            }
            Collections.sort(list_classes, new Comparator<Class>() {
                @Override
                public int compare(Class o1, Class o2) {
                    return o1.getStart().compareTo(o2.getStart());
                }

            });
            adapter.notifyDataSetChanged();
        }
        saved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                current_class = (Class) saved.getItemAtPosition(i);
                if (seen_macs.contains(current_class.getMac())) {
                    if (current_class.isPresent().equals("true")) {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Check out of class " + current_class.getName())
                                .setMessage(getString(R.string.class_format_description, current_class.getTeacherName(), current_class.getLocation(), Utils.string_date_full(current_class.getDate())))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        current_class.setPresent("false");
                                        if(isConnectedToServer()){
                                            postRequest(prefs.getString("student_id", "69"), String.valueOf(current_class.getId()), false, false);
                                            Log.d(getActivity().getPackageName(), "Passed on to postRequest");
                                        }else {
                                            db_offline.addClass(new ClassOffline( (int) System.currentTimeMillis(), String.valueOf(current_class.getId()),prefs.getString("student_id", "69"), "false", "false"));
                                            Log.d(getActivity().getPackageName(), "Passed on to offline Database");
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        new AlertDialog.Builder(view.getContext())
                                .setTitle("Check into class " + current_class.getName())
                                .setMessage(getString(R.string.class_format_description, current_class.getTeacherName(), current_class.getLocation(), Utils.string_date_full(current_class.getDate())))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        current_class.setPresent("true");
                                        db.updateClassPresence(current_class);
                                        if (isConnectedToServer()) {
                                            postRequest(prefs.getString("student_id", "69"), String.valueOf(current_class.getId()), true, false);
                                            Log.d(getActivity().getPackageName(), "Passed on to postRequest");
                                        }else {
                                            db_offline.addClass(new ClassOffline( (int) System.currentTimeMillis(), String.valueOf(current_class.getId()),prefs.getString("student_id", "69"), "true","false" ));
                                            Log.d(getActivity().getPackageName(), "Passed on to offline Database");
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                } else {
                    Log.d(getActivity().getPackageName(), "Mac not seen");
                    Toast.makeText(getContext(), "Unable to communicate with beacon!", Toast.LENGTH_SHORT).show();

                }
            }
        });
        saved.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                current_class = (Class) saved.getItemAtPosition(i);
                if (current_class.isPresent().equals("true")) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Check out of class " + current_class.getName())
                            .setMessage(getString(R.string.class_format_description, current_class.getTeacherName(), current_class.getLocation(), Utils.string_date_full(current_class.getDate())))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    current_class.setPresent("false");
                                    if(isConnectedToServer()){
                                        postRequest(prefs.getString("student_id", "69"), String.valueOf(current_class.getId()), false, true);
                                        Log.d(getActivity().getPackageName(), "Passed on to postRequest");
                                    }else {
                                        db_offline.addClass(new ClassOffline( (int) System.currentTimeMillis(), String.valueOf(current_class.getId()),prefs.getString("student_id", "69"), "false", "true"));
                                        Log.d(getActivity().getPackageName(), "Passed on to offline Database");
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Check into class " + current_class.getName())
                            .setMessage(getString(R.string.class_format_description, current_class.getTeacherName(), current_class.getLocation(), Utils.string_date_full(current_class.getDate())))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    current_class.setPresent("true");
                                    db.updateClassPresence(current_class);
                                    if(isConnectedToServer()){
                                        postRequest(prefs.getString("student_id", "69"), String.valueOf(current_class.getId()), true, true);
                                        Log.d(getActivity().getPackageName(), "Passed on to postRequest");
                                    }else {
                                        db_offline.addClass(new ClassOffline( (int) System.currentTimeMillis(),String.valueOf(current_class.getId()),prefs.getString("student_id", "69"), "true", "true"));
                                        Log.d(getActivity().getPackageName(), "Passed on to offline Database");
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                return false;
            }
        });
        swipe_view = view.findViewById(R.id.swiperefresh);
        swipe_view.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        deviceDiscovery();
                    }
                }
        );

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds
                // for (int i = 0; i < adapter.getCount()  ; i++) {
                if (adapter.getCount() > 0) {
                    if (seen_macs.contains(adapter.getItem(0).getMac())) {
                        adapter.getItem(1).setCansee(true);
                        adapter.notifyDataSetChanged();
                        Log.e("HI", adapter.getItem(0).getName());
                        //}

                    }
                }
                handler.postDelayed(this, 5000);
            }
        }, 5000);  //the time is in miliseconds

    }
    /**
     * Handles the ACTION_FOUND from the Device Discovery process
     *
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                seen_macs.add(device.getAddress());
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(getActivity().getPackageName(), "Discovery Started");
                Toast.makeText(getContext(), "Searching...", Toast.LENGTH_SHORT).show();
                searching = true;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(getActivity().getPackageName(), "Discovery Finished");
                Toast.makeText(getContext(), "Searching Finished", Toast.LENGTH_SHORT).show();
                swipe_view.setRefreshing(false);
                searching = false;
            }
        }
    };

    /**
     * Initiates the device discover process
     * Last's for 10-15 seconds
     * Any found devices are passed to @mReceiver
     */
    public void deviceDiscovery() {
        if (btAdapter != null) {
            if (prefs.getBoolean("bluetooth_auto", true)) {
                if (!searching) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                    Log.d(getActivity().getPackageName(), "Searching...");
                    btAdapter.startDiscovery();
                    searching = true;
                }
            }

        }
    }
    public boolean isConnectedToServer() {
/*      String url, int timeout
        Runtime runtime = Runtime.getRuntime();
        Process proc = null; //<- Try ping -c 1 www.serverURL.com
        try {
            proc = runtime.exec("ping www.google.com");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int mPingResult = 0;
        try {
            mPingResult = proc .waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mPingResult == 0){
            Log.e("S","SS");
            return true;
        }else{
            Log.e("S","SSGG");
            return false;
        }*/
        return false;
    }


    /**
     * Handles the communication with the Django framework
     *
     * @param student  the student's id
     * @param event    the class id
     * @param attended whether the student attended
     * @param manual   whether the student attendance was manually added
     *                 TODO OnFailure saves the event to the local database
     */

    public void postRequest(final String student, final String event, final Boolean attended, final Boolean manual) {
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://capstone.blny.me/studentevent/";
        OkHttpClient client = new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("student", student);
            postdata.put("event", event);
            postdata.put("attended", attended);
            postdata.put("manual", manual);
            Log.d(getActivity().getPackageName(), postdata.toString());
            Log.d(getActivity().getPackageName(), event);
        } catch (JSONException e) {
            Log.d(getActivity().getPackageName(), e.getMessage());
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
                .header("Authorization", "Token " + prefs.getString("student_token", ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage();
                Log.d(getActivity().getPackageName(), mMessage);
                Log.d(getActivity().getPackageName(), "OnFailure");
                db_offline.addClass(new ClassOffline( (int) System.currentTimeMillis() ,student, event, attended.toString(), manual.toString()));
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String mMessage = response.body().string();
                Log.d(getActivity().getPackageName(), mMessage);
                Log.d(getActivity().getPackageName(), "OnResponse");
            }
        });
    }


}
