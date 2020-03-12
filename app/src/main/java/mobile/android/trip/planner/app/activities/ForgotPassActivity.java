package mobile.android.trip.planner.app.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import mobile.android.trip.planner.app.R;

public class ForgotPassActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    TextInputEditText edtPassword, edtEmail;
    TextInputLayout passLay,userLay;
    Button btnCheck,btnCheckEmail;
    MKLoader mkLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        edtEmail = findViewById(R.id.edtEmail);
        passLay = findViewById(R.id.passLay);
        userLay = findViewById(R.id.userLay);
        edtPassword = findViewById(R.id.edtPassword);
        mkLoader = findViewById(R.id.mkloader);
        btnCheck = findViewById(R.id.btnCheckSet);
        btnCheckEmail = findViewById(R.id.btnCheckEmail);
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidatePassword()) {
                    mkLoader.setVisibility(View.VISIBLE);
                    firebaseDatabase.getReference("user_data").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isTrue = false;
                            for (DataSnapshot childInner : dataSnapshot.getChildren()) {

                                if (childInner.child("emailId").getValue().equals(edtEmail.getText().toString().trim())) {
                                    mkLoader.setVisibility(View.GONE);
                                    isTrue=true;
                                    String key = childInner.getKey();
                                    firebaseDatabase.getReference("user_data").child(key).child("password").setValue(edtPassword.getText().toString().trim());
                                    Toast.makeText(ForgotPassActivity.this, "Password update successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                    btnCheck.setVisibility(View.GONE);
                                    btnCheckEmail.setVisibility(View.GONE);

                                }

                            }
                            if (!isTrue) {
                                mkLoader.setVisibility(View.VISIBLE);
                                Toast.makeText(ForgotPassActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mkLoader.setVisibility(View.GONE);
                            Toast.makeText(ForgotPassActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });
        btnCheckEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidateEmail()) {
                    mkLoader.setVisibility(View.VISIBLE);
                    firebaseDatabase.getReference("user_data").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean isTrue = false;
                            for (DataSnapshot childInner : dataSnapshot.getChildren()) {

                                if (childInner.child("emailId").getValue().equals(edtEmail.getText().toString().trim())) {
                                    isTrue=true;
                                    mkLoader.setVisibility(View.GONE);
                                    passLay.setVisibility(View.VISIBLE);
                                    userLay.setVisibility(View.GONE);
                                    btnCheck.setVisibility(View.VISIBLE);
                                    btnCheckEmail.setVisibility(View.GONE);

                                }

                            }
                            if (!isTrue){
                                mkLoader.setVisibility(View.GONE);
                                Toast.makeText(ForgotPassActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();}


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            mkLoader.setVisibility(View.GONE);
                            Toast.makeText(ForgotPassActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private boolean isValidateEmail() {
        if (edtEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            edtEmail.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValidatePassword() {
        if (edtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

}
