package thnet.thanlib.com.thyi;

import android.test.AndroidTestCase;
import android.util.Log;

import java.util.concurrent.Semaphore;

import rx.Subscriber;

/**
 * Created by yi on 2/22/16.
 */
public class ThyiTest extends AndroidTestCase{
    private static final String TAG = "ThyiTest";
    private Semaphore semaphore;

    public void test_test() {
        semaphore = new Semaphore(0);
        String url = "http://freegeoip.net/json/";
        Log.i(TAG, "here, context: " + getContext());
        Thyi.request(getContext(), Thyi.GET, url, null, LocData.class)
                .subscribe(new Subscriber<LocData>() {
                    @Override
                    public void onCompleted() {
                        semaphore.release();
                    }

                    @Override
                    public void onError(Throwable e) {
                        assertNotNull(null);
                        semaphore.release();
                    }

                    @Override
                    public void onNext(LocData locData) {
                        Log.i(TAG, "onNext(): " + locData);
                        assertNotNull(locData.getIp());
                    }
                });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
