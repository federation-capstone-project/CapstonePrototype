package au.edu.federation.capstoneprototype;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ClassScheduleFragment extends Fragment {

    private TextView dateview;
public LocalDateTime s;
    private Context _context;
    private Button openDatePickerButton;
    ListView list;
    ArrayList<CalItem> arrayList;
    CalendarList adapter;
    CalItem newCal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_class_schedule, container, false);
        arrayList = new ArrayList<CalItem>();
        adapter = new CalendarList(getContext(),arrayList);
        ListView a = view.findViewById(R.id.lol);
        a.setAdapter(adapter);
        newCal = null;

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newCal = new CalItem("Bone Removal 101", "N. Hall", "N-298L", "9:30 - 14/08/2019", LocalDateTime.now());
        }

        adapter.add(newCal);*/
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        dateview = view.findViewById(R.id.datetemp);
        _context = this.getContext();
                super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Class Schedule");

        openDatePickerButton  = view.findViewById(R.id.btnitem);
        openDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateview.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        adapter.clear();
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                           if (dayOfMonth == LocalDateTime.now().getDayOfMonth() )
                           {
                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now());
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now());
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now());
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now());
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now());
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now());
                                   adapter.add(newCal);




                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now().plusDays(1));
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now().plusDays(2));
                                   adapter.add(newCal);
                                   newCal = new CalItem("Bone Removal 101", "N. Hall","N534L", "",LocalDateTime.now().plusDays(3));
                                   adapter.add(newCal);
                               }
                           }
                       }

                   }
               },year,month,dayOfMonth);
                datePickerDialog.show();
            }
        });


    }

}
