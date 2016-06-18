package com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketActivity extends AppCompatActivity {
    ListView lv;
    ArrayAdapter<String> adapter;
    TextView btnPrint;
    LinearLayout view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        lv = (ListView) findViewById(R.id.ListView1);
        btnPrint = (TextView) findViewById(R.id.idPrint);
        view = (LinearLayout)findViewById(R.id.idViewTicket);
        btnPrint.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                v.setDrawingCacheEnabled(true);
                v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
                v.buildDrawingCache(true);
                Bitmap b = viewToBitmap(view);
                storeImage(b);
                //Toast.makeText(TicketActivity.this, "Crear la imagen del Layout", Toast.LENGTH_SHORT).show();

            }
        });



        String[] datos ={"ARTICULO 1","ARTICULO 2","ARTICULO 3","ARTICULO 4","ARTICULO 5","ARTICULO 6","ARTICULO 7"};
        adapter = new ArrayAdapter<String>(TicketActivity.this, android.R.layout.simple_list_item_1,datos);
        lv.setAdapter(adapter);





    }
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.rgb(255,255,255));
        canvas.setDensity(24);
        view.draw(canvas);
        return bitmap;
    }
    private void storeImage(Bitmap image) {

        try {
            FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/DCIM/file.PNG");
            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/file.PNG");
            image.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();

            file.setReadable(true, false);
            Toast.makeText(TicketActivity.this, file.toString(), Toast.LENGTH_LONG).show();
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(intent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
