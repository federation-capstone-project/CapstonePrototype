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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClassFragment extends Fragment {
    public List<Class> list_known = new ArrayList<>();
    public List<Class> list_classes = new ArrayList<>();
    BluetoothAdapter btAdapter;
    ClassAdapter adapter;
    ListView saved;
    boolean searching = false;
    int REQUEST_ENABLE_BT = 0;
    SharedPreferences prefs;
    public TextView tvDate;

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
        getActivity().setTitle("Classes");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        final DatabaseHandler db = new DatabaseHandler(getContext());
        tvDate =view.findViewById(R.id.currentDate);
        tvDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + "/" + (Calendar.getInstance().get(Calendar.YEAR))));


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
        saved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class selected = (Class) saved.getItemAtPosition(i);
                final Class current_class = list_classes.get(selected.getId());
                if (current_class.isPresent().equals("true")) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Check out of class!")
                            .setMessage(getString(R.string.class_format_full, current_class.getName(), current_class.getTeacherName(), current_class.getStart()))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ImageView classStar = getView().findViewById(R.id.class_star);
                                    classStar.setVisibility(View.INVISIBLE);
                                    current_class.setPresent("true");
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Check into class!")
                            .setMessage(getContext().getString(R.string.class_format_description, current_class.getTeacherName(), current_class.getName(), current_class.getLocation()))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ImageView classStar = getView().findViewById(R.id.class_star);
                                    classStar.setVisibility(View.VISIBLE);
                                    current_class.setPresent("true");
                                    postRequest(prefs.getString("student_id", "69"), "3", true, false);
                                    Log.d(getActivity().getPackageName(), "Passed on to postRequest");
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
                final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_class_add, null);

                final EditText classID = dialogView.findViewById(R.id.et_class_id);
                final EditText studentID = dialogView.findViewById(R.id.et_student_id);
                Button button1 = dialogView.findViewById(R.id.buttonSubmit);
                Button button2 = dialogView.findViewById(R.id.buttonCancel);

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                        postRequest(studentID.getText().toString(), classID.getText().toString(), true, true);

                        db.addClass(new Class("MED1", "Medical Historyy", 1, "Mr Hall", "Lecture Room 70", "FE:90:6F:57:2A:FB", "2019-8-25", "09:00:00", "11:00:00", "true"));
                        db.addClass(new Class("MED1", "Life of Flex (of Muscles)", 1, "Mr Hall", "Lecture Room 70", "FE:90:6F:57:2A:FB", "2019-8-25", "11:00:00", "13:00:00", "true"));
                        db.addClass(new Class("MED1", "Medical History", 1, "Mr Hall", "Lecture Room 70", "FE:90:6F:57:2A:FB", "2019-8-25", "09:00:00", "11:00:00", "true"));
                        Toast.makeText(getContext(), "Class Successfully Checked In", Toast.LENGTH_SHORT).show();
                        list_classes.clear();
                        for (int i = 1; i < db.getAllClasses().size(); i++) {
                           Date newDate = string_date(db.getClass(i).getDate());
                           if (compareTwoDates(newDate, Calendar.getInstance().getTime()))
                            list_classes.add(db.getClass(i));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });
        Log.d("Reading: ", "Reading all classes");
        List<Class> classes = db.getAllClasses();

        for (Class cn : classes) {
            String log = " " + cn.getId()
                    + " " + cn.getCode()
                    + " " + cn.getName()
                    + " " + cn.getTeacherID()
                    + " " + cn.getTeacherName()
                    + " " + cn.getLocation()
                    + " " + cn.getMac()
                    + " " + cn.getDate()
                    + " " + cn.getStart()
                    + " " + cn.getFinish()
                    + " " + cn.isPresent();
            // Writing Classes to log
            Log.d("Name: ", log);
        }
    }

    /**
     *  Initiates the device discover process
     *  Last's for 10-15 seconds
     *  Any found devices are passed to @mReceiver
     */
    public void deviceDiscovery() {
        if (btAdapter != null){
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

    /**
     * Handles the ACTION_FOUND from the Device Discovery process
     *  TODO Compare to student schedule
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for (Class known : list_known) {
                    if (known.getMac().equals(device.getAddress())) {
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

    /**
     * Handles the communication with the Django framework
     * @param student the student's id
     * @param event the class id
     * @param attended whether the student attended
     * @param manual whether the student attendance was manually added
     * TODO OnFailure saves the event to the local database
     */

        public void postRequest(String student,  String event,  Boolean attended,  Boolean manual) {
            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            String url = "https://capstone.blny.me/studentevents/";
            OkHttpClient client = new OkHttpClient();
            JSONObject postdata = new JSONObject();
            try {
                postdata.put("student", student);
                postdata.put("event", event);
                postdata.put("attended", attended);
                postdata.put("manual", manual);
            } catch(JSONException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Token f89c13e25cd9d21a9d99d061d8e05e1cf5cff569")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String mMessage = e.getMessage();
                    Log.d(getActivity().getPackageName(), mMessage);
                    Log.d(getActivity().getPackageName(), "OnFailure");
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


    public Date string_date(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

            return myDate;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return myDate;

    }
    public boolean compareTwoDates(Date startDate, Date endDate) {
        Date sDate = getZeroTimeDate(startDate);
        Date eDate = getZeroTimeDate(endDate);
        if (sDate.before(eDate)) {
            Log.d("", "Start date is before end date");
            return false;
        }
        if (sDate.after(eDate)) {
            Log.d("", "Start date is after end date");
            return false;
        }
        Log.d("", "Start date and end date are equal");
        return true;
    }

    private Date getZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }
}
