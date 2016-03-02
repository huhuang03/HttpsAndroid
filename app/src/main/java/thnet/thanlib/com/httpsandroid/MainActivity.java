package thnet.thanlib.com.httpsandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import rx.Subscriber;
import thnet.thanlib.com.thyi.Thyi;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        Log.setTextView(tv);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                getIp(MainActivity.this);
//                getWikiHttps(MainActivity.this);
//                get12306Https(MainActivity.this);
                get12306OverSelfCA(MainActivity.this);
//                getServerHasNoCA(MainActivity.this);
            }
        });
    }

    private void getIp(Context context) {
        String url = "http://freegeoip.net/json/";
        Thyi.request(context, Thyi.GET, url, null, LocData.class)
                .subscribe(new Subscriber<LocData>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.i(TAG, "onError: " );
                    }

                    @Override
                    public void onNext(LocData locData) {
                        Log.i(TAG, "onNext(): " + locData);
                    }
                });
    }

    /**
     * GlobalSign Organization Validation CA - SHA256 - G2 颁发
     * @param context
     */
    public void getWikiHttps(Context context) {
        Thyi.request(context, "https://wikipedia.org")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: " + s);
                    }
                });
    }

    /**
     * 不被信任的证书,自己给自己颁的
     * Sinorail Certification Authority/CN=SRCA
     * @param context
     */
    public void get12306Https(Context context) {
        Thyi.request(context, "https://kyfw.12306.cn/otn")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: ");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "onNext: ");
                    }
                });
    }

    /**
     * 自己设置信任
     * @param context
     */
    public void get12306OverSelfCA(Context context) {
        //load证书
        InputStream caInput = null;
        Certificate ca = null;
        try {
            caInput = new BufferedInputStream(getAssets().open("kyfw.12306.cn.cer"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
        } catch (Exception e)  {
            e.printStackTrace();
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //创建包含我们证书的KeyStore
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

            //创建TrustManager,信任我们的证书
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://kyfw.12306.cn/otn");
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                        InputStream in = urlConnection.getInputStream();
                        copyInputStreamToOutputStream(in, System.out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器没有我们的信任的证书
     * @param context
     */
    public void getServerHasNoCA(Context context) {
        //load证书
        InputStream caInput = null;
        Certificate ca = null;
        try {
            caInput = new BufferedInputStream(getAssets().open("https://127.0.0.1/phpHttpsTest/index.php"));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ca = cf.generateCertificate(caInput);
        } catch (Exception e)  {
            e.printStackTrace();
        } finally {
            try {
                caInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //创建包含我们证书的KeyStore
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

            //创建TrustManager,信任我们的证书
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://127.0.0.1/phpHttpsTest/index.php");
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                        InputStream in = urlConnection.getInputStream();
                        copyInputStreamToOutputStream(in, System.out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void copyInputStreamToOutputStream(InputStream in, PrintStream out) {
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = in.read(buffer)) != 0) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.clearTextView();
        super.onDestroy();
    }
}
