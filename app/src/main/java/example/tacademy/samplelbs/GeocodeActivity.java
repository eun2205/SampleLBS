package example.tacademy.samplelbs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodeActivity extends AppCompatActivity {

    LocationManager mLM;// 1. 위치획득을 위해 필요

    String mProvider = LocationManager.NETWORK_PROVIDER;
    TextView messageView;

    ListView listView;
    ArrayAdapter<Address> mAdapter;
    EditText keywordView;

    Address mAddress;
//    GoogleMap map;
//    Map<Poi, Marker> markerResolver = new HashMap<>();
//    Map<Marker, Poi> poiResolver = new HashMap<>();
//    GoogleApiClient mApiClient;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocode);
        messageView = (TextView) findViewById(R.id.text_message);
        keywordView = (EditText) findViewById(R.id.edit_keyword);
        listView = (ListView) findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<Address>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
//        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map_fragment);
//        fragment.getMapAsync((OnMapReadyCallback) this);

        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 2. 매니저에 대한 코드 작성
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setCostAllowed(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        mProvider = mLM.getBestProvider(criteria, true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }

//        mApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addApi(ActivityRecognition.API)
//                .addConnectionCallbacks(this)
//                .enableAutoManage(this, this)
//                .build();
//    }


        Button btn = (Button) findViewById(R.id.btn_convert);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = keywordView.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    convertAddressToLocation(keyword);
                }
            }
        });

        btn=(Button)findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GeocodeActivity.this, "lat= "+ lat + "lng= " +lng, Toast.LENGTH_SHORT).show();
            }
        });
    }

    static double lat, lng;

    private void convertAddressToLocation(String keyword) { //주소를 위치로 변경하는 코드
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
            try {
                List<Address> list = geocoder.getFromLocationName(keyword, 10);
                mAddress=list.get(0);
                lat = mAddress.getLatitude();
                lng = mAddress.getLongitude();
                mAdapter.clear();
                mAdapter.addAll(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertLocationToAddress(Location location) { //위치를 주소로 변경하는 코드
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
            try {
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
                mAdapter.clear();
                mAdapter.addAll(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestLocationPermission() {// 3.퍼미션체크하는 과정이 필요
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION_PERMISSION);
    }

    private static final int RC_LOCATION_PERMISSION = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_LOCATION_PERMISSION) {
            if (permissions != null && permissions.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
        Toast.makeText(this, "need location permission", Toast.LENGTH_SHORT).show();
        finish();
    }

    boolean isFirst = true;

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!mLM.isProviderEnabled(mProvider)) {
            if (isFirst) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                isFirst = false;
            } else {
                Toast.makeText(this, "location enable setting..", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }

        Location location = mLM.getLastKnownLocation(mProvider); //위치와 관련된 정보를 담고있는 클래스
        if (location != null) {

        }
        mLM.requestLocationUpdates(mProvider, 2000, 5, mListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLM.removeUpdates(mListener);
    }

    private void displayLocation(Location location) {
//        messageView.setText("lat : " + location.getLatitude() + ", lng : " + location.getLongitude());
    }

    LocationListener mListener = new LocationListener() { // 4. 위치정보 획득
        @Override
        public void onLocationChanged(Location location) {
            displayLocation(location);
        }

        //새로 픽스된 위치정보가 있으면 호출
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
//provider의 상태가 변경되면 호출. status는 LocationProvider에 정의
        }

        @Override
        public void onProviderEnabled(String s) {
//설정에서 등록된 provider가 enabled로 설정되면 호출
        }

        @Override
        public void onProviderDisabled(String s) {
//설정에서 등록된 provider가 disabled로 설정되면 호출
        }
    };
}


//    @Override
//    public void onCameraMove() {
//
//    }
//
//    @Override
//    public void onInfoWindowClick(Marker marker) {
//
//    }
//
//    @Override
//    public void onMapClick(LatLng latLng) {
//
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        map = googleMap;
//        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
////        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
////        map.setIndoorEnabled(true);
////        map.setBuildingsEnabled(true);
////        map.setTrafficEnabled(true);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//
//        map.getUiSettings().setCompassEnabled(true);
//        map.getUiSettings().setZoomControlsEnabled(true);
//        map.setOnCameraMoveListener(this);
//        map.setOnMapClickListener(this);
//        map.setOnMarkerClickListener(this);
//        map.setOnInfoWindowClickListener(this);
//        map.setOnMarkerDragListener(this);
//
//        map.setInfoWindowAdapter(new MyInfoWindow(this, poiResolver));
//    }
//
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        return false;
//    }
//
//    @Override
//    public void onMarkerDragStart(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDrag(Marker marker) {
//
//    }
//
//    @Override
//    public void onMarkerDragEnd(Marker marker) {
//
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//}

