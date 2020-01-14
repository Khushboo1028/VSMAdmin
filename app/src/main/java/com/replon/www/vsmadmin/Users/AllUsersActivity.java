package com.replon.www.vsmadmin.Users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.replon.www.vsmadmin.R;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class AllUsersActivity extends AppCompatActivity {

    public static final String TAG = "AllUsersActivity";
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<ContentsUsers> usersList;
    ImageView back;
    EditText searchView;
    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AllUsersActivity.this);

        setContentView(R.layout.activity_all_users);

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


//        usersList=getIntent().getParcelableArrayListExtra("usersList");
//        Log.i(TAG,"usersList is "+usersList);

        userAdapter=new UserAdapter(this,usersList);
        recyclerView.setAdapter(userAdapter);



        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });



    }

    private void init(){

        recyclerView = (RecyclerView) findViewById(R.id.user_recycler_view);
        back=(ImageView)findViewById(R.id.back);
        searchView = findViewById(R.id.search_field);
        usersList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        db=FirebaseFirestore.getInstance();

    }

    private void filter(String text){
        ArrayList<ContentsUsers> temp = new ArrayList();
        for(ContentsUsers d: usersList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text) || d.getPhone().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        userAdapter.updateList(temp);

    }

    private void getData() {

        Query query = db.collection(getString(R.string.users)).orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);

        listenerRegistration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
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

                    userAdapter.notifyDataSetChanged();



                }else{
                    Log.i(TAG,"No data is present");
                }
            }
        });






    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
