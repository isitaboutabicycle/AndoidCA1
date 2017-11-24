package appdevmodule.peelo.cathal.androidca1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

    private ListView readout = null;

    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private ChildEventListener cel;

    private ArrayList<String> journeysList;
    private refreshList async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_history);

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

        async = new refreshList();
        async.execute();

        Button refreshBtn = (Button) findViewById(R.id.queryButton);

        refreshBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                async = new refreshList();
                async.execute();
            }
        });
    }


    class refreshList extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.historyProgressBar).setVisibility(View.VISIBLE);

            //resetting list to avoid duplication
            journeysList = new ArrayList<String>();
        }

        @Override
        protected String doInBackground(Void... params) {

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
                        int picsNo;

                        try{
                            picsNo = mPics.size();
                        }
                        catch (NullPointerException e){
                            picsNo = 0;
                        }

                        String parsedResponse =
                                "Date:\t\t\t\t\t\t\t\t" + mDate +
                                        "\nStart coordinates:\t\t" + "("+mStartLat+" , "+mStartLong+")" +
                                        "\nEnd coordinates:\t\t\t" + "("+mEndLat+" , "+mEndLong+")" +
                                        "\nPictures attached:\t\t" + picsNo;

                        //saving node details
                        journeysList.add(parsedResponse);

                        ArrayAdapter adapter = new ArrayAdapter<String>(JourneyHistoryActivity.this, R.layout.station_list_view,
                                journeysList
                        );

                        readout.setAdapter(adapter);

                    }catch(Exception e){
                        Toast.makeText(JourneyHistoryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(JourneyHistoryActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            //attaching listener to Firebase database
            ref.addChildEventListener(cel);

            return null;
        }

        @Override
        protected void onPostExecute(String response){
            super.onPostExecute(response);

            //made visible in onPreExecute()
            findViewById(R.id.historyProgressBar).setVisibility(View.GONE);
        }
    }

}
