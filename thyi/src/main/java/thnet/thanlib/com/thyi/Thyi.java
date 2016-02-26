package thnet.thanlib.com.thyi;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import thnet.thanlib.com.thyi.volley.VolleyObjectRequest;

/**
 * Created by yi on 2/22/16.
 */
public class Thyi {
    public static final String TAG = "Thyi";

    public static final int GET = 0;
    public static final int POST = 1;
    private static RequestQueue requestQueue;

    public static Observable<String> request(Context context, String url) {
//        return request(context, POST, url, null, String.class);
        return requestStringInternal(context, POST, url, null);
    }

    public static <T>Observable<T> request(Context context, String url, Class<T> clazz) {
        return request(context, POST, url, null, clazz);
    }

    public static <T>Observable<T> request(final Context context, final int method, final String url, Map<String, String> parasm, final Class<T> clazz) {
        Observable.OnSubscribe<T> onSubscribe = new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                initRequestQueue(context);
                VolleyObjectRequest<T> request = new VolleyObjectRequest<>(getRequestMethod(method), url, clazz, subscriber);
                request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
                requestQueue.add(request);
            }
        };
        return Observable.create(onSubscribe).subscribeOn(AndroidSchedulers.mainThread());
    }

    // MARK: private
    private static Observable<String> requestStringInternal(final Context context, final int method, final String url, Map<String, String> parasm) {
        Observable.OnSubscribe<String> onSubscribe = new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                initRequestQueue(context);
                StringRequest stringRequest = new StringRequest(getRequestMethod(method), url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                subscriber.onNext(response);
                                subscriber.onCompleted();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Throwable t = error;
                                if (t == null) {
                                    t = new Exception(error);
                                }
                                subscriber.onError(t);
                            }
                        });
                requestQueue.getCache().clear();
                requestQueue.add(stringRequest);
            }
        };
        return Observable.create(onSubscribe).subscribeOn(AndroidSchedulers.mainThread());
    }

    private static void initRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
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
