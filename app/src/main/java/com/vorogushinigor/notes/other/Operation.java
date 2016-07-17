package com.vorogushinigor.notes.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by viv on 08.07.2016.
 */
public class Operation {

    public static Bitmap getThumbnails(Context context, int id) {
        return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
    }

    static public int getBitmapWidth(String filePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int width = options.outWidth;
            return width;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    static public int getBitmapHeight(String filePath) {

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int height = options.outHeight;

            return height;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }

    static public Bitmap getBitmapCrop(int height, int width, String filePath, int maxsize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            int k = 0;
            while (height > maxsize || width > maxsize) {
                k = k + 1;
                options.inSampleSize = k;
                BitmapFactory.decodeFile(filePath, options);
                height = options.outHeight;
                width = options.outWidth;
            }
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(filePath, options);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }catch (OutOfMemoryError e){
            e.printStackTrace();
            return null;
        }
    }

    static public int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    static public String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    static public Notes getNotesFromIntent(Intent data) {
        return new Notes(data.getStringExtra(DBHelper.DB_COL_NAME), data.getStringExtra(DBHelper.DB_COL_MAIN), data.getStringExtra(DBHelper.DB_COL_TIME_CREATED), data.getStringExtra(DBHelper.DB_COL_TIME_CHANGED), data.getStringExtra(DBHelper.DB_COL_PATH_IMAGE));
    }

    static public void setIntentFromNotes(Notes notes, Intent intent) {
        intent.putExtra(DBHelper.DB_COL_NAME, notes.getName());
        intent.putExtra(DBHelper.DB_COL_MAIN, notes.getMain());
        intent.putExtra(DBHelper.DB_COL_TIME_CREATED, notes.getTimeCreated());
        intent.putExtra(DBHelper.DB_COL_TIME_CHANGED, notes.getTimeChanged());
        intent.putExtra(DBHelper.DB_COL_PATH_IMAGE, notes.getPathPhoto());
    }

    static public String getStringFromArray(ArrayList<String> arrayList) {
        String str = "";
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == 0)
                str = arrayList.get(i);
            else
                str += "|" + arrayList.get(i);
        }
        return str;
    }

    static public String[] getArrayFromString(String str) {
        return str.split("\\|", -1);
    }
}
