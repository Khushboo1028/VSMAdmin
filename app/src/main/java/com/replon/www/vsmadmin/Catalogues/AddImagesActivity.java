package com.replon.www.vsmadmin.Catalogues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.replon.www.vsmadmin.R;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;
import com.replon.www.vsmadmin.Utility.UploadsAdapter;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class AddImagesActivity extends AppCompatActivity {


    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 69;
    public static final int RESULT_LOAD_IMAGE_MULTIPLE = 18;
    public static final int CATALOG_IMAGE = 23;
    public static final int REQUEST_DATA = 20;
    public static final int HOME_IMAGE = 12;

    public static final String TAG = "AddImagesActivity";
    public static final int PDF_REQUEST_CODE = 14;
    ImageView home_image,catalogue_image,add_image;
    TextView tv_home_image;
    Boolean isHome;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    ArrayList<Uri> saree_image_uri_list;
    FirebaseStorage storage;
    StorageReference storageReference;

    Uri homeImageUri,catalogImageUri,pdfUri;
    ArrayList<String>sareeImageUriString;

    ImageView save,back,pdf_image;

    RecyclerView recyclerView;
    UploadsAdapter uploadsAdapter;
    CheckBox checkBox_home_page;

    Button btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AddImagesActivity.this);
        setContentView(R.layout.activity_add_images);

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {



                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddImagesActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == true){

                        showMessage("Uh-Oh", "Seems like you denied permission to access library! Go on app settings to grant permission");



                    }else {
                        ActivityCompat.requestPermissions(AddImagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                    }



                }else{
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE_MULTIPLE);
                }
            }
        });


        checkBox_home_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkBox_home_page.isChecked()){
                    home_image.setVisibility(View.GONE);
                    tv_home_image.setVisibility(View.GONE);
                } else if(checkBox_home_page.isChecked()){

                    home_image.setVisibility(View.VISIBLE);
                    tv_home_image.setVisibility(View.VISIBLE);
                }
            }
        });
        home_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddImagesActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == true){



                    }else {
                        ActivityCompat.requestPermissions(AddImagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                    }



                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, HOME_IMAGE);
                }
            }
        });

        catalogue_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddImagesActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == true){



                    }else {
                        ActivityCompat.requestPermissions(AddImagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                    }



                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, CATALOG_IMAGE);
                }

            }
        });

        pdf_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddImagesActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == true){



                    }else {
                        ActivityCompat.requestPermissions(AddImagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                    }



                }else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, PDF_REQUEST_CODE);
                }

            }
        });

        save.setVisibility(View.GONE);


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox_home_page.isChecked()){

                    if(homeImageUri==null ||catalogImageUri==null|| saree_image_uri_list ==null || saree_image_uri_list.size()==0 || pdfUri==null){
                        showMessage("Oops!","Please enter all data properly");
                    }else {

                        Log.i(TAG,"homeImageUriString" +homeImageUri.toString());

                        Intent intent =new Intent(getApplicationContext(), NewCatalogueActivity.class);
                        intent.putExtra("homeImageUriString",homeImageUri.toString());
                        intent.putExtra("catalogImageUriString",catalogImageUri.toString());
                        intent.putExtra("pdfUriString",pdfUri.toString());
                        intent.putExtra("isHome",true);

                        Bundle bundle=new Bundle();
                        bundle.putParcelableArrayList("sareeImageUriList",saree_image_uri_list);
                        intent.putExtras(bundle);
                        intent.putExtra("finisher", new ResultReceiver(null){
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                finish();
                            }
                        });

                        startActivityForResult(intent,1);

                    }

                }else{
                    if(catalogImageUri==null|| saree_image_uri_list ==null || saree_image_uri_list.size()==0 || pdfUri==null){

                        showMessage("Oops!","Please enter all data properly");
                    }else {



                        Intent intent =new Intent(getApplicationContext(), NewCatalogueActivity.class);

                        intent.putExtra("catalogImageUriString",catalogImageUri.toString());
                        intent.putExtra("pdfUriString",pdfUri.toString());
                        intent.putExtra("isHome",false);

                        Bundle bundle=new Bundle();
                        bundle.putParcelableArrayList("sareeImageUriList",saree_image_uri_list);
                        intent.putExtras(bundle);
                        intent.putExtra("finisher", new ResultReceiver(null){
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                finish();
                            }
                        });
                        startActivityForResult(intent,1);

                    }
                }
            }
        });



    }

    private void init(){
        home_image=(ImageView)findViewById(R.id.home_image);
        catalogue_image=(ImageView)findViewById(R.id.catalogue_image);
        tv_home_image=(TextView)findViewById(R.id.tv_home_image);
        add_image=(ImageView)findViewById(R.id.add_photo);
        save=(ImageView)findViewById(R.id.save);
        back=(ImageView)findViewById(R.id.back);
        pdf_image=(ImageView)findViewById(R.id.pdf_image);
        btn_next=(Button)findViewById(R.id.btn_next);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        checkBox_home_page = (CheckBox) findViewById(R.id.checkbox_home_page);


        saree_image_uri_list =new ArrayList<>();
        sareeImageUriString=new ArrayList<>();

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));//by default manager is vertical
        uploadsAdapter=new UploadsAdapter(this, saree_image_uri_list);
        recyclerView.setAdapter(uploadsAdapter);




    }






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE:
                if (grantResults!=null && grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // check whether storage permission granted or not.
                    if (grantResults!=null && grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE_MULTIPLE);
                    }
                }
                break;




            default:
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                showMessage("Uh-Oh", "Please grant permission to access library");

            } else {


                if(data.getData()!=null) {

                    Uri mImageUri = data.getData();
                    saree_image_uri_list.add(mImageUri);
                    uploadsAdapter.notifyDataSetChanged();

                }else {
                    try {

                        if (data.getClipData() != null) {

                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                saree_image_uri_list.add(uri);
                                uploadsAdapter.notifyDataSetChanged();

                            }
                            Log.v("LOG_TAG", "Selected Images size is " + saree_image_uri_list.size());

                        }


                    } catch (Exception e) {
                        Toast.makeText(this, "Something went wrong" + e.getMessage(), Toast.LENGTH_LONG)
                                .show();
                        Log.i(TAG, "Error is " + e.getMessage());
                    }
                }
            }


        }else  if (requestCode == HOME_IMAGE && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                showMessage("Uh-Oh", "Please grant permission to access library");

            } else {

                if(data.getData()!=null) {
                    homeImageUri= data.getData();
                    home_image.setImageURI(homeImageUri);

                }
            }


        }else  if (requestCode == CATALOG_IMAGE && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                showMessage("Uh-Oh", "Please grant permission to access library");

            } else {

                if(data.getData()!=null) {
                    catalogImageUri= data.getData();
                    catalogue_image.setImageURI(catalogImageUri);

                }
            }


        }else  if (requestCode == PDF_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);

                showMessage("Uh-Oh", "Please grant permission to access library");

            } else {

                if(data.getData()!=null) {
                    pdfUri= data.getData();
                    pdf_image.setImageDrawable(getDrawable(R.drawable.ic_pdf));

                }
            }


        }

        if (requestCode == REQUEST_DATA && resultCode == Activity.RESULT_OK) {
            boolean isFinish = data.getBooleanExtra("finish", false);
            Log.i(TAG,"in data from intent where finish is "+isFinish);

            if(isFinish){
                finish();
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(AddImagesActivity.this,R.style.AlertDialogCustom);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }
}
