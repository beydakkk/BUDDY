package com.example.findmate;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity implements LocationListener {

    ImageView photo;
    EditText mail,verification,duration,phone;
    TextView dep,getAddr,distance,currentAdr;
    int flag = 0;
    DatabaseReference ref, locationsRef;
    FirebaseAuth auth;
    FirebaseDatabase database;
    double latitude, longtitude;
    FirebaseUser user;
    Button save;
    RadioGroup radio_grade, radio_situation;
    String situation, grade,department, pinIconUri;
    Spinner department_spinner;
    ArrayAdapter<CharSequence> adapterDepartment;
    LocationManager locationManager;
    TextView adres,maxDist,durt;

    Location loc;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        photo = findViewById(R.id.foto);
        mail = findViewById(R.id.et_mail);
        verification = findViewById(R.id.et_verification);
        phone = findViewById(R.id.tv_phone);
        dep = findViewById(R.id.tv_department);
        department_spinner =findViewById(R.id.department_spinner);

        getAddr = findViewById(R.id.tv_getaddress);
        currentAdr = findViewById(R.id.tv_curraddr);
        adres = findViewById(R.id.textViewCurrentAddress);
        getAddr.setTextColor(Color.parseColor("#c7c6c5"));
        adres.setTextColor(Color.parseColor("#c7c6c5"));
        currentAdr.setBackgroundResource(R.drawable.disabled_background);
        currentAdr.setEnabled(false);

        distance = findViewById(R.id.tv_distance);
        maxDist = findViewById(R.id.maxdist);
        maxDist.setTextColor(Color.parseColor("#c7c6c5"));
        distance.setBackgroundResource(R.drawable.disabled_background);
        distance.setEnabled(false);

        duration = findViewById(R.id.tv_duration);
        durt = findViewById(R.id.textView22);
        durt.setTextColor(Color.parseColor("#c7c6c5"));
        duration.setBackgroundResource(R.drawable.disabled_background);
        duration.setEnabled(false);

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        save = findViewById(R.id.bt_saveChanges);
        radio_grade = findViewById(R.id.radioGroup_grade);
        radio_situation = findViewById(R.id.radioGroup_situation);

        adapterDepartment = ArrayAdapter.createFromResource(this,R.array.Departments, android.R.layout.simple_spinner_item);
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department_spinner.setAdapter(adapterDepartment);
        department_spinner.setSelection(0);

        department_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        database = FirebaseDatabase.getInstance();
        locationsRef = database.getReference("locations");



        radio_situation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.situation_1:
                        situation = "Looking for a flat/room to stay";
                        getAddr.setTextColor(Color.parseColor("#c7c6c5"));
                        adres.setTextColor(Color.parseColor("#c7c6c5"));
                        currentAdr.setBackgroundResource(R.drawable.disabled_background);
                        currentAdr.setEnabled(false);
                        /////////////////////////////////////////////////////////////////
                        maxDist.setTextColor(Color.parseColor("#353535"));
                        distance.setBackgroundResource(R.drawable.edittext_border);
                        distance.setEnabled(true);
                        ///////////////////////////////////////////////////////////////
                        durt.setTextColor(Color.parseColor("#353535"));
                        duration.setBackgroundResource(R.drawable.edittext_border);
                        duration.setEnabled(true);
                        break;
                    case R.id.situation_2:
                        situation = "Looking for a flat/room mate";
                        maxDist.setTextColor(Color.parseColor("#c7c6c5"));
                        distance.setBackgroundResource(R.drawable.disabled_background);
                        distance.setEnabled(false);
                        //////////////////////////////////////////////////////////////
                        getAddr.setTextColor(Color.parseColor("#353535"));
                        adres.setTextColor(Color.parseColor("#353535"));
                        currentAdr.setBackgroundResource(R.drawable.edittext_border);
                        currentAdr.setEnabled(true);
                        ////////////////////////////////////////////////////////////
                        durt.setTextColor(Color.parseColor("#353535"));
                        duration.setBackgroundResource(R.drawable.edittext_border);
                        duration.setEnabled(true);
                        ////////////////////////////////////////////////////////////

                        break;
                    case R.id.situation_3:
                        situation = "Not looking for a flat/mate";
                        getAddr.setTextColor(Color.parseColor("#c7c6c5"));
                        adres.setTextColor(Color.parseColor("#c7c6c5"));
                        currentAdr.setBackgroundResource(R.drawable.disabled_background);
                        currentAdr.setEnabled(false);
                        ////////////////////////////////////////////////////////////////
                        maxDist.setTextColor(Color.parseColor("#c7c6c5"));
                        distance.setBackgroundResource(R.drawable.disabled_background);
                        distance.setEnabled(false);
                        ///////////////////////////////////////////////////////////////
                        durt.setTextColor(Color.parseColor("#c7c6c5"));
                        duration.setBackgroundResource(R.drawable.disabled_background);
                        duration.setEnabled(false);
                        break;
                }
            }
        });

        radio_grade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radio_prep:
                        grade = "Prep class";
                        break;
                    case R.id.radio_1:
                        grade = "1st grade";
                        break;
                    case R.id.radio_2:
                        grade = "2nd grade";
                        break;
                    case R.id.radio_3:
                        grade = "3rd grade";
                        break;
                    case R.id.radio_4:
                        grade = "4th grade";
                        break;
                }
            }
        });


        ref.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User profileUser = snapshot.getValue(User.class);
                if(profileUser.getPhotoUri() != null){
                    if(!isFinishing())
                        Glide.with(Settings.this).load(profileUser.getPhotoUri()).into(photo);
                }

                mail.setText(profileUser.getMail());
                verification.setText(profileUser.getVerification());
                dep.setText(profileUser.getDepartment());
                if(department != null){
                    int spinnerPosition = adapterDepartment.getPosition(department);
                    department_spinner.setSelection(spinnerPosition);
                }
                phone.setText(profileUser.getPhone());
                distance.setText(profileUser.getMaxDistance());
                duration.setText(profileUser.getDuration());

                if(profileUser.getSituation() != null){
                    if(profileUser.getSituation().equals("Looking for a flat/room to stay")){
                        RadioButton rb1 =findViewById(R.id.situation_1);
                        rb1.setChecked(true);
                    }
                    else if(profileUser.getSituation().equals("Looking for a flat/room mate")){
                        RadioButton rb1 =findViewById(R.id.situation_2);
                        rb1.setChecked(true);
                    }
                    else if(profileUser.getSituation().equals("Not looking for a flat/mate")){
                        RadioButton rb1 =findViewById(R.id.situation_3);
                        rb1.setChecked(true);
                    }
                }

                if(profileUser.getGrade() != null){
                    if(profileUser.getGrade().equals("Prep class")){
                        RadioButton rb1 =findViewById(R.id.radio_prep);
                        rb1.setChecked(true);
                    }
                    if(profileUser.getGrade().equals("1st grade")){
                        RadioButton rb1 =findViewById(R.id.radio_1);
                        rb1.setChecked(true);
                    }
                    if(profileUser.getGrade().equals("2nd grade")){
                        RadioButton rb1 =findViewById(R.id.radio_2);
                        rb1.setChecked(true);
                    }
                    if(profileUser.getGrade().equals("3rd grade")){
                        RadioButton rb1 =findViewById(R.id.radio_3);
                        rb1.setChecked(true);
                    }
                    if(profileUser.getGrade().equals("4th grade")){
                        RadioButton rb1 =findViewById(R.id.radio_4);
                        rb1.setChecked(true);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    pinIconUri = user.getPhotoUri();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailStr = mail.getText().toString().trim();
                String verificationStr = verification.getText().toString().trim();
                String phoneStr = phone.getText().toString().trim();
                String distanceStr = distance.getText().toString().trim();
                String durationStr = duration.getText().toString().trim();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("locations");

                RadioButton rb1 =findViewById(R.id.situation_1);
                RadioButton rb2 =findViewById(R.id.situation_2);
                RadioButton rb3 =findViewById(R.id.situation_3);


                if(rb1.isChecked() | rb3.isChecked()){
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot childSnapshot : snapshot.getChildren()){
                                LocationObj loc = childSnapshot.getValue(LocationObj.class);
                                String tmp = loc.getUserID();
                                if (tmp != null && tmp.equals(user.getUid())){

                                    reference.child(childSnapshot.getKey()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Child deleted successfully
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // An error occurred while deleting the child
                                                }
                                            });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                else if(rb2.isChecked()){

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                LocationObj loc = childSnapshot.getValue(LocationObj.class);
                                String tmp = loc.getUserID();
                                if (tmp != null && tmp.equals(user.getUid()))
                                    flag = 1;
                            }
                            if(flag==0){
                                getLocation();
                                DatabaseReference newLocationRef = locationsRef.push();
                                newLocationRef.child("userID").setValue(user.getUid());
                                newLocationRef.child("latitude").setValue(latitude);
                                newLocationRef.child("longitude").setValue(longtitude);
                                newLocationRef.child("iconURI").setValue(pinIconUri);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                HashMap data = new HashMap();
                data.put("mail",mailStr);
                data.put("verification",verificationStr);
                if(!department.equals("Select department"))
                    data.put("department",department);
                data.put("phone",phoneStr);
                data.put("maxDistance",distanceStr);
                data.put("duration",durationStr);
                data.put("grade",grade);
                data.put("situation",situation);
                ref.child("users").child(user.getUid()).updateChildren(data).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                            Toast.makeText(Settings.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Settings.this, "Update Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(Settings.this,MainScreen.class);
                startActivity(intent);
            }
        });

        if(ContextCompat.checkSelfPermission(Settings.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Settings.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        getAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,Settings.this);
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,Settings.this);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longtitude = lastKnownLocation.getLongitude();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        latitude = location.getLatitude();
        longtitude = location.getLongitude();
        Toast.makeText(this, ""+latitude+" "+longtitude, Toast.LENGTH_SHORT).show();

        try{
            Geocoder geocoder = new Geocoder(Settings.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude,longtitude,1);
            String adr = addresses.get(0).getAddressLine(0);
            currentAdr.setText(adr);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
