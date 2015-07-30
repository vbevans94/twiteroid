package info.zametki.twitteroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import info.zametki.twitteroid.R;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String EXTRA_LAT_LNG = "extra_lat_lng";
    public static final String EXTRA_NAME = "extra_name";

    private GoogleMap map;

    public static void start(Context context, LatLng latLng, String name) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_LAT_LNG, latLng);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMapAsync(this);
        } else {
            onMapReady(map);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Intent intent = getIntent();
        LatLng sydney = intent.getParcelableExtra(EXTRA_LAT_LNG);
        map.addMarker(new MarkerOptions().position(sydney).title(intent.getStringExtra(EXTRA_NAME)));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
