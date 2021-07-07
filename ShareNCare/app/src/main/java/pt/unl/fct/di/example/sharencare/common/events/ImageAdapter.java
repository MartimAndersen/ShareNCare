package pt.unl.fct.di.example.sharencare.common.events;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import pt.unl.fct.di.example.sharencare.R;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int[] images = {
            R.drawable.volunteering1, R.drawable.volunteering2, R.drawable.volunteering3,
            R.drawable.volunteering4, R.drawable.volunteering5, R.drawable.volunteering6,
            R.drawable.volunteering7, R.drawable.volunteering8, R.drawable.volunteering9,
            R.drawable.volunteering10, R.drawable.volunteering11, R.drawable.volunteering12,
            R.drawable.volunteering13, R.drawable.volunteering14, R.drawable.volunteering15,
            R.drawable.volunteering16, R.drawable.volunteering17, R.drawable.volunteering18,
            R.drawable.volunteering19, R.drawable.volunteering20, R.drawable.volunteering21,
            R.drawable.volunteering22, R.drawable.volunteering23, R.drawable.volunteering24
    };

    public ImageAdapter(Context mContext){
        this.mContext = mContext;
    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(images[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(340, 350));

        return imageView;
    }
}
