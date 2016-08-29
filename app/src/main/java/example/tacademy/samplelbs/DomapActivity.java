package example.tacademy.samplelbs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

import example.tacademy.samplelbs.data.POIResult;
import example.tacademy.samplelbs.data.Poi;
import example.tacademy.samplelbs.manager.NetworkManager;
import example.tacademy.samplelbs.manager.NetworkRequest;
import example.tacademy.samplelbs.request.POISearchRequest;

public class DomapActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnCameraMoveListener, GoogleMap.OnInfoWindowClickListener{

    GoogleMap map;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;

    EditText keywordView;
    ListView listView;
    ArrayAdapter<Poi> mAdapter;

    Map<Poi,Marker> markerResolver = new HashMap<>();
    Map<Marker, Poi> poiResolver = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        listView = (ListView)findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<Poi>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        keywordView = (EditText)findViewById(R.id.edit_keyword);
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        fragment.getMapAsync(this);

        Button btn = (Button)findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = keywordView.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    POISearchRequest request = new POISearchRequest(DomapActivity.this, keyword);
                    NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<POIResult>() {
                        @Override
                        public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {

                            clear();

                            mAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                            for (Poi poi : result.getSearchPoiInfo().getPois().getPoi()) {
                                addMarker(poi);
                            }
                            if (result.getSearchPoiInfo().getPois().getPoi().length > 0) {
                                Poi poi = result.getSearchPoiInfo().getPois().getPoi()[0];
                                moveMap(poi.getLatitude(), poi.getLongitude());
                            }
                        }

                        @Override
                        public void onFail(NetworkRequest<POIResult> request, int errorCode, String errorMessage, Throwable e) {

                        }
                    });
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Poi poi = (Poi) listView.getItemAtPosition(position);
                animateMap(poi.getLatitude(), poi.getLongitude(), new Runnable() {
                    @Override
                    public void run() {
                        Marker m = markerResolver.get(poi);
                        m.showInfoWindow();
                    }
                });
            }
        });
    }

    private void clear(){
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Poi poi = mAdapter.getItem(i);
            Marker m = markerResolver.get(poi);
            m.remove();
        }
        mAdapter.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnCameraMoveListener(this);
        map.setOnInfoWindowClickListener(this);
    }


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

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            mListener.onLocationChanged(location);
        }
        mLM.requestSingleUpdate(mProvider, mListener, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    private void animateMap(double lat, double lng, final Runnable callback) {
        if (map != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(lat,lng));
            map.animateCamera(update, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    callback.run();
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }
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

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            moveMap(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onCameraMove() {
        CameraPosition position = map.getCameraPosition();
        LatLng target = position.target;
        Projection projection = map.getProjection();
        VisibleRegion region = projection.getVisibleRegion();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(DomapActivity.this, "This place selected your home", Toast.LENGTH_SHORT).show();
    }
}
