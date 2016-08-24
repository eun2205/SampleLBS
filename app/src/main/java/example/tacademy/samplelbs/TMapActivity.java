package example.tacademy.samplelbs;

import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.skp.Tmap.TMapView;

public class TMapActivity extends AppCompatActivity {
    TMapView mapView;
    LocationManager mLM;
    String mProvider = LocationManager.NETWORK_PROVIDER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);
        mapView = (TMapView) findViewById(R.id.map_view);
        mLM = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                mapView.setSKPMapApiKey("e8f919b1-5a43-31bc-b9b9-0a9e5d6d55ef");
                mapView.setLanguage(TMapView.LANGUAGE_KOREAN);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                setupMap();
            }
        }.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
 //       mLM.removeUpdates(mListner);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    boolean isInitialized = false;

    private void setupMap() {
        isInitialized = true;
        mapView.setMapType(TMapView.MAPTYPE_STANDARD);
    }
}
