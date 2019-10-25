package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.edu.federation.capstoneprototype.Base.Class;


public class ClassAdapter extends ArrayAdapter<Class> {
    private List<Class> list;

    ClassAdapter(Context context, List<Class> list) {
        super(context, R.layout.beacon, list);
        this.list = list;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.beacon, parent, false);

        }
        final Class beacon = list.get(position);
        ImageView star = convertView.findViewById(R.id.class_star);
        TextView beaconName = convertView.findViewById(R.id.class_name);
        if (beacon.getName() == null) {
            beaconName.setText("");
        } else if (beacon.canSee()) {
            beaconName.setTextColor(Color.RED);
            beaconName.setText(beacon.getName() + " *");

        } else {
            beaconName.setTextColor(Color.BLACK);
            beaconName.setText(beacon.getName());
        }

        if (beacon.isPresent().equals("true")) {
            star.setVisibility(View.VISIBLE);
        } else {
            star.setVisibility(View.INVISIBLE);
        }
        TextView beaconMac = convertView.findViewById(R.id.class_description);

        beaconMac.setText(getContext().getString(R.string.class_format_description_long, beacon.getTeacherName(), beacon.getLocation(), Utils.string_time(beacon.getStart()), Utils.string_time(beacon.getFinish())));


        return convertView;
    }

}
