package csd.atlas.htc_task.fragments;

import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;

import csd.atlas.htc_task.controllers.EventParser;
import csd.atlas.htc_task.R;
import csd.atlas.htc_task.pojo.Event;

/**
 * Created by FRAME on 2/25/2018.
 */

public class EventFragment extends Fragment implements OnMapReadyCallback {
    private Event mEvent;
    private static final String ARG_EVENT_GID = "event_gid";


    private TextView mEventName;
    private TextView mEventStartDate;
    private TextView mEventDescription;
    private ImageView mThumbnail;
    private MapView mMapView;
    private GoogleMap mMap;
    private LatLng mLocation;

    public static EventFragment newInstance(long gID) {
        Bundle args = new Bundle();
        args.putLong(ARG_EVENT_GID, gID);
        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_activity, container, false);
        mEventName = (TextView) v.findViewById(R.id.event_name);
        mEventName.setText(mEvent.getName());
        mEventStartDate = (TextView) v.findViewById(R.id.event_start_date);
        String dateToShow = "Дата начала: " +
                DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
                        .format(mEvent.getStartDate());
        mEventStartDate.setText(dateToShow);
        mEventDescription = (TextView) v.findViewById(R.id.event_description);
        mEventDescription.setMovementMethod(new ScrollingMovementMethod());
        mEventDescription.setText(mEvent.getDescription().replaceAll("<br>", "\n"));
        mThumbnail = (ImageView) v.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load(mEvent.getThumbnail()).into(mThumbnail);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);


        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        long gID = getArguments().getLong(ARG_EVENT_GID);
        mEvent = EventParser.get(getActivity()).getEvent(gID);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_add_calendar:
                long startMillis = mEvent.getStartDate().getTime();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, startMillis);
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // MAP SETTINGS START
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mLocation = new LatLng(mEvent.getLatitude(), mEvent.getLongitude());
        mMap.addMarker(new MarkerOptions().position(mLocation)).setTitle("Location");
        CameraUpdate cameraUpdate =
                CameraUpdateFactory
                        .newLatLngZoom(mLocation, 17);
        mMap.moveCamera(cameraUpdate);
    }
// MAP SETTINGS END

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
