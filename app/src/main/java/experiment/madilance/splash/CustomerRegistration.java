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

public class CustomerRegistration extends AppCompatActivity {

    EditText cname,cemail,cpassword,cmobile;
    Button customerRegister;
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
        setContentView(R.layout.activity_customer_registration);

        cname=findViewById(R.id.customerName);
        cemail=findViewById(R.id.customeremail);
        cpassword=findViewById(R.id.customerpassword);
        cmobile=findViewById(R.id.customermobile);
        customerRegister=findViewById(R.id.customerRegister);
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        ref=db.getReference().child("User").child("Customer");

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public  void  onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent = new Intent(CustomerRegistration.this, CustomerMapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }


       };

        customerRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerRegister();
            }
        });



    }



    private void CustomerRegister() {



        boolean bol= checkValidation();

        if(bol == false){
            final String Name,Phone,Email,Password;
            Email=cemail.getText().toString();
            Password=cpassword.getText().toString();
            Phone=cmobile.getText().toString();
            Name=cname.getText().toString();


            mAuth.createUserWithEmailAndPassword(Email,Password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            Customer customer=new Customer();
                            customer.setCname(Name);
                            customer.setCemail(Email);
                            customer.setCpassword(Password);
                            customer.setCmobile(Phone);
                            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent intent=new Intent(getApplicationContext(),CustomerVerfication.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"registration success",Toast.LENGTH_SHORT).show();
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

    }


    Boolean checkValidation() {

        final String Name,Phone,emailid,Password;
        emailid=cemail.getText().toString();
        Password=cpassword.getText().toString();
        Phone=cmobile.getText().toString();
        Name=cname.getText().toString();


        Boolean flag = false;
        if (emailid.isEmpty() && Password.isEmpty() && Phone.isEmpty() && Name.isEmpty()) {
            Toast.makeText(this, "Filup Info.", Toast.LENGTH_SHORT).show();
            cemail.setError("Enter Email");
            cpassword.setError("Enter Password");
            cmobile.setError("Enter mobile");
            cname.setError("Enter name");
            flag = true;


        } if (!emailid.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
            cemail.setError("Enter valid email Id");
            flag = true;
        }
        if (Password.length() < 6) {
            cpassword.setError("At Least 6 Charecter");
            flag = true;

        }
        if (Phone.length() < 11) {
            cmobile.setError("At Least 11 Charecter");
            flag = true;

        }return flag;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerRegistration.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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

}
