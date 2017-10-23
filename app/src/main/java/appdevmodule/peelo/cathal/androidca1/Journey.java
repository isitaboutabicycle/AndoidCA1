package appdevmodule.peelo.cathal.androidca1;

import android.media.Image;

import java.util.ArrayList;
import java.util.Calendar;

public final class Journey {

    public int StartLat;
    public int StartLong;
    public int EndLat;
    public int EndLong;
    public Calendar Date;
    public ArrayList<Image> Pics;

    public Journey(int startLat, int startLong, int endLat, int endLong, Calendar date, ArrayList<Image> pics) {
        StartLat = startLat;
        StartLong = startLong;
        EndLat = endLat;
        EndLong = endLong;
        Date = date;
        Pics = pics;
    }
}
