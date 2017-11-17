package appdevmodule.peelo.cathal.androidca1;

import android.*;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    private Button getStart, viewStart, getEnd, viewEnd, addPics, addButton;

    private Calendar newCalendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ArrayList<Bitmap> pics;
    private Calendar date;
    private String dateString;
    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mFusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST = 1;
    private Boolean myLocationPermission;
    private Location myLocation;
    private Boolean first;

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editTextStartLat = (EditText) findViewById(R.id.editTextStartLat);
        editTextStartLong = (EditText) findViewById(R.id.editTextStartLong);
        editTextEndLat = (EditText) findViewById(R.id.editTextEndLat);
        editTextEndLong = (EditText) findViewById(R.id.editTextEndLong);
        textDate = (TextView) findViewById(R.id.textDate);
        picsView = (TextView) findViewById(R.id.picsView);
        getStart = (Button) findViewById(R.id.getStart);
        viewStart = (Button) findViewById(R.id.viewStart);
        getEnd = (Button) findViewById(R.id.getEnd);
        viewEnd = (Button) findViewById(R.id.viewEnd);
        addPics = (Button) findViewById(R.id.addPicsButton);
        addButton = (Button) findViewById(R.id.addButton);

        getStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getPoint(true);
            }
        });
        viewStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                viewPoint(true);
            }
        });
        getEnd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getPoint(false);
            }
        });
        viewEnd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                viewPoint(false);
            }
        });
        textDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                datePickerDialog.show();
            }
        });

        //listener and method to feed into datePickerDialog
        DatePickerDialog.OnDateSetListener myODSListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = Calendar.getInstance();
                //updating data member
                date.set(year, month, dayOfMonth);
                //updating display
                dateString = dateFormatter.format(date.getTime());
                textDate.setText(dateString);
            }
        };

        //getting current date...?
        newCalendar = Calendar.getInstance();

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

            Journey myJourney = new Journey(startLat, startLong, endLat, endLong, dateString, pics);

            FirebaseUser user = firebaseAuth.getCurrentUser();

            try{
                databaseReference.child(user.getUid()) //client id
                        .child("journeys")             //purpose
                        .push()                        //unique journey id
                        .setValue(myJourney);

                Toast.makeText(this, "Journey saved!", Toast.LENGTH_LONG).show();

                //resetting variables
                date = null;
                dateString = "";
                editTextStartLat.setText("");
                editTextEndLat.setText("");
                editTextStartLong.setText("");
                editTextEndLong.setText("");
                textDate.setText("");
                pics = new ArrayList<Bitmap>();
                picsView.setText("");
                newCalendar = Calendar.getInstance();
            }
            catch(Exception e){
                Toast.makeText(this, "Error saving the journey!", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Latitude or longitude invalid", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, "Could not create the file", Toast.LENGTH_LONG).show();
            }

            if(photoFile != null){
                Uri photoUri = null;

                try{
                    photoUri = FileProvider.getUriForFile(this,
                            "appdevmodule.peelo.cathal.androidca1",
                            photoFile);
                }
                catch(IllegalArgumentException e){
                    Toast.makeText(this, "Could not get FileProvider", Toast.LENGTH_LONG).show();
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

    private void getLocationPermission(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            myLocationPermission = true;
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }

    private void onRequestPermissionResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults){
        myLocationPermission = false;
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    myLocationPermission = true;
                }
            }
        }
        //updateLocationUI();
    }

    //to fetch latest coordinates from FusedLocationProviderClient and fill them into the EditTexts
    private void getPoint(boolean mFirst){
        first = mFirst;



        first = null;
    }

    //to parse entered coordinates and pass them to Maps
    private void viewPoint(boolean mFirst){
        String name, lat, lng;
        int intLat, intLng;

        if(mFirst){
            name = "Journey's Start";
            lat = editTextStartLat.getText().toString().trim();
            lng = editTextStartLong.getText().toString().trim();
        } else {
            name = "Journey's End";
            lat = editTextEndLat.getText().toString().trim();
            lng = editTextEndLong.getText().toString().trim();
        }
        intLat = Integer.parseInt(lat);
        intLng = Integer.parseInt(lng);

        //sanitising coordinates
        if(lat==null || intLat > 90 || intLat < -90){
            lat = "0";
        }
        if(lng==null || intLng > 90 || intLng < -90){
            lng = "0";
        }

        Uri myUri = Uri.parse("geo:"+lat+","+lng+"?q="+lat+","+lng+"("+name+")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, myUri);
        startActivity(mapIntent);

        first = null;
    }
}
