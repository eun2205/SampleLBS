package example.tacademy.samplelbs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    TextView messageView;
    Geocoder mCoder;
    //   static double lat, lng;
    static double locX, locY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        messageView = (TextView) findViewById(R.id.text_message);
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        mCoder = new Geocoder(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }

        Button btn = (Button) findViewById(R.id.btn_2);

    }

//    List<Address> addr;
//    String saddr = messageView.getText().toString();

    private void requestLocationPermission() {
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
        Toast.makeText(this, "need permission", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "location success", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }

        Location location = mLM.getLastKnownLocation(mProvider);
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
        messageView.setText("lat : " + location.getLatitude() + ", lng : " + location.getLongitude());
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            //  displayLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            updateWithNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }

    };
    public static long lat, lng;

    private void updateWithNewLocation(Location location) {
        String latLongString;
        TextView myLocationText;
//        myLocationText = (TextView) findViewById(R.id.myLocationText);


        if (location != null) {
            lat = (long) (location.getLatitude() * 100000);
            lng = (long) (location.getLongitude() * 100000);
            latLongString = "Lat:" + lat + "\nLong:" + lng;
        } else {
            latLongString = "No location found";
        }
//        myLocationText.setText("Your Current Position is:\n" +
//                latLongString);
    }

    @Override
    public void onClick(View view) {
    }
}