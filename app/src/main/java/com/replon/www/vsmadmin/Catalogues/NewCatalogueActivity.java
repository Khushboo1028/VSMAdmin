package com.replon.www.vsmadmin.Catalogues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.replon.www.vsmadmin.R;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewCatalogueActivity extends AppCompatActivity {

    public static final String TAG = "NewCatalogueActivity";
    public static final int REQUEST_DATA = 20;

    ImageView back;
    EditText et_name,et_fabric, et_saree_length, et_packaging, et_pieces, et_price, et_home_text;
    Spinner type_spinner, tag_spinner;
    CheckBox checkBox_home_page;

    TextView tv_view_images;
    Button btn_add_catalog;
    FirebaseFirestore db;
    ProgressBar progressBar;

    ArrayList<String> tagList, typeList,saree_image_url,sareeImageUriListString;
    String homeImageUrl;
    String catalogImageUrl,pdfUrl;
    String str_tag,str_type;
    Boolean isConnected,monitoringConnectivity;

    String catalog_name,fabric,saree_length,packaging,pieces,price,home_text;
    Boolean isHome;

    ArrayList<Uri> sareeImageUriList;
    Uri homeImageUri, catalogImageUri,pdfUri;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageTask uploadTask;
    String downloadUrl;

    ArrayList<String> allDownloadsForCount;
    int tag,type;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), NewCatalogueActivity.this);
        setContentView(R.layout.activity_catalog_details);

        init();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_view_images.setText("Add Images");

        btn_add_catalog.setText("ADD CATALOG");

        tagList.add(getString(R.string.new_arrivals));
        tagList.add(getString(R.string.best_seller));
        tagList.add(getString(R.string.featured));
        tagList.add(getString(R.string.none));


        typeList.add(getString(R.string.VSM_weaves));
        typeList.add(getString(R.string.VSM_sarees));





        ArrayAdapter<String> tagDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagList);
        ArrayAdapter<String> typeDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);

        tagDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tag_spinner.setAdapter(tagDataAdapter);
        type_spinner.setAdapter(typeDataAdapter);

        tv_view_images.setVisibility(View.GONE);

        if(isHome){
            checkBox_home_page.setChecked(true);
            et_home_text.setVisibility(View.VISIBLE);

        }else{
            checkBox_home_page.setChecked(false);
            et_home_text.setVisibility(View.GONE);
        }

        checkBox_home_page.setEnabled(false);
        checkBox_home_page.setVisibility(View.GONE);

        checkBox_home_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox_home_page.isChecked()){
                    et_home_text.setVisibility(View.VISIBLE);
                }else{
                    et_home_text.setVisibility(View.GONE);
                }
            }
        });

