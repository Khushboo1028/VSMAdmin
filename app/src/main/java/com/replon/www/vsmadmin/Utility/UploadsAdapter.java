package com.replon.www.vsmadmin.Utility;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.replon.www.vsmadmin.R;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class UploadsAdapter extends RecyclerView.Adapter<UploadsAdapter.UploadsViewHolder>{


    private Activity activity;
    ArrayList<Uri> imageURIlist;



    public UploadsAdapter(Activity activity,ArrayList<Uri> imageURIlist) {
        this.activity = activity;
        this.imageURIlist=imageURIlist;
    }


    @NonNull
    @Override
    public UploadsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater= LayoutInflater.from(activity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_image_layout,null);
        UploadsViewHolder holder=new UploadsViewHolder(view);
        return holder;
    }



    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull UploadsViewHolder holder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        //So now we bind the data using the help of this contentsComments object we created

        //if image would have been present then
       //holder.images.setImageDrawable(mContext.getResources().getDrawable(uploadsAttachmentContent.getImage(),null));




        holder.images.setImageURI(imageURIlist.get(i));


        holder.images.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddDeleteDialog(i,imageURIlist);
                return true;
            }
        });


        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), ViewImageActivity.class);
                intent.putExtra("imageUri",imageURIlist.get(i).toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                activity.getApplicationContext().startActivity(intent);

            }
        });



    }





    public void showAddDeleteDialog(final int pos,final ArrayList<Uri> imageURIlist){


        AlertDialog.Builder dialog = new AlertDialog.Builder(activity,R.style.AlertDialogCustom);
        dialog.setCancelable(false);
        dialog.setTitle("Are you sure you want to remove attachment?");
        // dialog.setMessage("Are you sure you want to delete this entry?" );
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Add".
                imageURIlist.remove(pos);


                notifyDataSetChanged();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return imageURIlist.size();
    }

    public class UploadsViewHolder extends RecyclerView.ViewHolder {


       private ImageView images;


        public UploadsViewHolder(@NonNull View itemView) {
            super(itemView);

            images=(ImageView)itemView.findViewById(R.id.images);




        }
    }






}

