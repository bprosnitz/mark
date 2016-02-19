package bprosnitz.mark;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import bprosnitz.mark.model.Location;
import bprosnitz.mark.model.LocationType;


public class LocationDialogFragment extends DialogFragment {

    public LocationDialogFragment() {
    }

    LocationDialogListener listener;

    public interface LocationDialogListener {
        void onSave(Location location);
        void onCancel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (LocationDialogListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LocationDialogListener");
        }

    }

    public static LocationDialogFragment newInstance(String param1, String param2) {
        LocationDialogFragment fragment = new LocationDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_location_dialog, container, false);
        view.findViewById(R.id.location_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationType locationType;
                if (((RadioButton)view.findViewById(R.id.locationtype_default)).isChecked()) {
                    locationType = LocationType.Default;
                } else if (((RadioButton)view.findViewById(R.id.locationtype_parking)).isChecked()) {
                    locationType = LocationType.Parking;
                } else if (((RadioButton)view.findViewById(R.id.locationtype_waypoint)).isChecked()) {
                    locationType = LocationType.Waypoint;
                } else {
                    throw new RuntimeException("invalid location type - no radio buttons checked");
                }
                String locStr = ((EditText)view.findViewById(R.id.location_point)).getText().toString();
                String[] locParts = locStr.split(",");
                if (locParts.length != 2) {
                    throw new RuntimeException("invalid location string " + locStr + " - expected exactly one comma");
                }
                double lat = android.location.Location.convert(locParts[0]);
                double lon = android.location.Location.convert(locParts[1]);
                android.location.Location pt = new android.location.Location("pt");
                pt.setLatitude(lat);
                pt.setLongitude(lon);
                Location loc = new Location(locationType, pt);
                listener.onSave(loc);
                LocationDialogFragment.this.dismiss();
            }
        });
        view.findViewById(R.id.location_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
                LocationDialogFragment.this.dismiss();
            }
        });
        return view;
    }
}
