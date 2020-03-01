package experiment.madilance.splash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {


    private CircleImageView profileImageView;
    Button saveButton;
    private EditText nameEditText, phoneEditText, driverCarName;
    TextView profileChangeBtn;
    private String getType;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private String checker = "";
    private Uri imageUri;
    private String myUrl = "";
    StorageTask uploadTask;
    private StorageReference storageProfilePicsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getType = getIntent().getStringExtra("type");
        Toast.makeText(this, getType, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(getType).child(customerID);
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");



        profileImageView = findViewById(R.id.profile_image);
        saveButton = findViewById(R.id.save);
        profileChangeBtn = findViewById(R.id.change_picture_btn);
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone_number);

        driverCarName = findViewById(R.id.driver_car_name);
        if (getType.equals("Driver"))
        {
            driverCarName.setVisibility(View.VISIBLE);
        }
        getUserInformation();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {

                    validateControllers();
                }
                else
                {

                    validateAndSaveOnlyInformation();
                }
            }
        });


        profileChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(profile.this);
            }
        });

        getUserInformation();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            if (getType.equals("Driver"))
            {
                startActivity(new Intent(profile.this, DriverMapsActivity.class));
            }
            else
            {
                startActivity(new Intent(profile.this, CustomerMapsActivity.class));
            }

            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
        }
    }


    private void validateControllers()
    {

        if (TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your name.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show();
        }
        else if (getType.equals("Driver")  &&  TextUtils.isEmpty(driverCarName.getText().toString()))
        {
            Toast.makeText(this, "Please provide your car Name.", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadProfilePicture();
        }
    }



    private void uploadProfilePicture()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Settings Account Information");
        progressDialog.setMessage("Please wait, while we are settings your account information");
        progressDialog.show();


        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePicsRef
                    .child(mAuth.getCurrentUser().getUid()  +  ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();


                        HashMap<String, Object> userMap = new HashMap<>();
                        //userMap.put("uid", mAuth.getCurrentUser().getUid());
                        userMap.put("cname", nameEditText.getText().toString());
                        userMap.put("cphone", phoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        if (getType.equals("Driver"))
                        {
                            userMap.put("cartype", driverCarName.getText().toString());
                        }
                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        if (getType.equals("Driver"))
                        {
                            startActivity(new Intent(profile.this, DriverMapsActivity.class));
                        }
                        else
                        {
                            startActivity(new Intent(profile.this, CustomerMapsActivity.class));
                        }
                        progressDialog.dismiss();


                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "Image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private void validateAndSaveOnlyInformation() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())) {
            Toast.makeText(this, "Please provide your name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(this, "Please provide your phone number.", Toast.LENGTH_SHORT).show();
        } else if (getType.equals("Drivers") && TextUtils.isEmpty(driverCarName.getText().toString())) {
            Toast.makeText(this, "Please provide your car Name.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> userMap = new HashMap<>();
            //userMap.put("uid", mAuth.getCurrentUser().getUid());
            userMap.put("cname", nameEditText.getText().toString());
            userMap.put("cphone", phoneEditText.getText().toString());

            if (getType.equals("Driver")) {
                userMap.put("cartype", driverCarName.getText().toString());
            }

            databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

            if (getType.equals("Driver")) {
                startActivity(new Intent(profile.this, DriverMapsActivity.class));
            } else {
                startActivity(new Intent(profile.this, CustomerMapsActivity.class));
            }
        }

    }


        private void getUserInformation()
        {
            databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        String name = dataSnapshot.child("cname").getValue().toString();
                        String phone = dataSnapshot.child("cphone").getValue().toString();

                        nameEditText.setText(name);
                        phoneEditText.setText(phone);

                        if (getType.equals("Drivers")) {
                            String car = dataSnapshot.child("cartype").getValue().toString();
                            driverCarName.setText(car);
                        }


                        if (dataSnapshot.hasChild("image")) {
                            String image = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(image).into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

}
