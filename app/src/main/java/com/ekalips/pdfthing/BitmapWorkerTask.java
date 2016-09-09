package com.ekalips.pdfthing;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by ekalips on 9/9/16.
 */

public class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;
    private int width,height;
    Context context;
    public BitmapWorkerTask(ImageView imageView, Context context) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... params) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), params[0]);
            PDFRecyclerViewAdapter.addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
