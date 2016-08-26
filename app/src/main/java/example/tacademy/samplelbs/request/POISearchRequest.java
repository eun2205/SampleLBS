package example.tacademy.samplelbs.request;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import example.tacademy.samplelbs.data.POIResult;
import example.tacademy.samplelbs.manager.NetworkRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016-08-19.
 */
public class POISearchRequest extends NetworkRequest<POIResult>{
    Request request;
    public POISearchRequest(Context context, String keyword) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("apis.skplanetx.com")
                .addPathSegments("/tmap/pois")
                .addQueryParameter("version","1")
                .addQueryParameter("searchKeyword", keyword)
                .addQueryParameter("resCoordType","WGS84GEO")
                .build();

        request = new Request.Builder()
                .url(url)
                .header("Accept","application/json")
                .header("appKey","2bc7afe3-fc89-3125-b699-b9fb7cfe2fae")
                .tag(context)
                .build();

    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    protected POIResult parse(ResponseBody body) throws IOException {
        Gson gson = new Gson();
        POIResult result = gson.fromJson(body.charStream(), POIResult.class);
        return result;
    }
}
