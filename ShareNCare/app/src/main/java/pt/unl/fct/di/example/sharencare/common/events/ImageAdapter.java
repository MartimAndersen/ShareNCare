package pt.unl.fct.di.example.sharencare.common.events;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

import pt.unl.fct.di.example.sharencare.R;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int[] image = {
            R.drawable.volunteering1, R.drawable.volunteering2, R.drawable.volunteering3,
            R.drawable.volunteering4, R.drawable.volunteering5, R.drawable.volunteering6,
            R.drawable.volunteering7, R.drawable.volunteering8, R.drawable.volunteering9,
            R.drawable.volunteering10, R.drawable.volunteering11, R.drawable.volunteering12,
            R.drawable.volunteering13, R.drawable.volunteering14, R.drawable.volunteering15,
            R.drawable.volunteering16, R.drawable.volunteering17, R.drawable.volunteering18,
            R.drawable.volunteering19, R.drawable.volunteering20, R.drawable.volunteering21,
            R.drawable.volunteering22, R.drawable.volunteering23, R.drawable.volunteering24
    };
    private List<String> images;

    public ImageAdapter(Context mContext, List<String> images){
        this.mContext = mContext;
        this.images = images;
    }

    public int rotate(String imagePath){
        int rotate = 0;

        try {
            ExifInterface exif = null;
            exif = new ExifInterface(imagePath);
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(mContext);
        String imagePath = images.get(position);
        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(myBitmap);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setRotation(rotate(imagePath));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(340, 350));

        return imageView;
    }
}
