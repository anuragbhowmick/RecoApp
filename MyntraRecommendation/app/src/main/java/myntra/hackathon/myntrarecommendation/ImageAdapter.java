package myntra.hackathon.myntrarecommendation;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anurag on 4/17/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    List<Uri> imageUri = new ArrayList<>();

    public ImageAdapter(Context c, List<Uri> imageUri) {
        mContext = c;
        this.imageUri = imageUri;
    }

    @Override
    public int getCount() {
        return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("AB","imageADapter.getView " + position);
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),imageUri.get(position));
            imageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
//        Picasso.with(mContext).load(imageUri.get(position)).into(imageView);

        return imageView;
    }

    public void setGridData(List<Uri> imageUri) {
        Log.d("AB","ImageAdapter.setGridData");
        this.imageUri = imageUri;
        notifyDataSetChanged();
    }
}