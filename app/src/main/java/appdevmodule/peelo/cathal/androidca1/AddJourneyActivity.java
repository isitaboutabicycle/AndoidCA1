package appdevmodule.peelo.cathal.androidca1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddJourneyActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private EditText editTextStartLat, editTextStartLong, editTextEndLat, editTextEndLong;
    private TextView textDate;
    private Button addPics, addButton;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private static int REQUEST_IMAGE = 0;

    private File currentPic;
    private ArrayList<Image> pics;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        //if not logged in, redirect to Login Activity
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);

        editTextStartLat = (EditText) findViewById(R.id.editTextStartLat);
        editTextStartLong = (EditText) findViewById(R.id.editTextStartLong);
        editTextEndLat = (EditText) findViewById(R.id.editTextEndLat);
        editTextEndLong = (EditText) findViewById(R.id.editTextEndLong);
        textDate = (TextView) findViewById(R.id.textDate);
        addPics = (Button) findViewById(R.id.addPicsButton);
        addButton = (Button) findViewById(R.id.addButton);

        textDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                datePickerDialog.show();
            }
        });

        //getting current date...?
        Calendar newCalender = Calendar.getInstance();

        //listener and method to feed into datePickerDialog
        DatePickerDialog.OnDateSetListener myODSListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = Calendar.getInstance();
                //updating data member
                date.set(year, month, dayOfMonth);
                //updating dislay
                textDate.setText(dateFormatter.format(date.getTime()));
            }
        };

        //feeding it in...
        datePickerDialog = new DatePickerDialog(this, myODSListener, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH),
                newCalender.get(Calendar.DAY_OF_MONTH));

        //TODO: change to start gallery for result
        //addPics
        pics = new ArrayList<Image>();

        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                createJourney();
            }
        });
    }


    private void createJourney()
    {
        int startLat, startLong, endLat, endLong;

        try{
            startLat = Integer.parseInt(editTextStartLat.getText().toString().trim());
            startLong = Integer.parseInt(editTextStartLong.getText().toString().trim());
            endLat = Integer.parseInt(editTextEndLat.getText().toString().trim());
            endLong = Integer.parseInt(editTextEndLong.getText().toString().trim());

            Journey myJourney = new Journey(startLat, startLong, endLat, endLong, date, pics);

            FirebaseUser user = firebaseAuth.getCurrentUser();

            try{
                databaseReference.child(user.getUid()).setValue(myJourney);

                Toast.makeText(this, "Journey saved!", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e){
                Toast.makeText(this, "Error saving the journey!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Latitude or longitude invalid", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view){
        createJourney();
    }

    private void startGallery(){
        try{
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_IMAGE);
        }
        catch (Exception e){
            Toast.makeText(this, "Could not open the gallery", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            String[] filePathColumn = {MediaStore.Images.Media.DATA };

            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(data.getData(), /*filePathColumn*/null, null, null, null);

            if (null != cursor && cursor.moveToFirst()){
                String id = cursor.getString(cursor.getColumnIndex())
            }
        }
    }

}
