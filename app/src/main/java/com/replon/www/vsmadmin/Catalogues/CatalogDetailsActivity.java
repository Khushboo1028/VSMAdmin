package com.replon.www.vsmadmin.Catalogues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.vsmadmin.R;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CatalogDetailsActivity extends AppCompatActivity {

    public static final String TAG = "CatalogDetailsActivity";
    ImageView back;
    EditText et_name,et_fabric, et_saree_length, et_packaging, et_pieces, et_price, et_home_text;
    Spinner type_spinner, tag_spinner;
    CheckBox checkBox_home_page;

    ArrayList<String> tagList, typeList;
    ArrayList<ContentsCatalogue> catalogueList;

    Button btn_update;
    int position;

    FirebaseFirestore db;
    ProgressBar progressBar;

    Boolean isConnected,monitoringConnectivity;
    String home_text;
    TextView tv_view_images,tv_date;

    String tag,type;

    LinearLayout date_picker_layout;
    int YEAR, MONTH, DATE;
    Calendar currentDate;
    String dayOfTheWeek;
    DecimalFormat formatter;
    String date_format;
    Date newDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), CatalogDetailsActivity.this);

        setContentView(R.layout.activity_catalog_details);

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        tv_view_images.setText("View Images");
        catalogueList=getIntent().getParcelableArrayListExtra("catalogueList");
        position=getIntent().getIntExtra("position",0);

        if(catalogueList!=null){
            et_name.setText(catalogueList.get(position).getName());
            et_fabric.setText(catalogueList.get(position).getFabric());
            et_saree_length.setText(catalogueList.get(position).getSaree_length());
            et_packaging.setText(catalogueList.get(position).getPackaging());
            et_pieces.setText(catalogueList.get(position).getPieces());
            et_price.setText(catalogueList.get(position).getPrice());
            tv_date.setText(catalogueList.get(position).getDate_created());


            if(catalogueList.get(position).isHome()){
                    checkBox_home_page.setChecked(true);
                    et_home_text.setText(catalogueList.get(position).getHome_text());
            }else{
                checkBox_home_page.setChecked(false);
                et_home_text.setVisibility(View.GONE);
                checkBox_home_page.setVisibility(View.GONE);
            }


            typeList.add(getString(R.string.VSM_sarees));
            typeList.add(getString(R.string.VSM_weaves));

            if(catalogueList.get(position).getType()==0){
                type=getString(R.string.VSM_weaves);
            }else if(catalogueList.get(position).getType()==1){
                type=getString(R.string.VSM_sarees);
            }

            if(catalogueList.get(position).getTag() == 0){
               tag=getString(R.string.none);

            }else if(catalogueList.get(position).getTag() == 1){
                tag=getString(R.string.best_seller);

            }else if(catalogueList.get(position).getTag() == 2){
                tag=getString(R.string.featured);

            }else if(catalogueList.get(position).getTag() == 3){
                tag=getString(R.string.new_arrivals);
            }


        }

        tagList.add(getString(R.string.new_arrivals));
        tagList.add(getString(R.string.best_seller));
        tagList.add(getString(R.string.featured));
        tagList.add(getString(R.string.none));



        checkBox_home_page.setEnabled(false);



        btn_update.setText("UPDATE CATALOG");


        ArrayAdapter<String> tagDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagList);
        ArrayAdapter<String> typeDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);


        tagDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tag_spinner.setAdapter(tagDataAdapter);
        type_spinner.setAdapter(typeDataAdapter);


        Log.i(TAG,"tag is "+tag);
        Log.i(TAG,"type is "+type);
        tag_spinner.setSelection(tagDataAdapter.getPosition(tag));
        type_spinner.setSelection(typeDataAdapter.getPosition(type));


        tv_view_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), ViewImagesActivity.class);
                intent.putExtra("position",position);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("catalogueList",catalogueList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        date_format=catalogueList.get(position).getDate_created();
        date_picker_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar currentDate = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(CatalogDetailsActivity.this,R.style.TimePickerTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


//                        date_format = formatter.format(dayOfMonth) + "/" + formatter.format(month+1)  + "/" + year;
                        Calendar calendar= Calendar.getInstance();
                        calendar.set(year,month,dayOfMonth);

                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                        date_format = sfd_viewFormat.format(calendar.getTime());
                        try {
                            newDate = sfd_viewFormat.parse(date_format);
                            Log.i(TAG,"new date is "+newDate);
                        }
                        catch(ParseException pe) {
                            throw new IllegalArgumentException(pe);
                        }

                        tv_date.setText(date_format);
                        DATE = dayOfMonth;
                        YEAR = year;
                        MONTH = month;



                    }
                },YEAR, MONTH, DATE);
                datePickerDialog.show();


            }
        });




        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkConnectivity();

                if(checkBox_home_page.isSelected()){
                    home_text=et_home_text.getText().toString().trim();
                    if(home_text.isEmpty()){
                        showMessage("Oops!","If home is checked, then home text is mandatory");
                    }
                }
                else if(!isConnected){
                    showMessage("Oops!","Please check your internet connections");
                }
                else if(et_name.getText().toString().trim().isEmpty() || et_fabric.getText().toString().trim().isEmpty()
                    ||et_saree_length.getText().toString().trim().isEmpty() || et_packaging.getText().toString().trim().isEmpty()
                        || et_pieces.getText().toString().trim().isEmpty()|| et_price.getText().toString().trim().isEmpty()
                ){
                    showMessage("Uh-Oh","Kindly fill all the fields");
                }else{
                    updateData();
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
        tv_date=(TextView)findViewById(R.id.tv_date);

        btn_update=(Button) findViewById(R.id.btn_update);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        date_picker_layout=(LinearLayout)findViewById(R.id.date_picker_layout);
        date_picker_layout.setVisibility(View.VISIBLE);

        type_spinner = (Spinner) findViewById(R.id.type_spinner);
        tag_spinner = (Spinner) findViewById(R.id.tag_spinner);

        checkBox_home_page = (CheckBox) findViewById(R.id.checkbox_home_page);

        currentDate = Calendar.getInstance();
        DATE = currentDate.get(Calendar.DATE);
        MONTH = currentDate.get(Calendar.MONTH);
        YEAR = currentDate.get(Calendar.YEAR);
        formatter = new DecimalFormat("00");

        tagList = new ArrayList<>();
        typeList = new ArrayList<>();
        catalogueList=new ArrayList<>();

        db=FirebaseFirestore.getInstance();

    }

    private void updateData(){

        if(date_format.equals(catalogueList.get(position).date_created)){

            try {
                SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                newDate = sfd_viewFormat.parse(date_format);
                Log.i(TAG,"New data same as original date "+newDate);
            }
            catch(ParseException pe) {
                throw new IllegalArgumentException(pe);
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        DocumentReference doc_Ref=db.collection(getString(R.string.catalogues)).document(catalogueList.get(position).document_id);

        int type,tag=0;
        if(type_spinner.getSelectedItem().toString().equals(getString(R.string.VSM_weaves))){
             type=0;
        }else{
            type=1;
        }

        if(tag_spinner.getSelectedItem().toString().equals("None")){
            tag=0;
        }else if(tag_spinner.getSelectedItem().toString().equals("Best Seller")){
            tag=1;
        }else if(tag_spinner.getSelectedItem().toString().equals("Featured")){
            tag=2;
        }else if(tag_spinner.getSelectedItem().toString().equals("New Arrival")){
            tag=3;
        }
        Map<String, Object> data = new HashMap<>();
        data.put(getString(R.string.name), et_name.getText().toString().trim());
        data.put(getString(R.string.fabric), et_fabric.getText().toString().trim());
        data.put(getString(R.string.saree_length), et_saree_length.getText().toString().trim());
        data.put(getString(R.string.packaging), et_packaging.getText().toString().trim());
        data.put(getString(R.string.name), et_name.getText().toString().trim());
        data.put(getString(R.string.pieces), et_pieces.getText().toString().trim());
        data.put(getString(R.string.price), et_price.getText().toString().trim());
        data.put(getString(R.string.type), type);
        data.put(getString(R.string.tag), tag);
        data.put(getString(R.string.home), checkBox_home_page.isChecked());
        data.put(getString(R.string.home_text),et_home_text.getText().toString().trim());
        data.put(getString(R.string.date_created),newDate);


        doc_Ref.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG,"An error occurred in updating catalog"+e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
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
    public void showMessage(String title, String message){
        final AlertDialog.Builder builder=new AlertDialog.Builder(CatalogDetailsActivity.this,R.style.AlertDialogCustom);
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
}
