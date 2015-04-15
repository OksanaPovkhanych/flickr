package image.collection.test.network;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import image.collection.test.R;


public class ResponseReceiver extends BroadcastReceiver {
    public static final String ERROR_RESPONSE =
            "image.collection.test.action.ERROR";

    private final Activity mActivity;

    public ResponseReceiver(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String error = intent.getStringExtra(NetworkService.ERROR_TEXT);

        Toast.makeText(context, error, Toast.LENGTH_LONG).show();

        if (mActivity.findViewById(R.id.progressbar) != null)
            mActivity.findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        if (mActivity.findViewById(R.id.gridview) != null)
            mActivity.findViewById(R.id.gridview).setVisibility(View.VISIBLE);
    }
}
