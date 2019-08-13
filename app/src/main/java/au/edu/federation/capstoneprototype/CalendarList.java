package au.edu.federation.capstoneprototype;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarList extends ArrayAdapter<CalItem> {
   public CalendarList(Context context, ArrayList<CalItem> calItems){
       super(context,0,calItems);
   }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){
       CalItem calItem = getItem(position);
       if (convertView == null)
       {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_array,parent,false);
       }
       //TextView tvName = (TextView) convertView.findViewById(R.id.txt);
       Button tvName = (Button) convertView.findViewById(R.id.btnitem);

            tvName.setText(calItem.ClassName + ", " + calItem.ClassClin + "\n Room: " + calItem.ClassLocation + " "+"\n" + calItem.ClassTime+ "\n"+calItem.ClassDateTime.toString());

        return convertView;
   }
}
