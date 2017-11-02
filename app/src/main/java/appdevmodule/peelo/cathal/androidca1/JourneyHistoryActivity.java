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
import java.util.HashMap;
import java.util.Locale;

public class JourneyHistoryActivity extends AppCompatActivity {

    private int feedCode = 1;
    private ListView readout = null;
    private FirebaseUser user = null;
    private DatabaseReference ref;
    private SimpleDateFormat dateFormatter;

    private ArrayList<Journey> journeyObjs;
    private ArrayList<String> journeysList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_history);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //TODO: remove these two lines
        journeyObjs = new ArrayList<Journey>();
        journeyObjs.add(new Journey());

        //if not logged in, redirect to Login Activity
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (null == user) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        readout = (ListView) findViewById(R.id.responseView);

        journeysList = new ArrayList<String>();
        ref = FirebaseDatabase.getInstance().getReference();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);

        Button refreshBtn = (Button) findViewById(R.id.queryButton);
        refreshBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //JourneyHistoryActivity.RetrieveJourneysTask retrieveFirebase = new JourneyHistoryActivity.RetrieveJourneysTask();
                //retrieveFirebase.execute();



                //start

                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                //

                try {
                    //getting all children of the user's own "journeys" node (hopefully)
                    ref.child(user.getUid())
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
                            });

                    try {

                        int size = journeyObjs.size();
                        //iterating over the objects
                        for(int i = 0; i < size; i++) {

                            Journey j = journeyObjs.get(i);

                            String parsedResponse =
                                    "Date:\t\t\t\t\t\t\t\t" + dateFormatter.format(j.getDate().getTime()) +
                                            "\nStart Coordinates:\t\t" + "("+j.getStartLat()+" , "+j.getStartLong()+")" +
                                            "\nEnd Coordinates:\t\t" + "("+j.getEndLat()+" , "+j.getEndLong()+")" +
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
