package thnet.thanlib.com.httpsandroid;

import android.widget.TextView;


/**
 * Created by yi on 2/26/16.
 */
public class Log {
    private static TextView textView;

    public static void setTextView(TextView textView) {
        Log.textView = textView;
    }

    public static void clearTextView() {
        Log.textView = null;
    }

    public static void i(String TAG, String log) {
        android.util.Log.i(TAG, log);
        if (textView != null) {
            if (log.startsWith("onCompleted:")) {

            } else {
                textView.setText(log);
            }
        }
    }
}
