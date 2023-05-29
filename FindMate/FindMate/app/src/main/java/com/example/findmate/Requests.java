package com.example.findmate;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Requests extends AppCompatActivity implements RecyclerInterface{

    RecyclerView recycler;
    ArrayList<String> senderIds;
    RequestRecyclerAdapter rcAdapter;
    DatabaseReference ref , userref;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    HashMap<String, User> requests;
    ArrayList<RequestRecyclerModel> arrayList;
    TextView pending,accepted;
    RecyclerInterface inter ;
    String cocukUdi;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference();

        pending = findViewById(R.id.pendingReqs);
        accepted = findViewById(R.id.acceptedReqs);

        recycler = findViewById(R.id.reqRecycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        requests = new HashMap<>();
        arrayList = new ArrayList<>();
        senderIds = new ArrayList<>();
        rcAdapter =new RequestRecyclerAdapter(arrayList,this,this);
        recycler.setAdapter(rcAdapter);

        pendingStage();
        pending.setTypeface(null, Typeface.BOLD);

        accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accepted.setTypeface(null, Typeface.BOLD);
                pending.setTypeface(null, Typeface.NORMAL);
                arrayList.clear();
                rcAdapter.notifyDataSetChanged();
                acceptedStage();
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pending.setTypeface(null, Typeface.BOLD);
                accepted.setTypeface(null, Typeface.NORMAL);
                arrayList.clear();
                rcAdapter.notifyDataSetChanged();
                pendingStage();
            }
        });



    }
    public void acceptedStage(){

        ref.child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String receiverId = dataSnapshot.child("receiverId").getValue(String.class);
                    String senderId = dataSnapshot.child("senderId").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    String currentUserId = firebaseUser.getUid();

                    if (receiverId != null && receiverId.equals(currentUserId) && status.equals("accepted")) {
                        senderIds.add(senderId);
                    }
                }
                for( String x : senderIds){
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(x);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            cocukUdi = snapshot.getKey();
                            createModel(cocukUdi);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void pendingStage(){


        ref.child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                senderIds.clear();
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String receiverId = dataSnapshot.child("receiverId").getValue(String.class);
                    String senderId = dataSnapshot.child("senderId").getValue(String.class);
                    String status = dataSnapshot.child("status").getValue(String.class);

                    String currentUserId = firebaseUser.getUid();

                    if (receiverId != null && receiverId.equals(currentUserId) && status.equals("pending")) {
                        senderIds.add(senderId);
                    }
                }
                for( String x : senderIds){
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(x);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            cocukUdi = snapshot.getKey();
                            createModel(cocukUdi);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void createModel(String cocukUdi){

        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot s : snapshot.getChildren()){
                    User user = s.getValue(User.class);
                    if(s.getKey().equals(cocukUdi))
                        requests.put(s.getKey().toString(),user);
                }
                requests.forEach((k,v) -> insertRCModel((String) k, (User) v,arrayList));
                rcAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void insertRCModel (String key, User u, ArrayList<RequestRecyclerModel> arrayList ){
        RequestRecyclerModel rcModel = new RequestRecyclerModel(u.getName(), u.getPhotoUri(), u.getPhone(), u.getVerification());
        rcModel.setUid(key);
        arrayList.add(rcModel);
    }


    @Override
    public void onItemClick(int position) {
    }

}
