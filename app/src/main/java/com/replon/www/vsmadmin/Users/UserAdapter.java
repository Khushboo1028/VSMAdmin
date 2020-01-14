package com.replon.www.vsmadmin.Users;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.vsmadmin.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    public static final String TAG = "UserAdapter";
    private ArrayList<ContentsUsers> usersList;
    private FirebaseFirestore db;
    private Activity activity;


    public UserAdapter(Activity activity, ArrayList<ContentsUsers> usersList) {
        this.activity = activity;
        this.usersList = usersList;
    }



    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(activity.getApplicationContext());
        View view=layoutInflater.inflate(R.layout.row_users_info,null);
        UserViewHolder holder=new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.UserViewHolder holder, final int i) {

        final ContentsUsers contents = usersList.get(i);

        holder.tv_phone.setText(contents.getPhone());
        holder.tv_name.setText(contents.getName());
        holder.tv_gst_no.setText(contents.getGst_no());
        holder.tv_company_name.setText(contents.getCompany_name());
        holder.tv_city.setText(contents.getCity());
        holder.tv_date.setText(contents.getDate_created());
        holder.tv_doc_id.setText(contents.getDocument_id());
        holder.tv_state.setText(contents.getState());

        if(contents.getCurrent_status()){
            holder.tv_status.setText("Approved");
            holder.btn_approve.setText("Reject");
            holder.btn_approve.setBackground(activity.getApplicationContext().getDrawable(R.drawable.red_rounded_corners));
        }else{
            holder.tv_status.setText("Unapproved");
            holder.btn_approve.setText("Approve");
            holder.btn_approve.setBackground(activity.getApplicationContext().getDrawable(R.drawable.green_rounded_corners));
        }

        holder.progressBar.setVisibility(View.GONE);

        holder.btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contents.getCurrent_status()){

                    confirmStatusUpdate("","Are you sure you want to change the status?",contents.getDocument_id(),false,holder.btn_approve,holder.progressBar,i);

                }else{
                    confirmStatusUpdate("","Are you sure you want to change the status?",contents.getDocument_id(),true,holder.btn_approve,holder.progressBar,i);
                }

            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity.getApplicationContext(), ViewUserActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("usersList", usersList);
                intent.putExtras(bundle);
                intent.putExtra("position",i);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);

            }
        });

    }

    public void updateList(ArrayList<ContentsUsers> list){
        usersList = list;
        notifyDataSetChanged();
    }
    private void updateData(String document_id, final Boolean dealer_status, final Button button, final ProgressBar progressBar, final int position){
        db=FirebaseFirestore.getInstance();
        DocumentReference doc_ref=db.collection(activity.getApplicationContext().getString(R.string.users)).document(document_id);

        progressBar.setVisibility(View.VISIBLE);

        doc_ref.update(activity.getApplicationContext().getString(R.string.dealer),dealer_status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG,"Dealer status updated to "+dealer_status  );
                        progressBar.setVisibility(View.GONE);

                        if(dealer_status){
                            button.setText("Reject");
                            button.setBackground(activity.getApplicationContext().getDrawable(R.drawable.red_rounded_corners));

                            Log.i(TAG,"Button changed to reject");
                        }else{

                            button.setText("Approve");
                            button.setBackground(activity.getApplicationContext().getDrawable(R.drawable.green_rounded_corners));
                            Log.i(TAG,"Button changed to reject");
                        }

                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,"An error occurred in updating dealer status" +e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });


    }


    public void confirmStatusUpdate(String title, String message, final String document_id, final boolean status,final Button btn,final ProgressBar progressBar,final int pos){

        final AlertDialog.Builder builder=new AlertDialog.Builder(activity,R.style.AlertDialogCustom);
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateData(document_id,status,btn,progressBar,pos);


            }
        });

        builder.setNegativeButton("No", null);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }



    @Override
    public int getItemCount() {
        return usersList.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder{

        Button btn_approve;
        TextView tv_phone, tv_name, tv_gst_no, tv_company_name, tv_city,tv_state, tv_status, tv_date, tv_doc_id;
        ProgressBar progressBar;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);

            btn_approve = (Button)itemView.findViewById(R.id.btn_approve);

            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_gst_no = (TextView) itemView.findViewById(R.id.tv_gst_no);
            tv_company_name = (TextView) itemView.findViewById(R.id.tv_company_name);
            tv_city = (TextView) itemView.findViewById(R.id.tv_city);
            tv_state=(TextView) itemView.findViewById(R.id.tv_state);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date_created);
            tv_doc_id = (TextView) itemView.findViewById(R.id.tv_document_id);

            progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);


        }

    }
}
