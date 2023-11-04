package com.hurricanedev.muslimpro.adapter;


import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hurricanedev.muslimpro.model.Category;
import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.utilities.RoundImage;

public class CategoriesListAdapter extends ArrayAdapter<Category> {
    Context context;
    int layoutResourceId;
    List<Category> data;

    public CategoriesListAdapter(Context context, int layoutResourceId,
                                 List<Category> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ImageHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ImageHolder();
            holder.txtName = (TextView) row.findViewById(R.id.CatName);
            holder.imgCat = (ImageView) row.findViewById(R.id.imgCat);
            holder.txtCounter = (TextView) row.findViewById(R.id.txtCounter);
            Typeface font = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto-Light.ttf");
            holder.txtName.setTypeface(font);
            row.setTag(holder);
        } else {
            holder = (ImageHolder) row.getTag();
        }

        Category picture = data.get(position);
        holder.txtName.setText(picture.getName());
        holder.txtCounter.setText("   " + picture.getCount() + "   ");

        boolean isExist = false;
        AssetManager assetManager = context.getAssets();
        InputStream imageStream = null;
        try {
            imageStream = assetManager.open("categories/"+picture.getFileName()+".png");

            isExist =true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        RoundImage roundedImage;
        if (isExist){
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            roundedImage = new RoundImage(theImage);
            holder.imgCat.setImageDrawable(roundedImage);
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.mipmap.uncategorized);
            roundedImage = new RoundImage(bm);
            holder.imgCat.setImageDrawable(roundedImage);
        }

        return row;
    }

    static class ImageHolder {
        TextView txtCounter;
        ImageView imgCat;
        TextView txtName;

    }
}
