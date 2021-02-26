package com.realmdata.treeleafdatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realmdata.treeleafdatabase.adapter.MasterListAdapter;
import com.realmdata.treeleafdatabase.databinding.ActivityMainBinding;
import com.realmdata.treeleafdatabase.realmPackage.UserDetail;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class
MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    List<UserDetail> userDetails = new ArrayList<>();
    MasterListAdapter masterListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarTop.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbarTop.toolbarTitle.setText("Master List");



        binding.addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddRecordsActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra("title","Add Record"));
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                masterListAdapter.removeDataAtPostition(viewHolder.getLayoutPosition());
                showSnackBar("Deleted Succesfully");
                fetchDataAndReload();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerMasterList);

        binding.recyclerMasterList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        masterListAdapter = new MasterListAdapter(getApplicationContext());

        fetchDataAndReload();

    }

    private void fetchDataAndReload() {
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        RealmQuery query = realm.where(UserDetail.class);
        RealmResults<UserDetail> realmResults = query.findAll();


        userDetails = new Gson().fromJson(realmResults.asJSON(),new TypeToken<List<UserDetail>>(){}.getType());
        if (userDetails.size()==0){
            binding.noData.setVisibility(View.VISIBLE);
        }
        else {
            binding.noData.setVisibility(View.GONE);
            masterListAdapter.updateData(userDetails);
            binding.recyclerMasterList.setAdapter(masterListAdapter);
        }

    }

    void showSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content).getRootView(),message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDataAndReload();
    }


}