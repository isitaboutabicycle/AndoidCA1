package appdevmodule.peelo.cathal.androidca1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static appdevmodule.peelo.cathal.androidca1.R.id.progressBar;
import static appdevmodule.peelo.cathal.androidca1.R.id.queryButton;
import static appdevmodule.peelo.cathal.androidca1.R.id.responseView;

public class ApiActivity extends AppCompatActivity {

    int feedCode = 1;
    ListView readout = null;
    //a list of HashMaps to store data from the JSON in
    //ArrayList<HashMap<String, String>> stationsList;
    ArrayList<String> stationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        readout = (ListView) findViewById(R.id.responseView);
        //stationsList = new ArrayList<HashMap<String, String>>();
        stationsList = new ArrayList<String>();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button doTheThing = (Button) findViewById(R.id.queryButton);

        doTheThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RetrieveFeedTask goldenRetriever = new RetrieveFeedTask();

                goldenRetriever.execute();
            }
        });
    }



    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            String response = null;

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
                        stringBuilder.append(line);//.append("\n");
                        Log.i("INFO", line);
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();

                    //parsing the string to objects
                    if (response != null){

                        //JSONObject jsonObject = new JSONObject(response);

                        JSONArray stations = new JSONArray(response);

                        for(int i = 0; i < stations.length(); i++) {

                            JSONObject obj = stations.getJSONObject(i);
                            JSONObject posobj = obj.getJSONObject("position");

                            //getting value strings from the obj
                            String address = obj.getString("address");
                            String lat = posobj.getString("lat");
                            String lng = posobj.getString("lng");
                            String availableBikes = obj.getString("available_bikes");
                            String availableStands = obj.getString("available_bike_stands");

                            //creating a key/value hashmap for the station
                            HashMap<String, String> station = new HashMap<String, String>();

                            String parsedResponse =
                                      "Station:\t\t\t\t" + address +
                                    "\nLatitude:\t\t\t" + lat +
                                    "\nLongitude:\t\t" + lng +
                                    "\nFree Bikes:\t\t" + availableBikes +
                                    "\nFree Stands:\t\t" + availableStands;

                            //adding each keyvalue pair to the HashMap
                            station.put("address", address);
                            station.put("latitude", lat);
                            station.put("longitude", lng);
                            station.put("bikesFree", availableBikes);
                            station.put("standsFree", availableStands);

                            //saving station
                            //stationsList.add(station);
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
                return e.getMessage();//changed from null
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
                ArrayAdapter adapter = new ArrayAdapter<String>(ApiActivity.this, R.layout.station_list_view,
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

            //Setting the TextView to the response string
            //setReadout(response);
        }
    }
}
