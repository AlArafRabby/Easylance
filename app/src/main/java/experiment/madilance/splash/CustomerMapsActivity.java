package experiment.madilance.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;

import android.net.Uri;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest = new LocationRequest();
    SupportMapFragment mapFragment;
    Button Call;
    private boolean requestType=false;
    private LatLng CustomerPickUpLocation;
    private ValueEventListener DriverLocationRefListner;
    private DatabaseReference driverRef;
    private Boolean driverFound=false;
    private Marker DriverMarker, PickUpMarker;

    private TextView txtName, txtPhone, txtCarName;
    private CircleImageView profilePic;
    private RelativeLayout relativeLayout;
    private int radius=1;
    private String driverFoundID;
    GeoQuery geoQuery;
    private DatabaseReference CustomerDatabaseRef;
    private ImageView call_driver;



    FirebaseAuth mAuth;
    private DatabaseReference DriverAvailableRef, DriverLocationRef;
    //String customerID;
    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();



    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else
        {
            mapFragment.getMapAsync(this);
        }



        mAuth = FirebaseAuth.getInstance();
        customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DriverAvailableRef = FirebaseDatabase.getInstance().getReference().child("DriversAvailable");
        DriverLocationRef = FirebaseDatabase.getInstance().getReference().child("DriversWorking");
        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("CustomerRequest");

        txtName = findViewById(R.id.name_driver);
        txtPhone = findViewById(R.id.phone_driver);
        txtCarName = findViewById(R.id.car_name_driver);
        profilePic = findViewById(R.id.profile_image_driver);
        relativeLayout = findViewById(R.id.rel1);
        call_driver=findViewById(R.id.call_driver);
        Call=findViewById(R.id.request);
        //nevigation drawer
        dl = (DrawerLayout)findViewById(R.id.activity_map_customer);
        t = new ActionBarDrawerToggle(this, dl,R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        Intent intent=new Intent(getApplicationContext(),profile.class);
                        intent.putExtra("type", "Customer");
                        startActivity(intent);
                        break;
                    case R.id.share_app:
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBodyText = "Check it out. Your message goes here";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject here");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                        startActivity(Intent.createChooser(sharingIntent, "Shearing Option"));
                        break;
                    case R.id.about:
                        Intent aintent=new Intent(getApplicationContext(),about_developer.class);
                        startActivity(aintent);
                        break;
                    case R.id.feedback:
                        Intent Email = new Intent(Intent.ACTION_SEND);
                        Email.setType("text/email");
                        Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "alrabbyjoyprince5@gmail.com" });
                        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        Email.putExtra(Intent.EXTRA_TEXT, "Dear developer," + "");
                        startActivity(Intent.createChooser(Email, "Send Feedback:"));
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent mintent = new Intent(getApplicationContext(), MainActivity.class);
                        mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mintent);
                        finish();
                        break;
                    default:
                        return true;
                }


                return true;

            }
        });


        Call.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (requestType) {

                requestType = false;
                geoQuery.removeAllListeners();
                DriverLocationRef.removeEventListener(DriverLocationRefListner);

                if (driverFound != null) {
                    driverRef = FirebaseDatabase.getInstance().getReference()
                            .child("User").child("Driver").child(driverFoundID).child("CustomerRideID");

                    driverRef.removeValue();

                    driverFoundID = null;
                }
                driverFound = false;
                radius = 1;



                GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
                geoFire.removeLocation(customerID);

                if (PickUpMarker != null) {
                    PickUpMarker.remove();
                }
                if (DriverMarker != null) {
                    DriverMarker.remove();
                }

                Call.setText("Call EasyLance");

                relativeLayout.setVisibility(View.GONE);


            } else {

                requestType = true;


                GeoFire geoFire = new GeoFire(CustomerDatabaseRef);
                geoFire.setLocation(customerID, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                CustomerPickUpLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                PickUpMarker = mMap.addMarker(new MarkerOptions().position(CustomerPickUpLocation).title("my location").icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));


                Call.setText("Getting your Driver....");
                getClosestDriver();


            }
         }

        });
    }



    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //DriverRegistration.this.finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(t.onOptionsItemSelected(item))
        {
            return true;
        }
        else
        {
            // Change the map type based on the user's selection.
            switch (item.getItemId()) {
                case R.id.normal_map:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    return true;

                case R.id.satellite_map:
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    return true;

                default:
                    return super.onOptionsItemSelected(item);
            }

        }

    }




    private void getClosestDriver(){
        GeoFire geoFire = new GeoFire(DriverAvailableRef);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(CustomerPickUpLocation.latitude, CustomerPickUpLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestType) {
                    driverFound = true;
                    driverFoundID = key;

                    //we tell driver which customer he is going to have

                    driverRef = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(driverFoundID);
                    //String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("CustomerRideID", customerID);
                    driverRef.updateChildren(map);

                    //Show driver location on customerMapActivity
                    getDriverLocation();
                    Call.setText("Looking for Driver Location....");
                    relativeLayout.setVisibility(View.VISIBLE);
                    getAssignedDriverInformation();


                }
            }


            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound)
                {
                    radius++;
                    getClosestDriver();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }



    private void getDriverLocation() {

        DriverLocationRefListner = DriverLocationRef.child(driverFoundID).child("l")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists() && requestType)
                        {


                            List<Object> driverLocationMap = (List<Object>) dataSnapshot.getValue();
                            double LocationLat = 0;
                            double LocationLng = 0;
                            Call.setText("Driver Found");
                            if(driverLocationMap.get(0) != null)
                            {
                                LocationLat = Double.parseDouble(driverLocationMap.get(0).toString());
                            }
                            if(driverLocationMap.get(1) != null)
                            {
                                LocationLng = Double.parseDouble(driverLocationMap.get(1).toString());
                            }

                            //adding marker - to pointing where driver is - using this lat lng
                            LatLng DriverLatLng = new LatLng(LocationLat, LocationLng);
                            if(DriverMarker != null)
                            {
                                DriverMarker.remove();
                            }


                            Location location1 = new Location("");
                            location1.setLatitude(CustomerPickUpLocation.latitude);
                            location1.setLongitude(CustomerPickUpLocation.longitude);

                            Location location2 = new Location("");
                            location2.setLatitude(DriverLatLng.latitude);
                            location2.setLongitude(DriverLatLng.longitude);

                            float Distance = location1.distanceTo(location2);

                            if (Distance < 90)
                            {
                                Call.setText("Driver's Reached");
                            }
                            else
                            {
                                Call.setText("Driver Found: " + String.valueOf(Distance));
                            }

                            DriverMarker=mMap.addMarker(new MarkerOptions().position(DriverLatLng).title("your Driver").icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));


                }


                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop()
    {
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    final int LOCATION_REQUEST_CODE = 1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mapFragment.getMapAsync(this);
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private void getAssignedDriverInformation()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child("1");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()  &&  dataSnapshot.getChildrenCount() > 0)
                {

                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("dname")!=null){
                        txtName.setText(map.get("dname").toString());
                    }
                    if(map.get("dmobile")!=null){
                        txtPhone.setText(map.get("dmobile").toString());
                    }
                    if(map.get("dlicence")!=null){
                        txtCarName.setText(map.get("dlicence").toString());
                    }
                    if (dataSnapshot.hasChild("image"))
                    {
                        String image = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                        Picasso.get().load(image).into(profilePic);
                    }

                    call_driver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse(txtPhone.getText().toString()));
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(callIntent);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
