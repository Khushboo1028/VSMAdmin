package com.replon.www.vsmadmin.Catalogues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.replon.www.vsmadmin.R;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;
import com.replon.www.vsmadmin.Utility.SwipeToDelete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.annotation.Nullable;

public class AllCatalogsActivity extends AppCompatActivity {

    public static final String TAG = "AllCatalogsActivity";
    ImageView back,add_catalog;
    RecyclerView home_recyclerView, all_recyclerView;
    ArrayList<ContentsCatalogue> catalogueList, homeCatalogueList;
    CatalogAdapter homeAdapter, allAdapter;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    EditText searchView;
    ProgressBar progressBar;
    StorageReference mStorageRef;
    int deletedImageCount=0;
    int numberOfImages=0;
    DocumentReference doc_ref_to_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AllCatalogsActivity.this);

        setContentView(R.layout.activity_all_catalogs);

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




        add_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddImagesActivity.class);
                startActivity(intent);
            }
        });

       homeAdapter = new CatalogAdapter(this,homeCatalogueList);
       allAdapter  = new CatalogAdapter(this,catalogueList);

       home_recyclerView.setAdapter(homeAdapter);
       all_recyclerView.setAdapter(allAdapter);



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


        enableSwipeToDelete();
    }


    private void filter(String text){
        ArrayList<ContentsCatalogue> temp = new ArrayList();
        for(ContentsCatalogue d: catalogueList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().toLowerCase().contains(text) || d.getFabric().toLowerCase().contains(text) || d.getDate_created().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        homeAdapter.updateList(temp);
        allAdapter.updateList(temp);

    }
    private void init(){
        back = (ImageView) findViewById(R.id.back);
        add_catalog = (ImageView)findViewById(R.id.add_catalog);
        home_recyclerView = (RecyclerView) findViewById(R.id.home_recycler_view);
        all_recyclerView = (RecyclerView)findViewById(R.id.all_catalogs_recycler_view);
        searchView = (EditText) findViewById(R.id.search_field);


        catalogueList = new ArrayList<>();
        homeCatalogueList = new ArrayList<>();


        home_recyclerView.setHasFixedSize(true);
        all_recyclerView.setHasFixedSize(true);

        db=FirebaseFirestore.getInstance();

    }

    private void getData(){
        Query query = db.collection(getString(R.string.catalogues)).orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);

        listenerRegistration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i(TAG,"An error occurred"+e.getMessage());
                }

                if(snapshots!=null && !snapshots.getDocuments().isEmpty()){


                    catalogueList.clear();
                    for(QueryDocumentSnapshot doc : snapshots){

                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                        Timestamp ts_date_created = (Timestamp) doc.get(getString(R.string.date_created));
                        String date_created = sfd_viewFormat.format(ts_date_created.toDate());

                        String home_text,home_image_url;
                        Boolean home= Boolean.parseBoolean(doc.get(getString(R.string.home)).toString());
                        if(home){
                            home_text=doc.getString(getString(R.string.home_text));
                            home_image_url=doc.getString(getString(R.string.home_image_url));
                        }else{
                            home_text="";
                            home_image_url="";
                        }


                        catalogueList.add(new ContentsCatalogue(
                                doc.getString(getString(R.string.name)),
                                doc.getString(getString(R.string.fabric)),
                                doc.getString(getString(R.string.saree_length)),
                                doc.getString(getString(R.string.packaging)),
                                doc.getString(getString(R.string.pieces)),
                                Integer.parseInt(doc.get(getString(R.string.type)).toString()),
                                Integer.parseInt(doc.get(getString(R.string.tag)).toString()),
                                home,
                                home_text,
                                home_image_url,
                                doc.getString(getString(R.string.price)),
                                doc.getString(getString(R.string.catalogue_image_url)),
                                (ArrayList<String>)doc.get(getString(R.string.saree_image_url)),
                                doc.getString(getString(R.string.pdf_url)),
                                date_created,
                                doc.getId()

                        ));
                    }

                    allAdapter.notifyDataSetChanged();

                    homeCatalogueList.clear();
                    for(int i=0;i<catalogueList.size();i++){
                        if(catalogueList.get(i).isHome()){
                            if(homeCatalogueList.size()<5){
                                homeCatalogueList.add(catalogueList.get(i));

                            }

                        }
                    }

                    homeAdapter.notifyDataSetChanged();

                }
            }
        });
    }



    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,  int i) {

                final int position = viewHolder.getAdapterPosition();
                Log.i(TAG,"position is "+position);

                    new AlertDialog.Builder(AllCatalogsActivity.this,R.style.AlertDialogCustom)
                            .setTitle("")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {


                                    doc_ref_to_delete = db.collection(getString(R.string.catalogues)).document(catalogueList.get(position).getDocument_id());

                                    ArrayList<String> saree_image_url_delete = catalogueList.get(position).getSaree_image_url();
                                    String pdf_url_delete=catalogueList.get(position).getPdf_url();
                                    String catalog_image_url_delete=catalogueList.get(position).getCatalogue_image_url();



                                        for (int i = 0; i < saree_image_url_delete.size(); i++) {
                                            deleteImage(saree_image_url_delete.get(i),position);

                                        }

                                        deleteImage(pdf_url_delete,position);
                                        deleteImage(catalog_image_url_delete,position);

                                        Log.i(TAG,"catalog image to delete is"+catalog_image_url_delete);

                                        if(catalogueList.get(position).isHome()){
                                            deleteImage(catalogueList.get(position).getHome_image_url(),position);
                                            numberOfImages=saree_image_url_delete.size()+3;
                                        }else{
                                            numberOfImages=saree_image_url_delete.size()+2;
                                        }


                                    }




                            })
                            .setNegativeButton("Cancel", null).show();

                homeAdapter.notifyDataSetChanged();
                allAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
        itemTouchhelper.attachToRecyclerView(home_recyclerView);
        itemTouchhelper.attachToRecyclerView(all_recyclerView);
    }

    private void deleteImage(String url,final int pos){

        if(url!=null) {
            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            if (mStorageRef != null) {
                Log.i(TAG, "Image URl is " + mStorageRef.toString());
                mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: deleted image from storage");

                        deletedImageCount++;

                        Log.i(TAG, "Deleted image number " + deletedImageCount);
                        Toast.makeText(getApplicationContext(), "Successfully deleted image: " + deletedImageCount, Toast.LENGTH_SHORT).show();


                        if (deletedImageCount == numberOfImages) {
                            doc_ref_to_delete.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "Catalog Successfully deleted");
                                            Toast.makeText(getApplicationContext(), "Catalog Deleted", Toast.LENGTH_SHORT).show();
                                            catalogueList.remove(pos);
                                            allAdapter.notifyDataSetChanged();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG, "An error occurred in deleting document " + e.getMessage());
                                        }
                                    });
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: an error occurred " + e.getMessage());


                        doc_ref_to_delete.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i(TAG, "Catalog Successfully deleted");
                                        Toast.makeText(getApplicationContext(), "Catalog Deleted", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(TAG, "An error occurred in deleting this catalog, it may already be deleted " + e.getMessage());
                                    }
                                });


                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "An error occurred in deleting this catalog", Toast.LENGTH_SHORT).show();
            }
        }
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
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
