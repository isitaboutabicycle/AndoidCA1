package appdevmodule.peelo.cathal.androidca1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddJourneyActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String currentFilePath;

    private EditText editTextStartLat, editTextStartLong, editTextEndLat, editTextEndLong;
    private TextView textDate, picsView;
    private Button addPics, addButton;

    private Calendar newCalendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private File currentPic;
    private ArrayList<Bitmap> pics;
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

        //why the italics?
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);

        editTextStartLat = (EditText) findViewById(R.id.editTextStartLat);
        editTextStartLong = (EditText) findViewById(R.id.editTextStartLong);
        editTextEndLat = (EditText) findViewById(R.id.editTextEndLat);
        editTextEndLong = (EditText) findViewById(R.id.editTextEndLong);
        textDate = (TextView) findViewById(R.id.textDate);
        picsView = (TextView) findViewById(R.id.picsView);
        addPics = (Button) findViewById(R.id.addPicsButton);
        addButton = (Button) findViewById(R.id.addButton);

        //TODO: remove these hard-coded values
        editTextStartLat.setText("2");
        editTextStartLong.setText("2");
        editTextEndLat.setText("2");
        editTextEndLong.setText("2");

        textDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                datePickerDialog.show();
            }
        });

        //getting current date...?
        newCalendar = Calendar.getInstance();

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
        datePickerDialog = new DatePickerDialog(this, myODSListener, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

        pics = new ArrayList<Bitmap>();

        //adding pic
        addPics.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dispatchTakePictureIntent();
            }
        });

        //saving journey
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
                databaseReference.child(user.getUid()) //client id
                        .child("journeys")             //purpose
                        .push()                        //unique journey id
                        .setValue(myJourney);

                Toast.makeText(this, "Journey saved!", Toast.LENGTH_SHORT).show();

                //resetting variables
                currentPic = null;
                date = null;
                editTextStartLat.setText("");
                editTextEndLat.setText("");
                editTextStartLong.setText("");
                editTextEndLong.setText("");
                pics = null;
                picsView.setText("");
                newCalendar = Calendar.getInstance();
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

    public void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //checking that there is an app to take pics
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile = null;

            try{
                photoFile = createImageFile();
            }
            catch (IOException e)
            {
                Toast.makeText(this, "Could not create the file", Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null){
                Uri photoUri = null;

                try{
                    photoUri = FileProvider.getUriForFile(this,
                            "appdevmodule.peelo.cathal.androidca1",
                            photoFile);
                }
                catch(IllegalArgumentException e){
                    Toast.makeText(this, "Could not get FileProvider", Toast.LENGTH_SHORT).show();
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri.toString());
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                //adding to array and updating view
                pics.add((Bitmap) data.getExtras().get("data"));//Has to be a Bitmap apparently, not a File
                picsView.setText("Pics added: " + pics.size());
            }
        }
    }


    private File createImageFile() throws IOException {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //get storage directory and name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        //setting variable
        currentFilePath = image.getAbsolutePath();

        return image;
    }
}
