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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static appdevmodule.peelo.cathal.androidca1.R.id.progressBar;
import static appdevmodule.peelo.cathal.androidca1.R.id.queryButton;
import static appdevmodule.peelo.cathal.androidca1.R.id.responseView;

public class ApiActivity extends AppCompatActivity {

    int feedCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //RetrieveFeedTask taskThingumy = new RetrieveFeedTask();
        Button doTheThing = (Button) findViewById(queryButton);

        doTheThing.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent retrieveFeedIntent = new Intent(ApiActivity.this, RetrieveFeedTask.class);

                //startActivity(retrieveFeedIntent);

                RetrieveFeedTask goldenRetriever = new RetrieveFeedTask();

                goldenRetriever.onPreExecute();
                String readout = goldenRetriever.doInBackground();
                TextView readoutHolder = (TextView) findViewById(R.id.responseView);
                readoutHolder.setText(readout);
                goldenRetriever.onPostExecute(readout);
            }
        });

    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        TextView readout = (TextView) findViewById(responseView);

        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            this.readout.setText("");
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL("https://api.jcdecaux.com/vls/v1/stations?contract=Dublin" + "&apiKey=" + "fcd108bd549949a9d7ee003a683cdece0fea1ff2");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return e.getMessage();//changed from null
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            Log.i("INFO", response);
            readout.setText(response);
        }
    }


}
