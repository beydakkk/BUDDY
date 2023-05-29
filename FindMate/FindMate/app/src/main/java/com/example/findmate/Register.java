package com.example.findmate;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;




public class Register extends AppCompatActivity {

    Button submit;
    FloatingActionButton upload;
    EditText name, mail, pass, confPass,vermail;
    TextView verif;
    ImageView photo;
    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    Uri uri;
    String uid, token;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.et_name);
        mail = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_pass);
        confPass = findViewById(R.id.et_confPass);
        upload = findViewById(R.id.uploadButton);
        submit = findViewById(R.id.submitButton);
        photo = findViewById(R.id.profilePhoto);
        verif = findViewById(R.id.tv_verify);
        vermail = findViewById(R.id.et_veremail);

        verif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mail verification kodu gelecek
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(Register.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameStr = name.getText().toString().trim();
                String mailStr = mail.getText().toString().trim();
                String regPasswordStr = pass.getText().toString().trim();
                String confPasswordStr = confPass.getText().toString().trim();
                String vermailStr = vermail.getText().toString().trim();



                auth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                ref = database.getReference("users");

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            return;
                        }
                        token = task.getResult();
                    }
                });


                if(!TextUtils.isEmpty(nameStr)  && !TextUtils.isEmpty(mailStr) && !TextUtils.isEmpty(regPasswordStr) &&
                        !TextUtils.isEmpty(confPasswordStr) && regPasswordStr.equals(confPasswordStr)){
                    if(uri != null){
                        //uploadToFirebase(uri);
                        Log.d("test",uri.toString());
                        auth.createUserWithEmailAndPassword(mailStr,regPasswordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    user = auth.getCurrentUser();
                                    uid = user.getUid();

                                    User user = new User(nameStr, mailStr);
                                    user.setPhotoUri(uri.toString());
                                    user.setVerification(vermailStr);
                                    user.setToken(token);
                                    ref.child(uid).setValue(user);
                                    Toast.makeText(Register.this, "Registration Successfull!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this,Login.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    }else
                        Toast.makeText(Register.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(TextUtils.isEmpty(nameStr))
                        name.setError("Name can not be empty!");
                    if(TextUtils.isEmpty(mailStr))
                        mail.setError("E-mail can not be empty!");
                    if(TextUtils.isEmpty(regPasswordStr))
                        pass.setError("Password can not be empty!");
                    if(TextUtils.isEmpty(confPasswordStr))
                        confPass.setError("Confirmation password can not be empty!");
                    if(!regPasswordStr.equals(confPasswordStr))
                        confPass.setError("Confirmation failed!");
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = data.getData();
        Log.d("test",uri.toString());
        photo.setImageURI(uri);
    }

    private void uploadToFirebase(Uri uri){
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        user = auth.getCurrentUser();
                        uid = user.getUid();
                        ref.child(uid).child("photoUri").setValue(uri.toString());

                        Toast.makeText(Register.this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
