package image.collection.test;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.photos.Photo;
import com.squareup.picasso.Picasso;

import image.collection.test.network.NetworkService;


class PhotoGridAdapter extends CursorAdapter {

    public PhotoGridAdapter(Context context, Cursor cursor) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = new MyImageView(context);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.image = (MyImageView) view;
        view.setTag(viewHolder);
        view.setPadding(4,4,4,4);
        ((ImageView) view).setScaleType(ImageView.ScaleType.CENTER_CROP);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHold = (ViewHolder) view.getTag();
        User user = new User();
        user.setId(cursor.getString(PhotoQuery.OWNER));
        user.setUsername(cursor.getString(PhotoQuery.OWNER_NAME));
        Photo photo = new Photo();
        photo.setId(cursor.getString(PhotoQuery.ID));
        photo.setUrl(cursor.getString(PhotoQuery.URL));
        photo.setFarm(cursor.getString(PhotoQuery.FARM));
        photo.setTitle(cursor.getString(PhotoQuery.TITLE));
        photo.setOwner(user);
        photo.setServer(cursor.getString(PhotoQuery.SERVER));
        photo.setSecret(cursor.getString(PhotoQuery.SECRET));
        Picasso.with(context).load(photo.getLargeSquareUrl()).placeholder(R.drawable.placeholder).fit().into(viewHold.image);
        view.setOnClickListener(new PhotoOnClickListener(photo, context));
    }

    private interface PhotoQuery {
        static final int ID = 0;
        static final int URL = 1;
        static final int FARM = 2;
        static final int TITLE = 3;
        static final int OWNER = 4;
        static final int OWNER_NAME = 5;
        static final int SERVER = 6;
        static final int SECRET = 7;
    }

    private static class ViewHolder {
        MyImageView image;
    }

    private class PhotoOnClickListener implements View.OnClickListener {
        private final Photo item;
        private final Context context;

        public PhotoOnClickListener(Photo item, Context context) {
            this.item = item;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            NetworkService.GetPhotoComments(context, item.getId());
            Intent intent = new Intent(context, PhotoCommentActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("photo", item);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }
    
}
