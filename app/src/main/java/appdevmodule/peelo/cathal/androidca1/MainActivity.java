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

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button mHistoryButton = (Button) findViewById(R.id.historyButton);

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change to new activity class
                Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);

                startActivity(mainActivityIntent);
            }
        });

        Button mJourneyButton = (Button) findViewById(R.id.journeyButton);

        mJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change to new activity class
                Intent mainActivityIntent = new Intent(MainActivity.this, MainActivity.class);

                startActivity(mainActivityIntent);
            }
        });

        Button mApiButton = (Button) findViewById(R.id.apiButton);

        mApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: change to new activity class
                Intent mainActivityIntent = new Intent(MainActivity.this, ApiActivity.class);

                startActivity(mainActivityIntent);
            }
        });

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