//        tv_view_images.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(),AddImagesActivity.class);
//                intent.putExtra("isHome",checkBox_home_page.isChecked());
//                if(homeImageUri!=null){
//                    intent.putExtra("homeImageUriString",homeImageUri.toString());
//
//                }
//                if(catalogImageUri!=null){
//                    intent.putExtra("catalogImageUriString",catalogImageUri.toString());
//
//                }
//                if(pdfUri!=null){
//                    intent.putExtra("pdfUriString",pdfUri.toString());
//
//                }
//                if(sareeImageUriListString!=null && sareeImageUriListString.size()!=0){
//                    intent.putStringArrayListExtra("sareeImageUriListString",sareeImageUriListString);
//                }
//                startActivityForResult(intent,REQUEST_DATA);
//            }
//        });

        btn_add_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHome=checkBox_home_page.isChecked();
                catalog_name=et_name.getText().toString().trim();
                fabric=et_fabric.getText().toString().trim();
                saree_length=et_saree_length.getText().toString().trim();
                packaging=et_packaging.getText().toString().trim();
                pieces=et_pieces.getText().toString().trim();
                price=et_price.getText().toString().trim();
                home_text=et_home_text.getText().toString().trim();
                checkConnectivity();

                if(!isConnected){
                    showMessage("Oops!","Please check your internet connections");

                }
                else if(isHome){
                    if(catalog_name.isEmpty() || fabric.isEmpty() || saree_length.isEmpty() || packaging.isEmpty() || pieces.isEmpty()
                            || price.isEmpty() || home_text.isEmpty() || catalogImageUri ==null || homeImageUri == null || sareeImageUriList.size()==0 ||home_text.isEmpty()){
                        showMessage("Uh - Oh", "One or more fields are missing! Have you entered all images? You have marked home as true! Have you entered home image?");
                    }else if(checkForSpecialCharacter(price) || checkForSpecialCharacter(pieces)){
                        showMessage("Uh - Oh", "Only numeric entry for price and pieces!");

                    }
                    else{

                        uploadImage(pdfUri);
                        uploadImage(catalogImageUri);
                        uploadImage(homeImageUri);

                        for(int i=0;i<sareeImageUriList.size();i++){
                            uploadImage(sareeImageUriList.get(i));
                        }
                    }

                }else  if(!isHome){
                    if(catalog_name.isEmpty() || fabric.isEmpty() || saree_length.isEmpty() || packaging.isEmpty() || pieces.isEmpty()
                            || price.isEmpty() || catalogImageUri ==null || sareeImageUriList.size()==0 ){
                        showMessage("Uh - Oh", "One or more fields are missing! Have you entered all images?");
                    }else if(checkForSpecialCharacter(price) || checkForSpecialCharacter(pieces)){
                        showMessage("Uh - Oh", "Only numeric entry for price and pieces!");

                    }

                    else{

                        uploadImage(pdfUri);
                        uploadImage(catalogImageUri);

                        for(int i=0;i<sareeImageUriList.size();i++){
                            uploadImage(sareeImageUriList.get(i));
                        }
                    }

                }

            }
        });



    }

    private void init(){
        back = (ImageView) findViewById(R.id.back);

        et_name = (EditText) findViewById(R.id.et_name);
        et_fabric = (EditText) findViewById(R.id.et_fabric);
        et_saree_length = (EditText) findViewById(R.id.et_saree_length);
        et_packaging = (EditText) findViewById(R.id.et_packaging);
        et_pieces = (EditText) findViewById(R.id.et_pieces);
        et_price = (EditText) findViewById(R.id.et_price);
        et_home_text = (EditText) findViewById(R.id.et_home_text);
        tv_view_images=(TextView)findViewById(R.id.tv_view_images);
        btn_add_catalog =(Button) findViewById(R.id.btn_update);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        tag_spinner = (Spinner) findViewById(R.id.tag_spinner);
        checkBox_home_page = (CheckBox) findViewById(R.id.checkbox_home_page);

        isHome=getIntent().getBooleanExtra("isHome",false);
        tagList=new ArrayList<>();
        typeList=new ArrayList<>();
        sareeImageUriList=new ArrayList<>();
        saree_image_url=new ArrayList<>();
        allDownloadsForCount=new ArrayList<>();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if(isHome){
            homeImageUri=Uri.parse(getIntent().getStringExtra("homeImageUriString"));
            Log.i(TAG,"homeImageUri from intent is "+homeImageUri);
        }

        if(getIntent().getStringExtra("catalogImageUriString")!=null){
            catalogImageUri=Uri.parse(getIntent().getStringExtra("catalogImageUriString"));
            Log.i(TAG,"catalogImageUri from intent is "+catalogImageUri);
        }

        if(getIntent().getStringExtra("pdfUriString")!=null){
            pdfUri=Uri.parse(getIntent().getStringExtra("pdfUriString"));
            Log.i(TAG,"pdfUri from intent is "+pdfUri);

        }

        if(getIntent().getParcelableArrayListExtra("sareeImageUriList")!=null){
            sareeImageUriList=getIntent().getParcelableArrayListExtra("sareeImageUriList");
            Log.i(TAG,"sareeImageUri from intent is "+sareeImageUriList);

        }

        db=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);

    }


    private boolean checkForSpecialCharacter(String text){
        Pattern p = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();

        if (b){
            return true;
        }else{
            return false;
        }
    }


    private void uploadImage(final Uri imageUri){

//        progressBar.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("This will take some time"); // Setting Message
        progressDialog.setTitle("Please Wait..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        et_name.setEnabled(false);
        et_price.setEnabled(false);
        et_fabric.setEnabled(false);
        et_saree_length.setEnabled(false);
        et_packaging.setEnabled(false);
        et_pieces.setEnabled(false);
        et_price.setEnabled(false);
        et_home_text.setEnabled(false);
        btn_add_catalog.setEnabled(false);


        final StorageReference ref = storageReference.child("catalog_images/"+ UUID.randomUUID().toString());
        uploadTask=ref.putFile(imageUri);


        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    downloadUrl= task.getResult().toString();
                    allDownloadsForCount.add(downloadUrl);

                    if (imageUri.equals(homeImageUri)){
                        homeImageUrl=downloadUrl;
                        Log.i(TAG,"Home image uploaded");
                    }

                    if (imageUri.equals(catalogImageUri)) {
                        catalogImageUrl=downloadUrl;
                        Log.i(TAG,"catalog image uploaded");
                    }

                    if(imageUri.equals(pdfUri)){
                        pdfUrl=downloadUrl;
                        Log.i(TAG,"Pdf uploaded");
                    }

                    if(sareeImageUriList.contains(imageUri)){
                        saree_image_url.add(downloadUrl);
                        Log.i(TAG,"saree image uploaded" );
                    }

                    Log.i(TAG,"allDownloadsForCount is " +allDownloadsForCount.size());




                    if(isHome){
                        int sizeOfUploads= sareeImageUriList.size()+3;

                        progressDialog.setMessage("Successfully uploaded image " +allDownloadsForCount.size() +" / " +sizeOfUploads);

                        if(allDownloadsForCount.size()==sizeOfUploads){
                            DocumentReference docRef=db.collection(getString(R.string.catalogues)).document();

                            if(type_spinner.getSelectedItem().toString().equals(getString(R.string.VSM_weaves))){
                                type=0;
                            }else if(type_spinner.getSelectedItem().toString().equals(getString(R.string.VSM_sarees))){
                                type=1;
                            }

                            if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.none))){
                                tag=0;
                            }else  if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.best_seller))){
                                tag=1;
                            }else  if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.featured))){
                                tag=2;
                            }else  if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.new_arrivals))){
                                tag=3;
                            }
                            Map<String, Object> docData = new HashMap<>();
                            docData.put(getString(R.string.date_created), new Timestamp(new Date()));
                            docData.put(getString(R.string.catalogue_image_url), catalogImageUrl);
                            docData.put(getString(R.string.fabric), fabric);
                            docData.put(getString(R.string.home), true);
                            docData.put(getString(R.string.home_image_url), homeImageUrl);
                            docData.put(getString(R.string.home_text), home_text);
                            docData.put(getString(R.string.name), catalog_name);
                            docData.put(getString(R.string.packaging), packaging);
                            docData.put(getString(R.string.pieces), pieces);
                            docData.put(getString(R.string.price), price);
                            docData.put(getString(R.string.saree_image_url), saree_image_url);
                            docData.put(getString(R.string.saree_length), saree_length);
                            docData.put(getString(R.string.tag),tag );
                            docData.put(getString(R.string.type),type );
                            docData.put(getString(R.string.pdf_url),pdfUrl );
                            docData.put(getString(R.string.doc_ref),docRef );



                            docRef.set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressBar.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    ((ResultReceiver)getIntent().getParcelableExtra("finisher")).send(1, new Bundle());
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.i(TAG,"An error occurred "+e.getMessage());
                                    progressBar.setVisibility(View.GONE);
                                    showMessage("Oops","An error occurred "+e.getMessage());
                                    progressDialog.dismiss();

                                    et_name.setEnabled(true);
                                    et_price.setEnabled(true);
                                    et_fabric.setEnabled(true);
                                    et_saree_length.setEnabled(true);
                                    et_packaging.setEnabled(true);
                                    et_pieces.setEnabled(true);
                                    et_price.setEnabled(true);
                                    et_home_text.setEnabled(true);
                                    btn_add_catalog.setEnabled(true);
                                }
                            });
                        }
                    }else{
                        int sizeOfUploads= sareeImageUriList.size()+2;

                        progressDialog.setMessage("Successfully uploaded image " +allDownloadsForCount.size() +" / " +sizeOfUploads);

                        if(allDownloadsForCount.size()==sizeOfUploads){
//                            addData(false);
                            Log.i(TAG,"in not isHome of uploadTask");

                            DocumentReference docRef=db.collection(getString(R.string.catalogues)).document();

                            if(type_spinner.getSelectedItem().toString().equals(getString(R.string.VSM_weaves))){
                                type=0;
                            }else if(type_spinner.getSelectedItem().toString().equals(getString(R.string.VSM_sarees))){
                                type=1;
                            }

                            if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.none))){
                                tag=0;
                            }else  if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.best_seller))){
                                tag=1;
                            }else  if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.featured))){
                                tag=2;
                            }else  if(tag_spinner.getSelectedItem().toString().equals(getString(R.string.new_arrivals))){
                                tag=3;
                            }
                            Map<String, Object> docData = new HashMap<>();
                            docData.put(getString(R.string.date_created), new Timestamp(new Date()));
                            docData.put(getString(R.string.catalogue_image_url), catalogImageUrl);
                            docData.put(getString(R.string.fabric), fabric);
                            docData.put(getString(R.string.home), false);
                            docData.put(getString(R.string.name), catalog_name);
                            docData.put(getString(R.string.packaging), packaging);
                            docData.put(getString(R.string.pieces), pieces);
                            docData.put(getString(R.string.price), price);
                            docData.put(getString(R.string.saree_image_url), saree_image_url);
                            docData.put(getString(R.string.saree_length), saree_length);
                            docData.put(getString(R.string.tag),tag );
                            docData.put(getString(R.string.type),type );
                            docData.put(getString(R.string.pdf_url),pdfUrl );
                            docData.put(getString(R.string.doc_ref),docRef );



                            docRef.set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressBar.setVisibility(View.GONE);
                                    ((ResultReceiver)getIntent().getParcelableExtra("finisher")).send(1, new Bundle());
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.i(TAG,"An error occurred "+e.getMessage());
                                    progressBar.setVisibility(View.GONE);
                                    showMessage("Oops","An error occurred "+e.getMessage());

                                    et_name.setEnabled(true);
                                    et_price.setEnabled(true);
                                    et_fabric.setEnabled(true);
                                    et_saree_length.setEnabled(true);
                                    et_packaging.setEnabled(true);
                                    et_pieces.setEnabled(true);
                                    et_price.setEnabled(true);
                                    et_home_text.setEnabled(true);
                                    btn_add_catalog.setEnabled(true);

                                }
                            });

                        }

                    }
                }else{
                    Log.i(TAG,"An error occurred "+task.getException());
                }
            }
        });


    }

    //check Internet

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


    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(NewCatalogueActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
