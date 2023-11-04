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

import com.hurricanedev.muslimpro.model.Azkar;
import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.utilities.RoundImage;


public class AzkarsListAdapter extends ArrayAdapter<Azkar> {
    Context context;
    int layoutResourceId;
    List<Azkar> data;

    public AzkarsListAdapter(Context context, int layoutResourceId,
                             List<Azkar> data) {
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
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            holder.txtAzkar = (TextView) row.findViewById(R.id.txtAzkar);
            holder.txtCategory = (TextView) row.findViewById(R.id.txtCategory);

            Typeface font = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Roboto-Light.ttf");
            holder.txtTitle.setTypeface(font);
            holder.txtTitle.setTextSize(16);
            holder.txtAzkar.setTypeface(font);
            holder.txtAzkar.setTextSize(16);
            holder.txtCategory.setTypeface(font);
            holder.txtCategory.setTextSize(14);
            row.setTag(holder);
        } else {
            holder = (ImageHolder) row.getTag();
        }

        Azkar picture = data.get(position);
        holder.txtTitle.setText(picture.getName());
        holder.txtAzkar.setText(picture.getAzkar());
        holder.txtCategory.setText("  " + picture.getCategory() + "  ");

        boolean isExist = false;
        AssetManager assetManager = context.getAssets();
        InputStream imageStream = null;
        try {
            imageStream = assetManager.open("subcategories/"+picture.getFileName()+".png");

            isExist =true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        RoundImage roundedImage;
        if (isExist){
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            roundedImage = new RoundImage(theImage);
            holder.imgIcon.setImageDrawable(roundedImage);
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_quran);
            roundedImage = new RoundImage(bm);
            holder.imgIcon.setImageDrawable(roundedImage);
        }
        return row;
    }

    static class ImageHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtAzkar;
        TextView txtCategory;
    }
}
