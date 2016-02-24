package thnet.thanlib.com.thyi.volley;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

import rx.Subscriber;
import thnet.thanlib.com.thyi.Thyi;

/**
 * Created by yi on 2/22/16.
 */
public class VolleyObjectRequest<T> extends Request<T>{
    private Class<T> clazz;
    private Map<String, String> params;
    private Subscriber<? super  T> subscriber;

    public VolleyObjectRequest(int method, String url, Class<T> clazz, Subscriber<? super T> subscriber) {
        super(method, url, null);
        this.clazz = clazz;
        this.subscriber = subscriber;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.i(Thyi.TAG, "parseNetworkResponse(), json: " + json);
            return Response.success(JSON.parseObject(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void deliverError(VolleyError error) {
        Throwable t = error;
        if (t == null) {
            t = new Exception(error);
        }
        subscriber.onError(t);
    }

}
