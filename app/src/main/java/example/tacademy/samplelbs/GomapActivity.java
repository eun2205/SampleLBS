package example.tacademy.samplelbs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;
import java.util.Map;

import example.tacademy.samplelbs.data.Poi;

public class GomapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    GoogleMap map;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    Map<Poi, Marker> markerResolver = new HashMap<>();
    Map<Marker, Poi> poiResolver = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gomap);
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_fragment);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        map.setIndoorEnabled(true);
//        map.setBuildingsEnabled(true);
//        map.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraMoveListener(this);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerDragListener(this);

    }

    @Override
    public void onCameraMove() {
        CameraPosition position = map.getCameraPosition();
        LatLng target = position.target;
        Projection projection = map.getProjection();
        VisibleRegion region = projection.getVisibleRegion();
    }

    @Override
    public void onMapClick(LatLng latLng) {
       // addMarker(latLng.latitude, latLng.longitude, "My Marker");
    }

    Marker marker;

    private void addMarker(Poi poi) {
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(poi.getLatitude(), poi.getLongitude()));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        options.anchor(0.5f, 1);
        options.title(poi.getName());
        options.snippet(poi.getMiddleAddrName() + " " + poi.getLowerAddrName());

        Marker marker = map.addMarker(options);
        markerResolver.put(poi, marker);
        poiResolver.put(marker, poi);
    }


    private void addMarker(double lat double lng, String title) {
        if (marker != null) {
            marker.remove();
            marker = null;
        }
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(lat, lng));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        options.anchor(0.5f, 1);
        options.title(title);
        options.snippet("snippet - " + title);
        options.draggable(true);

        marker = map.addMarker(options);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                marker.showInfoWindow();
            }
        }, 2000);
        return true;
    }

    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
        Poi poi = poiResolver.get(marker);
        Log.i("GoogleMapActivity", "addr : " + poi.getUpperAddrName());

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng = marker.getPosition();
        Log.i("GoogleMapActivity", "lat : " + latLng.latitude + ", lng : " + latLng.longitude);
    }

    LocationListener mListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            moveMap(location.getLatitude(), location.getLongitude());
        }
    };

    private void moveMap(double lat, double lng) {
        if (map != null) {
            LatLng latLng = new LatLng(lat, lng);
            CameraPosition position = new CameraPosition.Builder()
                    .target(latLng)
                    .bearing(30)
                    .tilt(45)
                    .zoom(17)
                    .build();
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.moveCamera(update);
        }
    }
}
