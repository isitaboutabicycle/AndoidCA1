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

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static appdevmodule.peelo.cathal.androidca1.R.id.progressBar;
import static appdevmodule.peelo.cathal.androidca1.R.id.queryButton;
import static appdevmodule.peelo.cathal.androidca1.R.id.responseView;

public class ApiActivity extends AppCompatActivity {

    int feedCode = 1;
    TextView readout = null;
    ArrayList bikes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        readout = (TextView) findViewById(R.id.responseView);
        bikes = new ArrayList<>();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //RetrieveFeedTask taskThingumy = new RetrieveFeedTask();
        Button doTheThing = (Button) findViewById(R.id.queryButton);

        doTheThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent retrieveFeedIntent = new Intent(ApiActivity.this, RetrieveFeedTask.class);

                //startActivity(retrieveFeedIntent);

                RetrieveFeedTask goldenRetriever = new RetrieveFeedTask();

                //goldenRetriever.onPreExecute();
                goldenRetriever.execute();//from helper
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
                //String json = goldenRetriever.doInBackground();
                //setReadout(json);
                //goldenRetriever.onPostExecute(json);
            }
        });

    }

    private void setReadout(String filling){
        if (readout != null){
            readout.setText(filling);
        }
    }



    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        //private Exception exception;
        //String json = null;

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

                    //parsing the response
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    response = stringBuilder.toString();

                    //if (response != null)

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
            if(response == null) {
                response = "Oops, that didn't go to plan";
            }
            findViewById(R.id.progressBar).setVisibility(View.GONE);

            Log.i("INFO", response);

            //Setting the TextView to the response string
            setReadout(response);
        }
    }


}
