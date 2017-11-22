package appdevmodule.peelo.cathal.androidca1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ApiActivity extends AppCompatActivity {

    private ListView readout = null;
    private ArrayList<String> stationsList;
    private ArrayAdapter adapter;
    private JSONArray stations;
    private RetrieveFeedTask goldenRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        //if not logged in, redirect to Login Activity
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (null == user) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        readout = (ListView) findViewById(R.id.responseView);

        //making the readout clickable
        readout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj;
                JSONObject posobj;
                String lat = "";
                String lng = "";
                String name = "";

                try{
                    obj = stations.getJSONObject(position);
                    posobj = obj.getJSONObject("position");
                    lat = posobj.getString("lat");
                    lng = posobj.getString("lng");
                    name = obj.getString("address") + " Bike Station";
                }
                catch(JSONException e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                Uri myUri = Uri.parse("geo:"+lat+","+lng+"?q="+lat+","+lng+"("+name+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, myUri);
                startActivity(mapIntent);
            }
        });

        //stationsList = new ArrayList<HashMap<String, String>>();
        stationsList = new ArrayList<String>();

        //show stations on ListView
        goldenRetriever = new RetrieveFeedTask();
        goldenRetriever.execute();

        Button doTheThing = (Button) findViewById(R.id.queryButton);

        doTheThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //refresh ListView
                goldenRetriever = new RetrieveFeedTask();
                goldenRetriever.execute();
            }
        });
    }



    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            String response = null;
            stationsList = new ArrayList<String>();

            try {
                URL url = new URL("https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=fcd108bd549949a9d7ee003a683cdece0fea1ff2");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                try {
                    InputStream myIS = urlConnection.getInputStream();
                    InputStream myBIS = new BufferedInputStream(myIS);

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myBIS));
                    StringBuilder stringBuilder = new StringBuilder();

                    //parsing the response to a string
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        Log.i("INFO", line);
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();

                    //parsing the string to objects
                    if (response != null){

                        stations = new JSONArray(response);

                        for(int i = 0; i < stations.length(); i++) {

                            JSONObject obj = stations.getJSONObject(i);

                            //getting value strings from the obj
                            String address = obj.getString("address");
                            String availableBikes = obj.getString("available_bikes");
                            String availableStands = obj.getString("available_bike_stands");

                            String parsedResponse =
                                      "Station:\t\t\t\t" + address +
                                    "\nFree Bikes:\t\t" + availableBikes +
                                    "\nFree Stands:\t\t" + availableStands;

                            //saving station
                            stationsList.add(parsedResponse);
                        }
                    }
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return e.getMessage();
            }

            return response;
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if(response == null)
            { stationsList.add("Oops, JCDecaux aren't talking to us..."); }

            findViewById(R.id.progressBar).setVisibility(View.GONE);

            Log.i("INFO", response);

            try{
                adapter = new ArrayAdapter<String>(ApiActivity.this, R.layout.station_list_view,
                        stationsList
                        //        new String[]{"address", "status", "bikesFree", "standsFree"}
                        //        new String[]{R.id.address, R.id.status, R.id.bikesFree, R.id.standsFree}
                );

                readout.setAdapter(adapter);
            }
            catch(Exception e)
            {
                Log.i("INFO", e.getMessage());
            }
        }
    }
}
