package mobile.android.trip.planner.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tuyenmonkey.mkloader.MKLoader;

import mobile.android.trip.planner.app.R;
import mobile.android.trip.planner.app.pref.PreferenceManager;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    TextInputEditText edtPassword, edtUsername;
    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    TextView txtForgotPass;
    private PreferenceManager prefManager;
    MKLoader mkLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        mkLoader = findViewById(R.id.mkloader);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        txtForgotPass = findViewById(R.id.txtForgotPass);
        prefManager = new PreferenceManager(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeStatusBarColor();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mkLoader.setVisibility(View.VISIBLE);
                if (isValidateLogin()) {

                    isAdminOrUser(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim());
                }
                else {
                    mkLoader.setVisibility(View.INVISIBLE);
                }
            }
        });
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
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

    private void isAdminOrUser(final String username, final String password) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {


            firebaseDatabase.getReference("user_data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isTrue = false;
                    for (DataSnapshot childInner : dataSnapshot.getChildren()) {

                        if (childInner.child("username").getValue().equals(username) && childInner.child("password").getValue().equals(password)) {
                            isTrue = true;
                            mkLoader.setVisibility(View.INVISIBLE);
                            launchHomeScreen(childInner.child("emailId").getValue().toString().replace(".", "_"));
                            Toast.makeText(LoginActivity.this, "User login successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                    if (!isTrue) {
                        mkLoader.setVisibility(View.INVISIBLE);
                        Toast.makeText(LoginActivity.this, "Please enter correct username and password", Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mkLoader.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private boolean isValidateLogin() {
        if (edtUsername.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            edtUsername.requestFocus();
            return false;
        } else if (edtPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            edtPassword.requestFocus();
            return false;

        }
        return true;
    }

    private void launchHomeScreen(String key) {
        prefManager.setFirstTimeLaunch(false);
        prefManager.setUserKey(key);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
