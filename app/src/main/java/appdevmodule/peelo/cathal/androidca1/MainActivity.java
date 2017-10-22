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
                mainWelcome.setText("Welcome back " + id + "!");
            } else {
                mainWelcome.setText("Welcome back!");
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
/*
    private TextView mTextMessage;
    //private Fragment mFragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_journeyHistory);
                   // mFragment = new HistoryFragment();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_newJourney);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_API);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        android.app.Fragment mFragment = new android.app.Fragment();

        //get a reference to the FragmentManager
        android.app.FragmentManager fragmentManager = getFragmentManager();

        //begin a new FragmentTransaction
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //add the Fragment
        fragmentTransaction.add(R.id.contentMain, mFragment);

        //commit the FragmentTransaction
        fragmentTransaction.commit();
    }

}*/
