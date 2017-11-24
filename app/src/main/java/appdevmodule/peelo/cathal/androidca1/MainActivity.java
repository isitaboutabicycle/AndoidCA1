package appdevmodule.peelo.cathal.androidca1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (null == user) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else
        {
            String id = user.getEmail();

            TextView mainWelcome = (TextView) findViewById(R.id.mainWelcome);
            if (null != id) {
                mainWelcome.setText("Welcome " + id + "!");
            } else {
                mainWelcome.setText("Welcome!");
            }

            Button mHistoryButton = (Button) findViewById(R.id.historyButton);

            mHistoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent journeyHistoryIntent = new Intent(MainActivity.this, JourneyHistoryActivity.class);

                    startActivity(journeyHistoryIntent);
                }
            });

            Button mJourneyButton = (Button) findViewById(R.id.journeyButton);

            mJourneyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addJourneyIntent = new Intent(MainActivity.this, AddJourneyActivity.class);

                    startActivity(addJourneyIntent);
                }
            });

            Button mApiButton = (Button) findViewById(R.id.apiButton);

            mApiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent apiIntent = new Intent(MainActivity.this, ApiActivity.class);

                    startActivity(apiIntent);
                }
            });

            Button logoutButton = (Button) findViewById(R.id.logoutButton);

            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {

                    firebaseAuth.signOut();
                    finish();

                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
        });

        }

    }
}
