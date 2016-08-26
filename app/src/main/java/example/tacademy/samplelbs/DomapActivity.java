package example.tacademy.samplelbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

import example.tacademy.samplelbs.data.POIResult;
import example.tacademy.samplelbs.data.Poi;
import example.tacademy.samplelbs.manager.NetworkManager;
import example.tacademy.samplelbs.manager.NetworkRequest;
import example.tacademy.samplelbs.request.POISearchRequest;

public class DomapActivity extends AppCompatActivity {

    EditText searchEdit;
    ListView listView;
    ArrayAdapter<Poi> mAdapter;

    Map<Poi,Marker> markerResolver = new HashMap<>();
    Map<Marker, Poi> poiResolver = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domap);
        searchEdit = (EditText) findViewById(R.id.edit_search);
        listView = (ListView) findViewById(R.id.list_search);
        mAdapter = new ArrayAdapter<Poi>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);

        Button btn = (Button) findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = searchEdit.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    POISearchRequest request = new POISearchRequest(DomapActivity.this, keyword);
                    NetworkManager.getInstance().getNetworkData(request, new NetworkManager.OnResultListener<POIResult>() {
                        @Override
                        public void onSuccess(NetworkRequest<POIResult> request, POIResult result) {

                            clear();

                            mAdapter.addAll(result.getSearchPoiInfo().getPois().getPoi());
                            for (Poi poi : result.getSearchPoiInfo().getPois().getPoi()) {
//                                addMarker(poi);
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
                final Poi poi = (Poi)listView.getItemAtPosition(position);
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

    private void animateMap(double latitude, double longitude, Runnable runnable) {
    }

    private void moveMap(double latitude, double longitude) {
    }

//    private void addMarker(Poi poi) {
//    }

    private void clear() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Poi poi = mAdapter.getItem(i);
            Marker m = markerResolver.get(poi);
            m.remove();
        }
        mAdapter.clear();
    }



}
