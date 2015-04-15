package image.collection.test;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

import image.collection.test.network.NetworkService;
import image.collection.test.network.ResponseReceiver;
import image.collection.test.db.DBHelper;
import image.collection.test.db.FlickrContentProvider;


public class PhotoGridActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {

    private static final int FEED_LOADER = 0;
    private PhotoGridAdapter mAdapter;
    private ResponseReceiver mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_grid);
        mReceiver = new ResponseReceiver(this);
        getSupportLoaderManager().initLoader(FEED_LOADER, null, PhotoGridActivity.this);
        mAdapter = new PhotoGridAdapter(PhotoGridActivity.this, null);
        ((GridView) findViewById(R.id.gridview)).setAdapter(mAdapter);
        ((GridView) findViewById(R.id.gridview)).setOnScrollListener(this);
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ResponseReceiver.ERROR_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void refresh() {
        NetworkService.GetPhotos(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FEED_LOADER:
                return new CursorLoader(this, FlickrContentProvider.PHOTO_URI, null, null, null, DBHelper.TIMESTAMP);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (!data.isAfterLast()) {
            findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            mAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() > (mAdapter.getCount() / 2)) {
                NetworkService.GetPhotos(view.getContext());
            }

        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
