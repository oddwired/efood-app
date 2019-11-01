package com.sparkvirus.kshem.food;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sparkvirus.kshem.food.R;

public class SettingsActivity extends AppCompatActivity {

    TextView name, address, phone;
    Button edit_info;

    private static final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database;
    DatabaseReference dbRef;

    String userId;
    User value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone);
        edit_info = (Button) findViewById(R.id.btn_edit);

        //name.setText("Shem");

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("users");

        userId = mAuth.getCurrentUser().getUid();

        dbRef.child(userId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                value = dataSnapshot.getValue(User.class);
                //String d_address = dataSnapshot.child("users").child(userId).child("address").getValue(String.class);
                //String d_phone = dataSnapshot.child("users").child(userId).child("phone_number").getValue(String.class);

                name.setText(value.name);
                address.setText(value.address);
                phone.setText(value.phone_number);
                Toast.makeText(SettingsActivity.this, "retrieving data successful",
                        Toast.LENGTH_LONG).show();

                //Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(SettingsActivity.this, "Error retrieving data",
                        Toast.LENGTH_LONG).show();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, InfoAcivity.class));
            }
        });
    }
}
