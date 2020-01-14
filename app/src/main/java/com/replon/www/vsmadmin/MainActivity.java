package com.replon.www.vsmadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.replon.www.vsmadmin.Catalogues.AllCatalogsActivity;
import com.replon.www.vsmadmin.Users.AllUsersActivity;
import com.replon.www.vsmadmin.Users.ContentsUsers;
import com.replon.www.vsmadmin.Users.UserAdapter;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String CHANNEL_ID="NOTIF";
    public static final String CHANNEL_NAME="Notifications";
    public static final String CHANNEL_DESC="This channel is for all notifications";

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<ContentsUsers> usersList,unapprovedList;
    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;

    Button btn_users,btn_collections;
    ImageView logout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MainActivity.this);

        setContentView(R.layout.activity_main);

        init();

        setupFirebaseAuth();

        if(mAuth.getCurrentUser()!=null){
            getToken();
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }




        userAdapter = new UserAdapter(this,unapprovedList);
        recyclerView.setAdapter(userAdapter);


        btn_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AllUsersActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("usersList",usersList);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

            }
        });

        btn_collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AllCatalogsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("usersList",usersList);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogOutDialog("","Are you sure you want to logout?");
            }
        });
    }


    private void init(){
        logout = (ImageView) findViewById(R.id.logout);
        recyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);
        btn_users=(Button)findViewById(R.id.btn_users);
        btn_collections=(Button)findViewById(R.id.btn_collections);
        usersList = new ArrayList<>();
        unapprovedList=new ArrayList<>();

        db=FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);

    }

    private void getData() {

        Query query = db.collection(getString(R.string.users)).orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);

        listenerRegistration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(TAG,"error is "+e.getMessage());
                }

                if(snapshot!=null && !snapshot.getDocuments().isEmpty()){

                    usersList.clear();
                    for(QueryDocumentSnapshot doc : snapshot ){

                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                        Timestamp ts_date_created = (Timestamp) doc.get(getString(R.string.date_created));
                        String date_created = sfd_viewFormat.format(ts_date_created.toDate());


                        usersList.add(new ContentsUsers(

                                doc.getString(getString(R.string.phone)),
                                doc.getString(getString(R.string.name)),
                                doc.getString(getString(R.string.gst)),
                                doc.getString(getString(R.string.company)),
                                doc.getString(getString(R.string.city)),
                                doc.getString(getString(R.string.state)),
                                Boolean.parseBoolean(doc.get(getString(R.string.dealer)).toString()),
                                date_created,
                                doc.getId()

                        ));



                    }

                    unapprovedList.clear();

                    for(int i=0;i<usersList.size();i++){
                        if(!usersList.get(i).getCurrent_status()){
                            unapprovedList.add(usersList.get(i));
                        }
                    }


                    userAdapter.notifyDataSetChanged();



                }else{
                    Log.i(TAG,"No data is present");
                }
            }
        });



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


                    }
                });
    }

    public void showLogOutDialog(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DocumentReference doc_id=FirebaseFirestore.getInstance().collection(getString(R.string.superuser)).document(getString(R.string.admin_name));

                doc_id.update("token", FieldValue.arrayRemove(token)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG,"Token removed");
                        mAuth.signOut();
                        finish();
                        Toast.makeText(getApplicationContext(),"SignOut Successful",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,"An error occurred : "+e.getMessage());
                        Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }


    //firebaseAuth
    private void setupFirebaseAuth() {
        Log.d(TAG,"onAuthStateChanged:Setting up firebase auth");

        // Obtain the FirebaseAnalytics instance.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();


                //checks if the user is logged in
                checkCurrentUser(currentUser);
                if (currentUser != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in");
                    currentUser.reload();


                }
                else{
                    //user is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };

    }

    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG,"Checking if user is logged in");

        if(user==null){
            Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        }else{
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String user_id=currentFirebaseUser.getUid();



        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        checkCurrentUser(currentUser);



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if(listenerRegistration!=null){
            listenerRegistration.remove();
            listenerRegistration=null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
