package appdevmodule.peelo.cathal.androidca1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public final class Journey {

    private int StartLat;
    private int StartLong;
    private int EndLat;
    private int EndLong;
    private Calendar Date;
    private ArrayList<String> Pics;

    public Journey(){
        StartLat = 0;
        StartLong = 0;
        EndLat = 0;
        EndLong = 0;
        Date = Calendar.getInstance();
        Pics = new ArrayList<String>();
    }

    public Journey(int startLat, int startLong, int endLat, int endLong, Calendar date, ArrayList<Bitmap> pics) {
        StartLat = startLat;
        StartLong = startLong;
        EndLat = endLat;
        EndLong = endLong;
        Date = date;
        Pics = new ArrayList<String>();

        //converting the Bitmaps to Strings
        for(Bitmap bmp1 : pics){
            Pics.add(bitmapToString(bmp1));
        }
    }

    //turns out Firebase doesn't like Bitmaps. Doesn't warn you about it mind
    private String bitmapToString(Bitmap bmp){
        ByteArrayOutputStream bmpBytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bmpBytes);
        byte[] byteArray = bmpBytes.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public int getStartLat(){
        return StartLat;
    }

    public int getStartLong(){
        return StartLong;
    }

    public int getEndLat(){
        return EndLat;
    }

    public int getEndLong(){
        return EndLong;
    }

    public Calendar getDate(){
        return Date;
    }

    public ArrayList<String> getPics(){
        return Pics;
    }
}
