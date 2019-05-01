package au.edu.federation.capstoneprototype;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class AddBeacon extends AppCompatActivity {
    ArrayList<Beacon> visibleDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon);

        setTitle("Select transmitter...");

        visibleDevices = new ArrayList<Beacon>();
        visibleDevices.add (new Beacon(
                "Tile",
                "accessory",
                "01:02:03:04:05",
                "tuesday"));
        visibleDevices.add (new Beacon(
                "Spiderman",
                "speaker",
                "02:03:04:05:06",
                "today"
        ));
        visibleDevices.add (new Beacon(
                "Peter Parker",
                "mouse",
                "03:04:05:06:07",
                "now"
        ));

        BeaconAdapter visibleAdapter = new BeaconAdapter (this, visibleDevices, true);
        ListView visible = findViewById(R.id.visible_dynamic);
        visible.setAdapter(visibleAdapter);
    }
}
