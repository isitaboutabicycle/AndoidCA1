package appdevmodule.peelo.cathal.androidca1;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddJourneyActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private EditText editTextStartLat, editTextStartLong, editTextEndLat, editTextEndLong, editTextPics;
    private TextView textDate;
    private Button addButton;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        dateFormatter = new SimpleDateFormat("dd-mm-yyyy", Locale.UK);

        editTextStartLat = (EditText) findViewById(R.id.editTextStartLat);
        editTextStartLong = (EditText) findViewById(R.id.editTextStartLong);
        editTextEndLat = (EditText) findViewById(R.id.editTextEndLat);
        editTextEndLong = (EditText) findViewById(R.id.editTextEndLong);
        textDate = (TextView) findViewById(R.id.textDate);
        //setDate = (Button) findViewById(R.id.setDate);
        editTextPics = (EditText) findViewById(R.id.editTextPics);
        addButton = (Button) findViewById(R.id.addButton);

        textDate.setOnClickListener(this);

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                createJourney();
            }
        });
    }


    //TODO: change to actually take in images!
    private void createJourney()
    {
        int startLat = Integer.parseInt(editTextStartLat.getText().toString().trim());
        int startLong = Integer.parseInt(editTextStartLong.getText().toString().trim());
        int endLat = Integer.parseInt(editTextEndLat.getText().toString().trim());
        int endLong = Integer.parseInt(editTextEndLong.getText().toString().trim());
        Date date = new Date();
        ArrayList<Image> pics = new ArrayList<Image>();

        Journey journey = new Journey(startLat, startLong, endLat, endLong, date, pics);

        FirebaseUser user = firebaseAuth.getCurrentUser();

        databaseReference.child(user.getUid()).setValue(journey);

        Toast.makeText(this, "Information saved...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view){
        createJourney();
    }
}
