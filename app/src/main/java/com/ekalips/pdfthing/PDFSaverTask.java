package com.ekalips.pdfthing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import static android.content.Context.PRINT_SERVICE;

/**
 * Created by ekalips on 9/9/16.
 */

public class PDFSaverTask extends AsyncTask<PdfDocument, Void, Boolean> {
    Context context;
    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public PDFSaverTask(Context context, AsyncResponse delegate)
    {
        this.context = context;
        this.delegate = delegate;
    }


    @Override
    protected Boolean doInBackground(PdfDocument... pdfDocuments) {
                FileOutputStream out = null;
                try {
                    Calendar c = Calendar.getInstance();
                    File file = new File(Environment.getExternalStorageDirectory().toString() , new Random().nextInt() + ".pdf");
                    out = new FileOutputStream(file);
                    try {
                        pdfDocuments[0].writeTo(out);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        pdfDocuments[0].close();
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        return null;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    public void onPostExecute(Boolean bool) {
        delegate.processFinish("Yay");
    }
}
