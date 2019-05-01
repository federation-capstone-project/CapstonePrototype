package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BeaconAdapter extends ArrayAdapter<Beacon> {
    ArrayList<Beacon> list;

    public BeaconAdapter(Context context, ArrayList<Beacon> list) {
        super (context, R.layout.beacon, list);
        this.list = list;
    }

    public View getView (final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

        }
        final Beacon beacon = list.get(position);
        return convertView;
    }
}
