package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ClassAdapter extends ArrayAdapter<Class> {
    private List<Class> list;

    ClassAdapter(Context context, List<Class> list) {
        super (context, R.layout.beacon, list);
        this.list = list;
    }

    public View getView (final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.beacon, parent, false);
        }
        final Class beacon = list.get(position);

        TextView beaconName = convertView.findViewById(R.id.class_name);
        if (beacon.getName() == null) {
            beaconName.setText("");
        }else {
            beaconName.setText(beacon.getName());
        }
        TextView beaconMac = convertView.findViewById(R.id.class_description);
        beaconMac.setText(getContext().getString(R.string.class_format_description,beacon.getTeacher(), beacon.getDay(), beacon.getTime()));
        return convertView;
    }
}