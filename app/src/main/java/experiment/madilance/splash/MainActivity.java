package experiment.madilance.splash;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button c,d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        c=findViewById(R.id.customer);
        d=findViewById(R.id.Driver);


        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cintent=new Intent(MainActivity.this,CustomerRegistration.class);
                startActivity(cintent);
            }
        });


        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dintent=new Intent(MainActivity.this,DriverRegistration.class);
                startActivity(dintent);
            }
        });


    }
}
