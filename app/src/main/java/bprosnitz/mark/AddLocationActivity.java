package bprosnitz.mark;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import bprosnitz.mark.model.Entry;
import bprosnitz.mark.model.Location;
import bprosnitz.mark.model.LocationType;
import bprosnitz.mark.model.Tag;

public class AddLocationActivity extends FragmentActivity implements EditBoxFragment.OnFragmentInteractionListener {
    private static String TAG= "AddLocationActivity";
    private static final int FINE_LOCATION_REQUEST_CODE = 43;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        Intent intent = getIntent();
        int id = intent.getIntExtra("entry_id", -1);
        if (id == -1) {
            newEntryFromGPS();
        } else {
            initBody(Entry.newEntryFromId(id));
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        Log.i(TAG, "got fragment interaction: " + uri);
    }

    private void newEntryFromGPS() {
        int hasWriteContactsPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_REQUEST_CODE);
            return;
        } else {
            innerNewEntryFromGPS();
        }
    }

    private void innerNewEntryFromGPS() {
        LocationManager locationManager = (LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(new Criteria(), true);
        if (bestProvider == null) {
            throw new RuntimeException("no location providers enabled");
        }
        Location loc = new Location(LocationType.Default, locationManager.getLastKnownLocation(bestProvider));
        initBody(Entry.newEntryFromLocation(loc));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    innerNewEntryFromGPS();
                } else {
                    throw new RuntimeException("unable to operate without location permission");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initBody(Entry entry) {
        ((TextView)this.findViewById(R.id.location_title)).setText(entry.getTitle());

        Log.i(TAG, "edit box height: " + this.findViewById(R.id.location_editbox).getHeight());

        TwoWayView tagList = (TwoWayView) this.findViewById(R.id.tagList);
        List<Tag> sampleTags = new ArrayList<>();
        sampleTags.add(new Tag("aaa"));
        sampleTags.add(new Tag("bbb"));
        sampleTags.add(new Tag("ccc"));
        tagList.setAdapter(new ArrayAdapter<Tag>(this, R.layout.item_tag, sampleTags));
    }
}
