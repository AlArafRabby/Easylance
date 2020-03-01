package experiment.madilance.splash;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegistration extends AppCompatActivity {


    EditText dname,demail,dpassword,dmobile,dlicence;
    Button driverRegister;
    ProgressDialog progressDialog;
    FirebaseDatabase db;
    DatabaseReference ref;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }




    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        dname=findViewById(R.id.driverName);
        demail=findViewById(R.id.driveremail);
        dpassword=findViewById(R.id.driverpassword);
        dmobile=findViewById(R.id.drivermobile);
        dlicence=findViewById(R.id.driverlicence);
        driverRegister=findViewById(R.id.driverRegister);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        ref=db.getReference().child("User").child("Driver");


        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(DriverRegistration.this,DriverVerification.class);
                    startActivity(intent);
                    finish();
                }
            }


        };

        driverRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverRegister();

            }
        });


    }




    private void DriverRegister() {



        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering In ....");
        progressDialog.show();


        boolean bol= checkValidation();
        if(bol == false){

            final String Name,Phone,Email,Password,Licence;
            Name=dname.getText().toString();
            Licence=dlicence.getText().toString();
            Email=demail.getText().toString();
            Password=dpassword.getText().toString();
            Phone=dmobile.getText().toString();


            mAuth.createUserWithEmailAndPassword(Email,Password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            Driver driver = new Driver();
                            driver.setDname(Name);
                            driver.setDemail(Email);
                            driver.setDpassword(Password);
                            driver.setDmobile(Phone);
                            driver.setDlicence(Licence);
                            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    Toast.makeText(getApplicationContext(),"registration success",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(DriverRegistration.this,DriverMapsActivity.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }else if (bol == true) {
            Toast.makeText(getApplicationContext(), "Data not valid", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();

    }


    Boolean checkValidation() {

        final String Name,Phone,Email,Password,Licence;
        Name=dname.getText().toString();
        Licence=dlicence.getText().toString();
        Email=demail.getText().toString();
        Password=dpassword.getText().toString();
        Phone=dmobile.getText().toString();


        Boolean flag = false;
        if (Email.isEmpty() && Password.isEmpty() && Phone.isEmpty() && Name.isEmpty() && Licence.isEmpty()) {
            Toast.makeText(this, "Filup Info.", Toast.LENGTH_SHORT).show();
            demail.setError("Enter Email");
            dpassword.setError("Enter Password");
            dmobile.setError("Enter mobile");
            dname.setError("Enter Name");
            dlicence.setError("Enter licence");
            flag = true;


        } if (!Email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            demail.setError("Enter valid email Id");
            flag = true;
        }
        if (Password.length() < 6) {
            dpassword.setError("At Least 6 Charecter");
            flag = true;

        }
        if (Phone.length() < 11) {
            dmobile.setError("At Least 11 Charecter");
            flag = true;

        }return flag;
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
}
