package com.replon.www.vsmadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.replon.www.vsmadmin.Catalogues.AddImagesActivity;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    Button login;
    EditText et_email,et_password;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    String token;
    CheckBox showPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), LoginActivity.this);

        setContentView(R.layout.activity_login);

        init();


        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });



        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onChecked(showPass.isChecked());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                String email=et_email.getText().toString().toLowerCase().trim();
                String password=et_password.getText().toString();

                if(email.isEmpty() || password.isEmpty()){
                    showMessage("Uh-Oh","One or more fields are missing!");

                }else{
                    login.setEnabled(false);
                    et_email.setEnabled(false);
                    et_password.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.i(TAG,"Sign in Successful");

                                    getToken();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG,"An error occurred " +e.getMessage());

                                    String exception=e.getMessage();
                                    int index=exception.indexOf(":");
                                    String data=exception.substring(index+1).trim();
                                    showMessage("Error",data);
                                    progressBar.setVisibility(View.GONE);
                                    login.setEnabled(true);
                                    et_email.setEnabled(true);
                                    et_password.setEnabled(true);
                                    changeUI();
                                }
                            });
                }

            }
        });


    }


    public void onChecked(boolean checked){
        if(checked){
            et_password.setTransformationMethod(null);
        }else {
            et_password.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        et_password.setSelection(et_password.getText().length());
    }

    private void init(){
        login = (Button) findViewById(R.id.btn_login);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();

        showPass=(CheckBox)findViewById(R.id.showPass);
    }



    private void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();


                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                        DocumentReference doc_ref=db.collection(getString(R.string.superuser)).document(getString(R.string.admin_name));

                        Map<String, Object> data = new HashMap<>();
                        data.put(getString(R.string.email),mAuth.getCurrentUser().getEmail());
                        data.put(getString(R.string.token),FieldValue.arrayUnion(token));

                        doc_ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                Intent login_intent = new Intent(getApplicationContext(), MainActivity.class);
                                login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(login_intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(),"An error occurred "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }
    private void changeUI() {

        Vibrator vibrator = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(500);
        }
    }
    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
