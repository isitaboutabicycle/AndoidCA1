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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    TextView readout = null;
    //a list of HashMaps to store data from the JSON in
    ArrayList<HashMap<String, String>> stationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        readout = (TextView) findViewById(R.id.responseView);
        stationsList = new ArrayList<HashMap<String, String>>();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Button doTheThing = (Button) findViewById(R.id.queryButton);

        doTheThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RetrieveFeedTask goldenRetriever = new RetrieveFeedTask();

                goldenRetriever.execute();
                /* for parsing JSON into an object

                 try {
                 JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                 String requestID = object.getString("requestId");
                 int likelihood = object.getInt("likelihood");
                 JSONArray photos = object.getJSONArray("photos");
                 .
                 .
                 .
                 .
                 } catch (JSONException e) {
                 // Appropriate error handling code
                 }

                 */
            }
        });
    }

    private void setReadout(String filling){
        if (readout != null){
            readout.setText(filling);
        }
    }



    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();//from helper
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            setReadout("");
        }

        protected String doInBackground(Void... urls) {

            String response = null;

            try {
                URL url = new URL("https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=fcd108bd549949a9d7ee003a683cdece0fea1ff2");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                try {
                    InputStream myIS = urlConnection.getInputStream();
                    //InputStreamReader myISReader = new InputStreamReader(myIS);
                    //BufferedReader bufferedReader = new BufferedReader(myISReader);
                    InputStream myBIS = new BufferedInputStream(myIS);

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myBIS));
                    StringBuilder stringBuilder = new StringBuilder();

                    //parsing the response to a string
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();

                    //parsing the string to objects
                    if (response != null){

                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray stations = jsonObject.getJSONArray("stations");

                        for(int i = 0; i < stations.length(); i++) {

                            JSONObject obj = stations.getJSONObject(i);

                            //getting value strings from the obj
                            String address = obj.getString("address");
                            String status = obj.getString("status");
                            String availableBikes = obj.getString("available_bikes");
                            String availableStands = obj.getString("available_bike_stands");

                            //creating a key/value hashmap for the station
                            HashMap<String, String> station = new HashMap<String, String>();

                            //adding each keyvalue pair to the HashMap
                            station.put("Address", address);
                            station.put("Status", status);
                            station.put("Bikes Free", availableBikes);
                            station.put("Stands Free", availableStands);

                            //saving station
                            stationsList.add(station);
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
            { response = "Oops, that didn't go to plan"; }

            findViewById(R.id.progressBar).setVisibility(View.GONE);

            Log.i("INFO", response);

            //Setting the TextView to the response string
            setReadout(response);
        }
    }
}
