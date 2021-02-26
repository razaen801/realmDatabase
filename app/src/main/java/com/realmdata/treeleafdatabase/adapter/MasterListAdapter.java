package com.realmdata.treeleafdatabase.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.realmdata.treeleafdatabase.AddRecordsActivity;
import com.realmdata.treeleafdatabase.Constant;
import com.realmdata.treeleafdatabase.R;
import com.realmdata.treeleafdatabase.realmPackage.UserDetail;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;

public class MasterListAdapter extends RecyclerView.Adapter<MasterListAdapter.MyViewHolder> {
    Context context;
    List<UserDetail> userDetails = new ArrayList<>();
    public MasterListAdapter(Context context){
        this.context = context;
    }

    public void updateData(List<UserDetail> userDetails){
        this.userDetails = userDetails;
        notifyDataSetChanged();
    }

    public void removeDataAtPostition(int position){
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserDetail> realmResults = realm.where(UserDetail.class).findAll();
        realm.beginTransaction();
        UserDetail userDetail = realmResults.get(position);
        userDetail.deleteFromRealm();
        realm.commitTransaction();

        userDetails.remove(position);
        notifyItemRemoved(position);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_list_data,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(String.format("%s, %s",userDetails.get(position).getFullName(),userDetails.get(position).getGender()));
        holder.address.setText(userDetails.get(position).getAddress());
        holder.email.setText(userDetails.get(position).getEmail());
//        holder.dessc.setText(Html.fromHtml(userDetails.get(position).getCodedDescription()).toString());
        String desccode = userDetails.get(position).getCodedDescription();
        if (desccode.length()!=0){
            if (desccode.contains("b")){
                holder.dessc.setTypeface(holder.dessc.getTypeface(), Typeface.BOLD);
            }
            if (desccode.contains("i")){
                holder.dessc.setTypeface(holder.dessc.getTypeface(), Typeface.ITALIC);
            }
            if (desccode.contains("u")){
                holder.dessc.setPaintFlags(holder.dessc.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            }
            if (desccode.contains("s")){
                holder.dessc.setPaintFlags(holder.dessc.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
        holder.dessc.setText(Html.fromHtml(userDetails.get(position).getCodedDescription()).toString());

        Bitmap bitmap = BitmapFactory.decodeFile(Constant.ImagePath+"/saved/"+userDetails.get(position).getImageLocation());
        if (bitmap!=null) {
            Glide.with(context)
                    .load(bitmap)
                    .fitCenter()
                    .placeholder(R.drawable.ic_user)
                    .into(holder.profile);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddRecordsActivity.class);
                intent.putExtra("title","Update");
                intent.putExtra("updateDatas",new Gson().toJson(userDetails.get(position)));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, address, dessc;
        CircleImageView profile;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            email = itemView.findViewById(R.id.tvEmail);
            address = itemView.findViewById(R.id.tvAddress);
            dessc = itemView.findViewById(R.id.tvDesc);
            profile = itemView.findViewById(R.id.iv_profile);
        }
    }
}
