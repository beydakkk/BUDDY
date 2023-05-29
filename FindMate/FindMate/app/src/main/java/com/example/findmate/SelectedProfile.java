package com.example.findmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.HashMap;

public class SelectedProfile extends AppCompatActivity {
    TextView name, mail, phone, department, distance, duration, grade, situation, contact;
    DatabaseReference ref;
    ImageView foti;
    FirebaseAuth auth;
    FirebaseUser user;
    Button match;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    DatabaseReference matchRequestsRef;
    String uid ,receiverToken;
    public boolean requestAlreadySent;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_profile);
        uid = getIntent().getStringExtra("transfer");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference();
        matchRequestsRef = FirebaseDatabase.getInstance().getReference().child("requests");

        Location campus_loc = new Location("");
        Location curr_loc = new Location("");
        campus_loc.setLatitude(41.020816979527496);
        campus_loc.setLongitude(28.898765226044915);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        name = findViewById(R.id.tv_name);
        mail = findViewById(R.id.tv_mail);
        phone = findViewById(R.id.tv_phone);
        department = findViewById(R.id.tv_department);
        distance = findViewById(R.id.tv_distance);
        duration = findViewById(R.id.tv_duration);
        foti = findViewById(R.id.foti);
        grade = findViewById(R.id.tv_grade);
        situation = findViewById((R.id.tv_situation));
        contact = findViewById(R.id.tv_whatsapp);
        match = findViewById(R.id.matchButton);

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();





        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User profileUser = snapshot.getValue(User.class);
                if(profileUser != null){
                    if(!isFinishing())
                        Glide.with(SelectedProfile.this).load(profileUser.getPhotoUri()).into(foti);

                    name.setText(profileUser.getName());
                    mail.setText(profileUser.getMail());
                    department.setText(profileUser.getDepartment());
                    phone.setText(profileUser.getPhone());
                    duration.setText(profileUser.getDuration());
                    grade.setText(profileUser.getGrade());
                    situation.setText(profileUser.getSituation());
                }
                mDatabase2.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if(snapshot2.child("situation").getValue(String.class).equals("Not looking for a flat/mate")
                        | snapshot2.child("situation").getValue(String.class).equals(snapshot.child("situation").getValue(String.class))){
                            match.setEnabled(false);
                            match.setVisibility(View.INVISIBLE);
                        }else{
                            match.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = phone.getText().toString().trim();
                if(isValidPhoneNumber(num)){
                    num = num.replace("+", "").replace(" ", "");
                    try {
                        Intent send = new Intent("android.intent.action.MAIN");
                        send.putExtra("jid", num + "@s.whatsapp.net");
                        send.putExtra(Intent.EXTRA_TEXT, "Selamınaleyküm...");
                        send.setAction(Intent.ACTION_SEND);
                        send.setPackage("com.whatsapp");
                        send.setType("text/plain");
                        startActivity(send);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // WhatsApp is not installed, handle the exception here
                        contact.setError("WhatsApp is not installed!");
                        Toast.makeText(getApplicationContext(), "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    } catch (Exception e) {
                        // Handle any other exceptions here
                        e.printStackTrace();
                    }
                }else{
                    contact.setError("Phone number is invalid!");
                }
            }
        });

        match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sender = user.getUid();
                String receiver = uid;
                sendMatchRequest(sender, receiver, new TokenCallback() {
                    @Override
                    public void onTokenReceived(String token) {
                        String title = "You have a match request!";
                        String message = "Accept or Reject.";
                        if(!title.equals("") && !message.equals("")){
                            FCMSend.pushNotification(
                                    SelectedProfile.this,
                                    "cDTN7OymTXCzxYdIVY4jTk:APA91bFWk1i6Y1EPg99ojURGD0xP-Xc7E9NjRWM3nlq1FEtvVZ7yk2K4FNl4M5XSQr4hL6S53FMwEhD5PxMkOTObCWIj9dg8h7GW9fwurI-oFKyYsiwbBw1tfUA1p4RKfRAblBdFgzL2",
                                    title,
                                    message);
                        }
                    }
                });

            }
        });



    }

    private void sendMatchRequest(String senderId, String receiverId, TokenCallback callback) {
        // İstek verilerini oluşturun
        HashMap<String, Object> requestData = new HashMap<>();
        requestData.put("senderId", senderId);
        requestData.put("receiverId", receiverId);
        requestData.put("status", "pending"); // İstek durumu: bekliyor

        if(isAlreadySent(senderId,receiverId)){
            matchRequestsRef.push().setValue(requestData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Match request sent!", Toast.LENGTH_SHORT).show();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(receiverId);
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null) {
                                        receiverToken = user.getToken();
                                        callback.onTokenReceived(receiverToken); // Pass the token to the callback
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle onCancelled if needed
                                }
                            });

                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle onFailure if needed
                        }
                    });
        }
        else{
            Toast.makeText(this, "Already sent!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isAlreadySent(String sender , String receiver){

        DatabaseReference matchRequestsRef2 = FirebaseDatabase.getInstance().getReference().child("requests");
        matchRequestsRef2.orderByChild("senderId").equalTo(sender)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestAlreadySent = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MatchRequest request = snapshot.getValue(MatchRequest.class);

                            if (request != null) {
                                if (request.getReceiverID().equals(receiver)) {
                                    // Daha önce istek gönderilmiş
                                    requestAlreadySent = true;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if(requestAlreadySent)
            return false;
        else
            return true;
    }


    public boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (Exception e) {
            // An exception occurred during parsing
            return false;
        }
    }

    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation();
            else
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }
}
