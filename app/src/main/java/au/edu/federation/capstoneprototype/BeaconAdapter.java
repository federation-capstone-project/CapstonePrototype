package au.edu.federation.capstoneprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class BeaconAdapter extends ArrayAdapter<Beacon> {
    ArrayList<Beacon> list;
    boolean showSave;

    public BeaconAdapter(Context context, ArrayList<Beacon> list, boolean saveButton) {
        super (context, R.layout.beacon, list);
        this.list = list;
        this.showSave = saveButton;
    }

    public View getView (final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.beacon, parent, false);
        }
        final Beacon beacon = list.get(position);

        TextView beaconName = convertView.findViewById(R.id.beacon_name);
        beaconName.setText(beacon.name);

        TextView beaconMac = convertView.findViewById(R.id.beacon_mac);
        beaconMac.setText(beacon.macAddress);

        Button savebutton = convertView.findViewById(R.id.save_button);
        TextView visible = convertView.findViewById(R.id.beacon_available);

        return convertView;
    }
}
