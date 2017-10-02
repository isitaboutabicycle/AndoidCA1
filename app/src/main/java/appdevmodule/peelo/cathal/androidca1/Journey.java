package appdevmodule.peelo.cathal.androidca1;

import android.media.Image;

import java.util.ArrayList;
import java.util.Date;

public final class Journey {

    public int StartLat;
    public int StartLong;
    public int EndLat;
    public int EndLong;
    public java.util.Date Date;
    public ArrayList<Image> Pics;

    public Journey(int startLat, int startLong, int endLat, int endLong, java.util.Date date, ArrayList<Image> pics) {
        StartLat = startLat;
        StartLong = startLong;
        EndLat = endLat;
        EndLong = endLong;
        Date = date;
        Pics = pics;
    }
}
