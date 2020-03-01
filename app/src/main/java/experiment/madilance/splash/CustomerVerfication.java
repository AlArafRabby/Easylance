package experiment.madilance.splash;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class CustomerVerfication extends AppCompatActivity {

    EditText Customer_code,Customer_phone;
    Button Customer_verification,Customer_confirm;
    FirebaseAuth mAuth;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_verfication);

        Customer_code=findViewById(R.id.code_customer);
        Customer_phone=findViewById(R.id.phone_customer);
        Customer_verification =findViewById(R.id.verify_customer);
        Customer_confirm=findViewById(R.id.confirm_customer);
        mAuth=FirebaseAuth.getInstance();


        Customer_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile=Customer_phone.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        mobile,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        CustomerVerfication.this,               // Activity (for callback binding)
                        mCallbacks);
                Toast.makeText(CustomerVerfication.this,"Confirmed",Toast.LENGTH_SHORT).show();

            }
        });

        Customer_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code_number=Customer_code.getText().toString();
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code_number);
                signInWithPhoneAuthCredential(credential);

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Toast.makeText(CustomerVerfication.this,"Write the number carefully to avoid error",Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(getApplicationContext(),"Code send",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(CustomerVerfication.this,"error :"+e,Toast.LENGTH_SHORT).show();

            }


        };
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


    private void signInWithPhoneAuthCredential(PhoneAuthCredential Credential) {
        mAuth.signInWithCredential(Credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent=new Intent(CustomerVerfication.this,CustomerMapsActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Sign in success",Toast.LENGTH_SHORT).show();


                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(getApplicationContext(),"Sign in Failed",Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
