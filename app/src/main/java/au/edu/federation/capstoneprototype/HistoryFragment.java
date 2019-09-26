package au.edu.federation.capstoneprototype;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static au.edu.federation.capstoneprototype.Utils.compareTwoDates;

public class HistoryFragment extends Fragment {

    public LocalDateTime s;
    public ArrayList<CalItem> sss;
    ListView list;
    ArrayList<CalItem> arrayList;
    CalendarList adapter;
    CalItem newCal;
    Calendar calendar = Calendar.getInstance();
    private TextView dateview;
    private Context _context;
    private Button openDatePickerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_schedule, container, false);
        DatabaseHandler db = new DatabaseHandler(getContext());
        arrayList = new ArrayList<CalItem>();
        adapter = new CalendarList(getContext(), arrayList);
        ListView a = view.findViewById(R.id.lol);
        a.setAdapter(adapter);
        newCal = null;
        sss = new ArrayList<CalItem>();
        // Reading all classes
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
            CalItem qq = new CalItem(cn.getName(), cn.getTeacherName(), cn.getLocation(), Utils.string_time(cn.getStart()) + " - " + Utils.string_time(cn.getFinish()), Utils.string_date_full(cn.getDate()), cn.isPresent());
            sss.add(qq);
            // Writing Classes to log
            Log.d("Name: ", log);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        dateview = view.findViewById(R.id.datetemp);
        _context = this.getContext();
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Class History");
        adapter.clear();
        for (int i = 0; i < sss.size(); i++) {
            if (compareTwoDates(sss.get(i).getClassDateTime(), (Calendar.getInstance().getTime()))) {
                if (sss.get(i).getClassPresence().equalsIgnoreCase("true")) {
                    adapter.add(sss.get(i));
                }

            }
        }
        dateview.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + "/" + (Calendar.getInstance().get(Calendar.YEAR))));

        openDatePickerButton = view.findViewById(R.id.btnitem);
        openDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateview.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        calendar.set(year, month, dayOfMonth, 0, 0);
                        adapter.clear();
                        for (int i = 0; i < sss.size(); i++) {
                            if (compareTwoDates(sss.get(i).getClassDateTime(), (calendar.getTime()))) {
                                if (sss.get(i).getClassPresence().equalsIgnoreCase("true")) {
                                    adapter.add(sss.get(i));
                                }
                            }
                        }

                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

    }


}