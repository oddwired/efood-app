package com.sparkvirus.kshem.food;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sparkvirus.kshem.food.R;

public class InfoAcivity extends AppCompatActivity {
    EditText name;
    EditText address;
    EditText phone_number;
    Button update_info;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mFirebaseRef;
    private FirebaseDatabase mFirebaseInstance;

    String userId;

    //private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(InfoAcivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseRef = mFirebaseInstance.getReference("users");

        userId = mAuth.getCurrentUser().getUid();

        name = (EditText) findViewById(R.id.input_name);
        address = (EditText) findViewById(R.id.input_address);
        phone_number = (EditText) findViewById(R.id.input_mobile);
        update_info = (Button) findViewById(R.id.btn_update);

        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(InfoAcivity.this);
                pd.setMessage("Please Wait");
                pd.show();
                if(TextUtils.isEmpty(name.getText().toString())){
                    name.setError("Required");
                }else if(TextUtils.isEmpty(phone_number.getText().toString())){
                    phone_number.setError("Required");
                }else if(TextUtils.isEmpty(address.getText().toString())){
                    address.setError("Required");
                }else {
                    User user_data = new User(name.getText().toString(), phone_number.getText().toString(), address.getText().toString());

                    mFirebaseRef.child(userId).setValue(user_data);
                    startActivity(new Intent(InfoAcivity.this, MenuActivity.class));
                }
                pd.hide();
            }
        });

    }
}
