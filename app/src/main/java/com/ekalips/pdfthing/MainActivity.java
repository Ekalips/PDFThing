package com.ekalips.pdfthing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    static Context context;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initToolbar();



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<PageData> datas = new ArrayList<>();
        datas.add(new PageData());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(12));
        recyclerView.setAdapter(new PDFRecyclerViewAdapter(datas,this,this));
    }


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ((PDFRecyclerViewAdapter) recyclerView.getAdapter()).addPage();
                return true;

            case R.id.action_save:
                Toast.makeText(context,"Saving PDF to /0/ folder",Toast.LENGTH_SHORT).show();
                PrintAttributes printAttrs = new PrintAttributes.Builder().
                        setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                        setMediaSize(PrintAttributes.MediaSize.ISO_A4).
                        setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 450, 700)).
                        setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                        build();

                PdfDocument document = new PrintedPdfDocument(MainActivity.this, printAttrs);



                for (int i = 0; i < recyclerView.getLayoutManager().getChildCount(); i++) {
                    View content = recyclerView.getLayoutManager().getChildAt(i);

                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), 1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    content.draw(page.getCanvas());
                    document.finishPage(page);
                }


                PDFSaverTask task = new PDFSaverTask(context, new PDFSaverTask.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        Toast.makeText(context,"PDF saved",Toast.LENGTH_LONG).show();
                    }
                });
                task.execute(document);






                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                int position = sharedPref.getInt("position", -1);
                ((PDFRecyclerViewAdapter) recyclerView.getAdapter()).imageChosen(position, data.getData());
            }
            else Toast.makeText(this,"Please, select image",Toast.LENGTH_LONG).show();
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }





}
