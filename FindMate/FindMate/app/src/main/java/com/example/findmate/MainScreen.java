package com.example.findmate;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    LinearLayout settings, requests, community;
    private GoogleMap myMap;
    private final int FINE_PERMISSION_CODE = 1;
    Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    BitmapDescriptor myIcon;
    ImageView photo;
    TextView name, tv_seekBar;
    SeekBar seekBar;
    ArrayList<MarkerOptions> markerArrayList = new ArrayList<>();



    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        photo = findViewById(R.id.profileImage);
        settings = findViewById(R.id.settings);
        requests = findViewById(R.id.requests);
        community = findViewById(R.id.community);
        name = findViewById(R.id.tv_isim);
        seekBar = findViewById(R.id.seekBar);
        tv_seekBar = findViewById(R.id.tv_seekBar);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationsRef = database.getReference("locations");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_seekBar.setText(String.valueOf(progress)); // TextView'i güncelle
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // İşlem yapılmasını gerektiren durumlarda kullanılabilir
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // İşlem yapılmasını gerektiren durumlarda kullanılabilir
            }
        });

        locationsRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (myMap != null){
                    myMap.clear(); // Önceki markerları temizleyin
                    markerArrayList.clear();
                }


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    Double latitudeValue = snapshot.child("latitude").getValue(Double.class);
                    Double longitudeValue = snapshot.child("longitude").getValue(Double.class);
                    if (latitudeValue != null && longitudeValue != null){
                        double latitude = latitudeValue.doubleValue();
                        double longitude = longitudeValue.doubleValue();
                        LatLng userLocation = new LatLng(latitude, longitude);
                        String uriIcon = snapshot.child("iconURI").getValue(String.class);

                        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(MainScreen.this, R.color.black));
                        Glide.with(MainScreen.this).asBitmap().load(uriIcon).apply(RequestOptions.circleCropTransform()).listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }
                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(resource, 82, 82, false);

                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(userLocation)
                                                .title("I'm looking for a mate!")
                                                .icon(BitmapDescriptorFactory.fromBitmap(getCircleBitmap(scaledBitmap)));

                                        myMap.addMarker(markerOptions).setTag(snapshot.child("userID").getValue(String.class));

                                        return true;
                                    }
                                })
                                .placeholder(cd)
                                .centerCrop()
                                .preload();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunda yapılacaklar
            }
        });


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String photoUri = dataSnapshot.child("photoUri").getValue(String.class);
                    if (photoUri != null && !photoUri.isEmpty())
                        Glide.with(MainScreen.this).load(photoUri).into(photo);
                    name.setText(dataSnapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, Requests.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, Settings.class);
                startActivity(intent);
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, Community.class);
                startActivity(intent);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Yeni konum güncellemelerini burada kullanabilirsiniz
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                }
            }
        };
        getLastLocation();
    }

    public void addMarkers(ArrayList<MarkerOptions> markerArray) {
        // Firebase veritabanından konum bilgilerini al ve markerları oluştur ve ekle
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationsRef = database.getReference("locations");
        locationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Önceki markerları temizle

                markerArray.clear();
                // Veritabanından konumları al ve markerları oluşturarak haritaya ekle
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    LocationObj location = childSnapshot.getValue(LocationObj.class);
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String iconURI = location.getIconURI();

                    markerArray.add(new MarkerOptions().position(new LatLng(latitude, longitude)));
                }

                if(myMap != null){
                    myMap.clear();
                    for(MarkerOptions m : markerArray){
                        // Oluşturulan markerı haritaya ekle
                        myMap.addMarker(m);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MainScreen.this);
                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        myMap.getUiSettings().setZoomControlsEnabled(true); // + -
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(true);
        myMap.getUiSettings().setMapToolbarEnabled(true);
        myMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        myMap.getUiSettings().setAllGesturesEnabled(true);
        float zoomLevel = 5.2f; // Specify the desired zoom level
        float zoomLevel2 = 12.0f;
        LatLng curr = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(curr).title("You are here!"));
        LatLng tr = new LatLng(39.348123, 34.507826);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tr, zoomLevel));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, zoomLevel2));
            }
        }, 2000);

        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag() != null){
                    String userId = marker.getTag().toString();
                    Intent intent = new Intent(MainScreen.this,SelectedProfile.class);
                    intent.putExtra("transfer", userId);
                    System.out.println("*************************"+userId);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation();
            else
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);

        Canvas canvas = new Canvas(circleBitmap);
        float radius = bitmap.getWidth() / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        return circleBitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // 5 saniyede bir güncelleme yapar

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {

        Intent intent = new Intent(MainScreen.this,Settings.class);
        startActivity(intent);
        return false;
    }


}
