package com.replon.www.vsmadmin.Catalogues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.replon.www.vsmadmin.R;
import com.replon.www.vsmadmin.Utility.DefaultTextConfig;

import java.util.ArrayList;

public class ViewImagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CatalogueImageAdapter mAdapter;
    int position;

    String catalogueImageUrl,homeImageUrl;

    ArrayList<String> sareeImageUrlList;
    ArrayList<ContentsCatalogue> catalogueList;

    ImageView home_image,catalogue_image;
    TextView tv_home_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewImagesActivity.this);
        setContentView(R.layout.activity_view_images);

        init();

        catalogueList=getIntent().getParcelableArrayListExtra("catalogueList");
        position=getIntent().getIntExtra("position",0);
        sareeImageUrlList=catalogueList.get(position).getSaree_image_url();

        catalogueImageUrl=catalogueList.get(position).getCatalogue_image_url();

        if(catalogueList.get(position).isHome()){
            homeImageUrl=catalogueList.get(position).getHome_image_url();
            Glide.with(getApplicationContext()).load(homeImageUrl).placeholder(R.drawable.ic_place_holder_catalog).into(home_image);
        }else{
            home_image.setVisibility(View.GONE);
            tv_home_image.setVisibility(View.GONE);

        }

        Glide.with(getApplicationContext()).load(catalogueImageUrl).placeholder(R.drawable.ic_place_holder_catalog).into(catalogue_image);

        mAdapter=new CatalogueImageAdapter(this,sareeImageUrlList);
        recyclerView.setAdapter(mAdapter);


    }

    private void init() {
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        tv_home_image=(TextView)findViewById(R.id.tv_home_image);
        home_image=(ImageView)findViewById(R.id.home_image);
        catalogue_image=(ImageView)findViewById(R.id.catalogue_image);

        catalogueList=new ArrayList<>();
        sareeImageUrlList=new ArrayList<>();
    }
}
