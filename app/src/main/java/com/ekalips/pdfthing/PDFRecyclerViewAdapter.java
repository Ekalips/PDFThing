package com.ekalips.pdfthing;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Created by ekalips on 9/9/16.
 */

public class PDFRecyclerViewAdapter extends RecyclerView.Adapter<PDFRecyclerViewAdapter.ViewHolder>  {
    private static final int REQUEST_IMAGE_ACTIVITY_CODE = 1;
    List<PageData> data;
    Context context;
    static private LruCache<String, Bitmap> mMemoryCache;
    MainActivity activity;

    public PDFRecyclerViewAdapter(List<PageData> data, Context context,MainActivity activity) {
        this.data = data;
        this.context = context;
        this.activity = activity;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (!(data.get(holder.getAdapterPosition()).getPhotoUri() == null))
        {
            Uri uri = data.get(holder.getAdapterPosition()).getPhotoUri();
            holder.imageView.setScaleType(ImageView.ScaleType.FIT_START);

            final Bitmap bitmap = getBitmapFromMemCache(uri.toString());
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.add_icon);
                BitmapWorkerTask task;
                task = new BitmapWorkerTask(holder.imageView,context);
                task.execute(uri);
            }

        }
        else
        {
            holder.imageView.setImageResource(R.drawable.add_iamge);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("position", holder.getAdapterPosition());
                        editor.apply();
                        ((Activity) context).startActivityForResult(i,REQUEST_IMAGE_ACTIVITY_CODE);
                    }
                    else
                    {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
                    }
                }
            });
        }
        if (!(data.get(holder.getPosition()).getComment() == null || data.get(holder.getPosition()).getComment().equals("")))
        {
            holder.textView.setText(data.get(holder.getPosition()).getComment());
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void imageChosen(int position, Uri uri)
    {
        data.get(position).setPhotoUri(uri);
        notifyItemChanged(position);
    }

    public void addPage()
    {
        data.add(new PageData());
        notifyDataSetChanged();
    }



    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.comment);
        }
    }
}
