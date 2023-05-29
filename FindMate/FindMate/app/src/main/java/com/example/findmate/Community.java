package com.example.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

public class Community extends AppCompatActivity implements RecyclerInterface{

    RecyclerView recycler;
    HashMap users;
    ArrayList<RecyclerModel> arrayList;
    RecyclerAdapter rcAdapter;
    DatabaseReference ref;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Button apply;
    Spinner spinner;
    String filter;
    ArrayAdapter<CharSequence> filterAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference();

        spinner = findViewById(R.id.filterSpinner);
        apply = findViewById(R.id.applyButton);

        filterAdapter = ArrayAdapter.createFromResource(this,R.array.Situations, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(filterAdapter);
        spinner.setSelection(0);

        recycler = findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        arrayList=new ArrayList<>();
        rcAdapter =new RecyclerAdapter(arrayList,this,this);
        recycler.setAdapter(rcAdapter);

        users = new HashMap();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter = spinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                filter = "";
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterRecyclerView();
            }
        });

        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for(DataSnapshot s : snapshot.getChildren()){
                    User user = s.getValue(User.class);
                    users.put(s.getKey().toString(),user);

                }
                users.forEach((k,v) -> insertRCModel((String) k, (User) v,arrayList));
                rcAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

    private void filterRecyclerView() {

        ArrayList<RecyclerModel> filteredList = new ArrayList<>();
        for (RecyclerModel rcModel : arrayList) {
            if ((rcModel.getSituation() != null && rcModel.getSituation().contains(filter)) | filter.equals("-No filter-")) {
                filteredList.add(rcModel);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(Community.this, "There is no matching result...", Toast.LENGTH_SHORT).show();
        }

        rcAdapter.setArrayList(filteredList);
        rcAdapter.notifyDataSetChanged();
    }

    public void insertRCModel (String key, User u,ArrayList<RecyclerModel> arrayList ){

        RecyclerModel rcModel = new RecyclerModel(u.getName(), u.getDepartment(), u.getGrade(), u.getSituation(), u.getPhotoUri());
        rcModel.setUid(key);
        arrayList.add(rcModel);

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Community.this,SelectedProfile.class);
        RecyclerModel rcModel1 = arrayList.get(position);
        intent.putExtra("transfer",rcModel1.uid.toString());
        startActivity(intent);
    }


}
