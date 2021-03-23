package com.fordev.taski;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

public class GeneradorDeQr extends AppCompatActivity {

    String key = null;
    String nombreProducto = null;
    ImageView img_qr_code;
    TextView nomProducto;
    FloatingActionButton faq_guardar;
    RelativeLayout qr_contenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generador_de_qr);

        Bundle bundle = getIntent().getExtras();
        key = bundle.getString("codeQr");
        nombreProducto = bundle.getString("nombreProducto");
        img_qr_code = findViewById(R.id.img_qr_code);
        nomProducto = findViewById(R.id.nombreProducto);
        nomProducto.setText(nombreProducto);
        nomProducto.setText(nombreProducto);
        faq_guardar = findViewById(R.id.faq_guardar);
        qr_contenido = findViewById(R.id.qr_contenido);

        faq_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(GeneradorDeQr.this,"Cargando...", Toast.LENGTH_LONG, true).show();
                getBitmapFromView(qr_contenido);
            }
        });
    }

    private Bitmap getBitmapFromView(View view){
        Bitmap returnBitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnBitmap);
        Drawable bgdrawable = view.getBackground();
        if (bgdrawable!=null){
            bgdrawable.draw(canvas);
        }else {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);

        Date currentTime;
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(),returnBitmap,nombreProducto + "_QR_CODE_" + (currentTime = Calendar.getInstance().getTime()),  null);

        Uri uri = Uri.parse(bitmapPath);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Codigo Qr de: " + nombreProducto);
        startActivity(Intent.createChooser(intent, "Enviar Qr..."));

        return  returnBitmap;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (key!=null){
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE, 500,500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                img_qr_code.setImageBitmap(bitmap);

            }catch (WriterException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}