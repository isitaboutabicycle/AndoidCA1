package appdevmodule.peelo.cathal.androidca1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class JourneyHistoryActivity extends AppCompatActivity {

    private int feedCode = 1;
    private ListView readout = null;
    private SimpleDateFormat dateFormatter;

    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ChildEventListener cel;

    private ArrayList<Journey> journeyObjs;
    private ArrayList<String> journeysList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_history);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        journeyObjs = new ArrayList<Journey>();

        //if not logged in, redirect to Login Activity
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (null == user) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        readout = (ListView) findViewById(R.id.responseView);

        journeysList = new ArrayList<String>();
        //finding the user's own "journeys" node
        database = FirebaseDatabase.getInstance();
        ref = database.getReference()
                .child(user.getUid())
                .child("journeys");
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);

        Button refreshBtn = (Button) findViewById(R.id.queryButton);

        cel = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{
                    int mStartLat = (int)(long) dataSnapshot.child("startLat").getValue();
                    int mStartLong = (int)(long) dataSnapshot.child("startLong").getValue();
                    int mEndLat = (int)(long) dataSnapshot.child("endLat").getValue();
                    int mEndLong = (int)(long) dataSnapshot.child("endLong").getValue();
                    String mDate = dataSnapshot.child("date").getValue(String.class);
                    ArrayList<String> mPics = (ArrayList<String>) dataSnapshot.child("pics").getValue();

                    Journey journey = new Journey(mPics, mStartLat, mStartLong, mEndLat, mEndLong, mDate);
                    //Journey journey = dataSnapshot.getValue(Journey.class); Doesn't work for some reason
                    
                    journeyObjs.add(journey);
                }catch(Exception e){
                    Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(JourneyHistoryActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        ref.addChildEventListener(cel);

        refreshBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //JourneyHistoryActivity.RetrieveJourneysTask retrieveFirebase = new JourneyHistoryActivity.RetrieveJourneysTask();
                //retrieveFirebase.execute();



                //start

                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                //

                try {


                    /*ref.child(user.getUid())
                            .child("journeys")
                            .orderByChild("date")
                            .addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //iterating over the child nodes and adding them to the static ArrayList
                                    for(DataSnapshot dss : dataSnapshot.getChildren()){
                                        journeyObjs.add(dss.getValue(Journey.class));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError e) {
                                    Log.e("ERROR", e.getMessage(), new Exception());
                                    Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            });*/


                    try {
                        journeysList = new ArrayList<String>();

                        int size = journeyObjs.size();
                        //iterating over the objects
                        for(int i = 0; i < size; i++) {

                            Journey j = journeyObjs.get(i);

                            String parsedResponse =
                                    "Date:\t\t\t\t\t\t\t\t" + j.getDate() +
                                            "\nStart coordinates:\t\t" + "("+j.getStartLat()+" , "+j.getStartLong()+")" +
                                            "\nEnd coordinates:\t\t\t" + "("+j.getEndLat()+" , "+j.getEndLong()+")" +
                                            "\nPictures attached:\t\t" + j.getPics().size();

                            //saving node details
                            journeysList.add(parsedResponse);
                        }
                    }
                    catch (Exception e){
                        Log.e("ERROR", e.getMessage(), e);
                        Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
                catch(Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                }

                //

                //if(response == null)
                //{ journeysList.add("Could not extract data from Firebase"); }

                findViewById(R.id.progressBar).setVisibility(View.GONE);

                try{
                    ArrayAdapter adapter = new ArrayAdapter<String>(JourneyHistoryActivity.this, R.layout.station_list_view,
                            journeysList
                    );

                    readout.setAdapter(adapter);
                }
                catch(Exception e){
                    Log.i("INFO", e.getMessage());
                }

                //end



            }
        });
    }
/*
    class RetrieveJourneysTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                //getting all children of the user's own "journeys" node (hopefully)
                ref.child(user.getUid()).child("journeys").orderByChild("date")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //iterating over the child nodes and adding them to the static ArrayList
                        for(DataSnapshot dss : dataSnapshot.getChildren()){
                            journeyObjs.add(dss.getValue(Journey.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError e) {
                        Log.e("ERROR", e.getMessage(), new Exception());
                        Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });

                try {

                    int size = journeyObjs.size();
                    //iterating over the objects
                    for(int i = 0; i < size; i++) {

                        Journey j = journeyObjs.get(i);

                        String parsedResponse =
                                              "Start Coordinates:\t" + j.getStartLat()+","+j.getStartLong() +
                                            "\nEnd Coordinates:\t" + j.getEndLat()+","+j.getEndLong() +
                                            "\nDate:\t\t\t" + j.getDate().toString() +
                                            "\nPictures attached:\t" + j.getPics().size();

                        //saving node details
                        journeysList.add(parsedResponse);
                    }
                }
                catch (Exception e){
                    Log.e("ERROR", e.getMessage(), e);
                    Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    return e.getMessage();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                return e.getMessage();
            }

            return null;
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response == null)
            { journeysList.add("Could not extract data from Firebase"); }

            findViewById(R.id.progressBar).setVisibility(View.GONE);

            try{
                ArrayAdapter adapter = new ArrayAdapter<String>(JourneyHistoryActivity.this, R.layout.station_list_view,
                        journeysList
                );

                readout.setAdapter(adapter);
            }
            catch(Exception e){
                Log.i("INFO", e.getMessage());
            }
        }
    }*/
}
