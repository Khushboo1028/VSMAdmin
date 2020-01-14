package com.replon.www.vsmadmin.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.vsmadmin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewUserActivity extends AppCompatActivity {

    public static final String TAG = "ViewUserActivity";
    ProgressBar progressBar;
    ArrayList<ContentsUsers> usersList;
    EditText et_phone,et_name,et_company_name,et_gst_no, et_city;
    Button btn_update;
    int position;

    FirebaseFirestore db;
    ImageView back;
    Boolean isConnected,monitoringConnectivity;

    Spinner state_spinner;
    String [] states;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        init();

        et_phone.setText(usersList.get(position).getPhone());
        et_name.setText(usersList.get(position).getName());
        et_company_name.setText(usersList.get(position).getCompany_name());
        et_gst_no.setText(usersList.get(position).getGst_no());
        et_city.setText(usersList.get(position).getCity());

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this,R.layout.spinner_item, states);

        stateAdapter.setDropDownViewResource(R.layout.spinner_item);
        state_spinner.setAdapter(stateAdapter);

        state_spinner.setSelection(stateAdapter.getPosition(usersList.get(position).getState()));

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnectivity();
                updateData("","Are you sure you want to update "+usersList.get(position).getName()+"'s profile ?");

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private void init(){
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        usersList=getIntent().getParcelableArrayListExtra("usersList");
        position=getIntent().getIntExtra("position",0);

        et_name=(EditText)findViewById(R.id.et_name);
        et_company_name=(EditText)findViewById(R.id.et_company_name);
        et_gst_no=(EditText)findViewById(R.id.et_gst_no);
        et_city =(EditText)findViewById(R.id.et_city);

        et_phone=(EditText)findViewById(R.id.et_phone);
        btn_update=(Button)findViewById(R.id.btn_update);

        state_spinner=(Spinner)findViewById(R.id.state_spinner);
        states = getResources().getStringArray(R.array.india_states);
        back=(ImageView)findViewById(R.id.back);

        db=FirebaseFirestore.getInstance();

        et_phone.setEnabled(false);



    }


    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(ViewUserActivity.this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }


    public void updateData(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(ViewUserActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!isConnected){
                    showMessage("Oops!","Please check your internet connections");
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String, Object> data = new HashMap<>();
                    data.put(getString(R.string.name), et_name.getText().toString().trim());
                    data.put(getString(R.string.company), et_company_name.getText().toString().trim());
                    data.put(getString(R.string.city), et_city.getText().toString().trim());
                    data.put(getString(R.string.gst), et_gst_no.getText().toString().trim().toUpperCase());
                    data.put(getString(R.string.state),state_spinner.getSelectedItem());

                    DocumentReference doc_ref = db.collection(getString(R.string.users)).document(usersList.get(position).getDocument_id());

                    doc_ref.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            onBackPressed();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.i(TAG, "An error occurred in updating data" + e.getMessage());
                            showMessage("Oops!", "An internal error occurred");
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    //check for internet

    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            Log.i(TAG, "INTERNET CONNECTED");
        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            Log.i(TAG,"Internet lost");

            showMessage("Oops!","Please check your internet connection!");

        }
    };



    private void checkConnectivity() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();


        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {

            Log.i(TAG, " NO NETWORK!");
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
            showMessage("Oops!","Please check your internet connection!");

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }


}
