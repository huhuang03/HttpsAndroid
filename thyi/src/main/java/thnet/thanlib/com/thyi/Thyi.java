package thnet.thanlib.com.thyi;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import thnet.thanlib.com.thyi.volley.VolleyObjectRequest;

/**
 * Created by yi on 2/22/16.
 */
public class Thyi {
    public static final String TAG = "Thyi";

    public static final int GET = 0;
    public static final int POST = 1;
    private static RequestQueue requestQueue;

    public static <T>Observable<T> request(Context context, String url, Class<T> clazz) {
        return request(context, POST, url, null, clazz);
    }

    public static <T>Observable<T> request(final Context context, final int method, final String url, Map<String, String> parasm, final Class<T> clazz) {
        Observable.OnSubscribe<T> onSubscribe = new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                initRequestQueue(context);
                VolleyObjectRequest<T> request = new VolleyObjectRequest<>(getRequestMethod(method), url, clazz, subscriber);
                requestQueue.add(request);
            }
        };
        return Observable.create(onSubscribe);
    }

    // MARK: private
    private static void initRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    private static int getRequestMethod(int method) {
        switch (method) {
            case Thyi.GET:
                return Request.Method.GET;
            case Thyi.POST:
                return Request.Method.POST;
            default:
                return Request.Method.POST;
        }
    }

}
