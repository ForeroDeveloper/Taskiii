package com.fordev.taski;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;
    private static  String DATABASE_NAME = "mmydb.db";
    private static  int DATABASE_VERSION = 1;
    private static  String createTableQuery = "create table imageInfo (image BLOB)";
    private ByteArrayOutputStream objectByteArrayOutputStream;
    private byte[] imageInBytes;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createTableQuery);
            Toast.makeText(context, "Table created succesfully inside our database", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void storeImage(ModelClass objectModelClass){
        try {
            SQLiteDatabase objectSqliteDatabase = this.getWritableDatabase();
            Bitmap imageToStoreBitmao = objectModelClass.getImage();

            objectByteArrayOutputStream = new ByteArrayOutputStream();
            imageToStoreBitmao.compress(Bitmap.CompressFormat.JPEG, 100, objectByteArrayOutputStream);

            imageInBytes = objectByteArrayOutputStream.toByteArray();
            ContentValues objectContentValues = new ContentValues();

//            objectContentValues.put("imageName", objectModelClass.getImageName());
            objectContentValues.put("image", imageInBytes);

            long checkIfQueryRuns = objectSqliteDatabase.insert("imageInfo", null, objectContentValues);
            if (checkIfQueryRuns != 0) {
                Toast.makeText(context, "Data added into out table", Toast.LENGTH_SHORT).show();
                objectSqliteDatabase.close();
            }else {
                Toast.makeText(context, "Fails to add data", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(context, e.getMessage() + "Error", Toast.LENGTH_SHORT).show();

        }

    }

}
