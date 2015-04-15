package image.collection.test;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import image.collection.test.db.DBHelper;
import image.collection.test.db.FlickrContentProvider;

public class PlaceholderFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COMMENT_LOADER = 0;
    private CommentAdapter mAdapter;
    private Photo mPhoto;
    public ShareActionProvider mShareActionProvider;
    Uri bmpUri;
    private String HASH_TAG="#FlickrApp";

    public PlaceholderFragment() {
     setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhoto = (Photo) getArguments().getSerializable("photo");
        getLoaderManager().initLoader(COMMENT_LOADER, null, this);
        mAdapter = new CommentAdapter(getActivity(), null);
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = inflater.inflate(R.layout.fragment_photo_comment_land, container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_photo_comment, container, false);
        }
        Photo photo = (Photo) getArguments().getSerializable("photo");
        Picasso.with(getActivity()).load(photo.getLargeUrl()).fit().centerCrop().placeholder(R.drawable.placeholder).into((ImageView) rootView.findViewById(R.id.header_photo_comment_photo));
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
       }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case COMMENT_LOADER:
                return new CursorLoader(getActivity(), FlickrContentProvider.COMMENT_URI, null, DBHelper.PHOTO_ID + " = ?", new String[]{mPhoto.getId()}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (!data.isAfterLast()) {
            mAdapter.changeCursor(data);
        }
      }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
    if (id == R.id.menu_item_share)
    {
        mShareActionProvider = new ShareActionProvider(this.getActivity());
        MenuItemCompat.setActionProvider(item, mShareActionProvider);
        if (mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(setupShareIntent(this.getView()));
        }
    }
        return super.onOptionsItemSelected(item);
}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_photo, menu);
 }

    public Intent setupShareIntent(View view) {
        ImageView ivImage = (ImageView)  view.findViewById(R.id.header_photo_comment_photo);
        bmpUri =  getLocalBitmapUri(ivImage);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, HASH_TAG + " "+ R.string.sharetext + " " + mPhoto.getLargeUrl());
        shareIntent.setType("image/*");
        return shareIntent;
      }

    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image.png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
