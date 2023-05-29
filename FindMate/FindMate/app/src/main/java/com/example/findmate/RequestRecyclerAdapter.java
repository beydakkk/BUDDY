package com.example.findmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;

public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestRecyclerAdapter.ViewHolder> {

    final RecyclerInterface rc_interface;
    ArrayList<RequestRecyclerModel> user;
    String phone,verification;
    Context context;

    public RequestRecyclerAdapter(ArrayList<RequestRecyclerModel> user, Requests activity,RecyclerInterface rc_interface){
        this.user = user;
        this.context = activity;
        this.rc_interface = rc_interface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.request_item,parent,false);
        return new ViewHolder(view,rc_interface);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRecyclerAdapter.ViewHolder holder,@SuppressLint("RecyclerView") int position) {
        RequestRecyclerModel users = user.get(position);
        holder.name.setText(users.getName());
        if(users.getPhotoUri() != null)
            Glide.with(context).load(users.getPhotoUri()).into(holder.profile);


        holder.wp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = users.getPhone();
                if(isValidPhoneNumber(num)){
                    num = num.replace("+", "").replace(" ", "");
                    try {
                        Intent send = new Intent("android.intent.action.MAIN");
                        send.putExtra("jid", num + "@s.whatsapp.net");
                        send.putExtra(Intent.EXTRA_TEXT, "Selamınaleyküm...");
                        send.setAction(Intent.ACTION_SEND);
                        send.setPackage("com.whatsapp");
                        send.setType("text/plain");
                        context.startActivity(send);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // WhatsApp is not installed, handle the exception here
                        Toast.makeText(context, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    } catch (Exception e) {
                        // Handle any other exceptions here
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("PHONE -----------------> "+users.getPhone());
                    Toast.makeText(context, "Phone number is invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));

                intent.putExtra(Intent.EXTRA_EMAIL, users.getVerification()); // alıcı yazılacak

                intent.putExtra(Intent.EXTRA_SUBJECT, "Match Request");

                intent.putExtra(Intent.EXTRA_TEXT, "Hi! I want to have an online meeting if it is okay with you. :)");

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Gmail App is not installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.accept.setVisibility(View.INVISIBLE);
                holder.decline.setVisibility(View.INVISIBLE);
                holder.iWant.setText("Here is your new BUDDY!");
                FirebaseDatabase.getInstance().getReference().child("requests").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot d : snapshot.getChildren()){
                            MatchRequest match = d.getValue(MatchRequest.class);
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            String currentUserId = firebaseAuth.getCurrentUser().getUid();
                            if(position<user.size()){
                                if(match.getSenderID().equals(user.get(position).getUid()) && match.getReceiverID().equals(currentUserId) && match.getStatus().equals("pending")){
                                    DatabaseReference requestReff = FirebaseDatabase.getInstance().getReference().child("requests").child(d.getKey());
                                    requestReff.child("status").setValue("accepted");
                                }
                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });



        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("requests").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot d : snapshot.getChildren()){
                            MatchRequest match = d.getValue(MatchRequest.class);
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            String currentUserId = firebaseAuth.getCurrentUser().getUid();
                            if(position<user.size()){
                                if(match.getSenderID().equals(user.get(position).getUid()) && match.getReceiverID().equals(currentUserId) && match.getStatus().equals("pending")){
                                    DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("requests").child(d.getKey());
                                    requestRef.child("status").setValue("declined");
                                }
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
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

    @Override
    public int getItemCount() {
        return user.size();
    }

    public void setArrayList(ArrayList<RequestRecyclerModel> arrayList) {
        this.user = arrayList;
    }

    public ArrayList<RequestRecyclerModel> getArrayList() {
        return user;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name,iWant;
        ImageButton wp, mail, accept, decline;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView,RecyclerInterface rc_interface) {
            super(itemView);
            profile = itemView.findViewById(R.id.req_profile);
            name = itemView.findViewById(R.id.req_name);
            wp = itemView.findViewById(R.id.imButWp);
            mail = itemView.findViewById(R.id.imButMail);
            cardView = (CardView) itemView.findViewById(R.id.req_card_view);
            accept = itemView.findViewById(R.id.acceptButton);
            decline = itemView.findViewById(R.id.declineButton);
            iWant = itemView.findViewById(R.id.iWant);
        }
    }
}
