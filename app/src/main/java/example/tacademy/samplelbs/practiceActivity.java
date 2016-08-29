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
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import example.tacademy.samplelbs.data.POIResult;
import example.tacademy.samplelbs.data.Poi;
import example.tacademy.samplelbs.manager.NetworkManager;
import example.tacademy.samplelbs.manager.NetworkRequest;
import example.tacademy.samplelbs.request.POISearchRequest;

public class practiceActivity extends AppCompatActivity {

    LocationManager mLM;// 1. 위치획득을 위해 필요
    String mProvider = LocationManager.NETWORK_PROVIDER;
    ListView listView;
    ArrayAdapter<Address> mAdapter;
    EditText keywordView;
    Address mAddress;
    ArrayAdapter<Poi> pAdapter;

    Map<Poi,Marker> markerResolver = new HashMap<>();
    Map<Marker, Poi> poiResolver = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        keywordView = (EditText) findViewById(R.id.edit_keyword);
        listView = (ListView) findViewById(R.id.listView);
        pAdapter = new ArrayAdapter<Poi>(this, android.R.layout.simple_list_item_1);
//        mAdapter = new ArrayAdapter<Address>(this, android.R.layout.simple_list_item_1);
//        listView.setAdapter(mAdapter);
        listView.setAdapter(pAdapter);

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

        Button btn = (Button) findViewById(R.id.btn_convert);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = keywordView.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
//                    convertAddressToLocation(keyword);
                    POISearchRequest request = new POISearchRequest(practiceActivity.this, keyword);
                    NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<POIResult>() {
                        @Override
                        public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {

//                            clear();
//
//                            pAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
//                            for (Poi poi : result.getSearchPoiInfo().getPois().getPoi()) {
//                                ((IntentActivity)IntentActivity.mContext)addMarker(poi);
//                            }
//                            if (result.getSearchPoiInfo().getPois().getPoi().length > 0) {
//                                Poi poi = result.getSearchPoiInfo().getPois().getPoi()[0];
//                                moveMap(poi.getLatitude(), poi.getLongitude());
//                            }
                        }

                        @Override
                        public void onFail(NetworkRequest<POIResult> request, int errorCode, String errorMessage, Throwable e) {

                        }
                    });
                }
            }
        });

        btn=(Button)findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(practiceActivity.this, GeocoderActivity.class);
                Bundle b = new Bundle();
                b.putDouble("lat", lat);
                intent.putExtras(b);

                Bundle c = new Bundle();
                c.putDouble("lng", lng);
                intent.putExtras(c);
                startActivity(intent);
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

//    private void convertLocationToAddress(Location location) { //위치를 주소로 변경하는 코드
//        if (Geocoder.isPresent()) {
//            Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
//            try {
//                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
//                mAdapter.clear();
//                mAdapter.addAll(list);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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

    LocationListener mListener = new LocationListener() { // 4. 위치정보 획득
        @Override
        public void onLocationChanged(Location location) {

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
