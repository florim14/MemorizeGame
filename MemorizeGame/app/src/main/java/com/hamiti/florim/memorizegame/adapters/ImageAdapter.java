package com.hamiti.florim.memorizegame.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hamiti.florim.memorizegame.R;
import com.hamiti.florim.memorizegame.models.Images;

import java.util.ArrayList;

/**
 * Created by Florim on 5/26/2018.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private int width, height;
    private String levelOfGame = "80";

    public ImageAdapter(Context c, int width, int height, String levelOfGame) {
        mContext = c;
        this.levelOfGame = levelOfGame;
        this.width = width;
        this.height = height;
    }

    private ArrayList<Images> imagesData = new ArrayList<>();
    private LayoutInflater mInflaterCatalogListItems;

    public ImageAdapter(Context context, ArrayList<Images> imageData, int width, int height, String levelOfGame) {
        imagesData = imageData;
        this.levelOfGame = levelOfGame;
        mInflaterCatalogListItems = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.width = width;
        this.height = height;
    }

    //This function will determine how many items to be displayed
    @Override
    public int getCount() {
        return imagesData.size();
    }

    @Override
    public Object getItem(int position) {
        return imagesData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflaterCatalogListItems.inflate(R.layout.adapter_images,
                    null);
            holder.imagePhoto = (ImageView) convertView.findViewById(R.id.photoView);
            holder.imagePhoto.getLayoutParams().width = width / 4;
            holder.imagePhoto.getLayoutParams().height = height / Integer.parseInt(levelOfGame);
            holder.imagePhoto.setPadding(10,10,10,10);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (imagesData.get(position) != null) {
            Log.d("Position: " , position + "");
            holder.imagePhoto.setImageBitmap(imagesData.get(position).getBitmapImage());
            holder.imagePhoto.setPadding(10,10,10,10);
            holder.imagePhoto.setImageResource(R.drawable.back_card);
        }

        return convertView;
    }

    //View Holder class used for reusing the same inflated view. It will decrease the inflation overhead @getView
    private static class ViewHolder {
        ImageView imagePhoto;

    }
}