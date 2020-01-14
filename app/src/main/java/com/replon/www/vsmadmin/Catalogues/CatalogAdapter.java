package com.replon.www.vsmadmin.Catalogues;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.replon.www.vsmadmin.R;

import java.util.ArrayList;


public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder> {

    private static final String TAG = "CatalogueImageAdapter";
    Activity activity;
    ArrayList<ContentsCatalogue> catalogueList;

    public CatalogAdapter(Activity activity, ArrayList<ContentsCatalogue> catalogueList) {
        this.activity = activity;
        this.catalogueList = catalogueList;

    }


    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(activity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_catalog_info_new,null);
        CatalogViewHolder holder=new CatalogViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(@NonNull CatalogViewHolder holder, final int i) {

        final ContentsCatalogue contents = catalogueList.get(i);

        holder.tv_name.setText(contents.getName());
        holder.tv_name.setTypeface(null, Typeface.BOLD);
        holder.tv_fabric.setText("Fabric: "+contents.getFabric());
//        if(contents.isHome()){
//            holder.tv_home.setText("On HomePage");
//        }else{
//            holder.tv_home.setText("Not On HomePage");
//        }
//
//        holder.tv_home_text.setText(contents.getHome_text());
//        holder.tv_packaging.setText(contents.getPackaging());
//        holder.tv_pieces.setText(contents.getPieces());
//        holder.tv_price.setText(contents.getPrice());
//        holder.tv_saree_length.setText(contents.getSaree_length());
        holder.tv_date.setText(contents.getDate_created());
        holder.tv_doc_id.setText(contents.getDocument_id());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity.getApplicationContext(), CatalogDetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("catalogueList", catalogueList);
                intent.putExtras(bundle);
                intent.putExtra("position",i);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });



    }

    public void updateList(ArrayList<ContentsCatalogue> list){
        catalogueList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return catalogueList.size();
    }



    class CatalogViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_fabric,tv_date, tv_doc_id;
        ProgressBar progressBar;


        public  CatalogViewHolder(@NonNull View itemView){
            super(itemView);

            tv_name = (TextView)itemView.findViewById(R.id.tv_name);
            tv_fabric = (TextView)itemView.findViewById(R.id.tv_fabric);
//            tv_home = (TextView)itemView.findViewById(R.id.tv_home);
//            tv_home_text = (TextView)itemView.findViewById(R.id.tv_home_text);
//            tv_packaging = (TextView)itemView.findViewById(R.id.tv_packaging);
//            tv_pieces = (TextView)itemView.findViewById(R.id.tv_pieces);
//            tv_price = (TextView)itemView.findViewById(R.id.tv_prices);
//            tv_saree_length = (TextView)itemView.findViewById(R.id.tv_saree_length);
            tv_date = (TextView)itemView.findViewById(R.id.tv_date_created);
            tv_doc_id = (TextView)itemView.findViewById(R.id.tv_document_id);
            progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);


        }
    }
}
