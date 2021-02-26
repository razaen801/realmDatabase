package com.realmdata.treeleafdatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Filter;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.realmdata.treeleafdatabase.adapter.Patterns;
import com.realmdata.treeleafdatabase.databinding.ActivityAddRecordsBinding;
import com.realmdata.treeleafdatabase.realmPackage.UserDetail;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import id.zelory.compressor.Compressor;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import static java.security.AccessController.getContext;

public class AddRecordsActivity extends AppCompatActivity {

    ActivityAddRecordsBinding binding;
    boolean isUpdateData = false;
    UserDetail userDetail;
    String gender = "";
    Realm realm;
    Bitmap thumb_bitmap = null;
    private Uri mImageUri;
    byte[] byteArray;
    String fname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecordsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        setSupportActionBar(binding.toolbarTop.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.cbUseUnderline.setPaintFlags(binding.cbUseUnderline.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        binding.cbUseStrikeThrough.setPaintFlags(binding.cbUseStrikeThrough.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        binding.toolbarTop.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestPermissionHandler().requestPermission(AddRecordsActivity.this, new String[]{
                        Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                }, 123, new RequestPermissionHandler.RequestPermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess() {
                        selectImage();

                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(getApplicationContext(), "request permission failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        if (getIntent().getStringExtra("title") != null) {
            binding.toolbarTop.toolbarTitle.setText(getIntent().getStringExtra("title"));
        }

        if (getIntent().getStringExtra("updateDatas") != null) {
            isUpdateData = true;
            binding.btnProceed.setVisibility(View.GONE);
            binding.btnProceed.setText("Update Data");
            userDetail = new Gson().fromJson(getIntent().getStringExtra("updateDatas"), UserDetail.class);
            if (userDetail.getGender().equalsIgnoreCase("male")){
                binding.rbMale.setChecked(true);
            }
            else binding.rbFemale.setChecked(true);
            binding.etFullName.setText(userDetail.getAddress());
            binding.etEmail.setText(userDetail.getEmail());
            binding.etAddress.setText(userDetail.getAddress());
            binding.etDescription.setText(userDetail.getDesc());

            Bitmap bitmap = BitmapFactory.decodeFile(Constant.ImagePath+"/saved/"+userDetail.getImageLocation());
            if (bitmap!=null) {
                Glide.with(this)
                        .load(bitmap)
                        .fitCenter()
                        .placeholder(R.drawable.ic_user)
                        .into(binding.imageProfile);
            }

        } else {
            isUpdateData = false;
            binding.btnProceed.setText("Submit Form");
        }

        binding.rbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Male";
            }
        });
        binding.rbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "Female";
            }
        });


        binding.btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {

                    String desc = binding.etDescription.getText().toString();
                    String finaldesc = desc;
                    if (binding.cbUseBold.isChecked()){
                        finaldesc+="b";
                    }
                    if (binding.cbUseItalic.isChecked()){
                        finaldesc+="i";
                    }
                    if (binding.cbUseUnderline.isChecked()){
                        finaldesc+="u";

                    }
                    if (binding.cbUseStrikeThrough.isChecked()){
                        finaldesc+="s";
                    }
//                    finaldesc = "<![CDATA[  " + finaldesc+" ]]>";
                    
                    if (isUpdateData) {
                        updateData(binding.etFullName.getText().toString(), binding.etEmail.getText().toString(),
                                binding.etAddress.getText().toString(), binding.etDescription.getText().toString(),finaldesc);
                    } else {
                        addData(binding.etFullName.getText().toString(), binding.etEmail.getText().toString(),
                                binding.etAddress.getText().toString(), binding.etDescription.getText().toString(),finaldesc);
                    }

                }
            }
        });


        binding.etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeAllWarnings();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeAllWarnings();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeAllWarnings();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        binding.etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeAllWarnings();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                binding.btnProceed.setVisibility(View.VISIBLE);
            }
        });
    }

    void showSnackBar(String message){
        Snackbar.make(findViewById(android.R.id.content).getRootView(),message,Snackbar.LENGTH_LONG).show();
    }
    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    private void SaveImage() {

        File myDir = new File(Constant.ImagePath + "/saved");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        fname = n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                thumb_bitmap = null;
                Uri resultUri = result.getUri();
                this.mImageUri = resultUri;
                File thumb_filePath = new File(resultUri.getPath());
                try {
                    thumb_bitmap = new Compressor(getApplicationContext())
                            .setMaxWidth(300)
                            .setMaxHeight(300)
                            .setQuality(85)
                            .compressToBitmap(thumb_filePath);
                    Glide.with(this)
                            .load(thumb_filePath)
                            .fitCenter()
                            .placeholder(R.drawable.ic_user)
                            .into(binding.imageProfile);
                    SaveImage();
//                    saveImageAsByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                //Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error in choosing image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveImageAsByteArray() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byteArray = stream.toByteArray();
    }

    private void updateData(String fullname, String email, String address, String desc, String finalDesc) {
        RealmResults<UserDetail> realmResults = realm.where(UserDetail.class).equalTo("id", userDetail.getId()).findAll();
        realm.beginTransaction();
        for (UserDetail userDetail : realmResults) {
            userDetail.setFullName(fullname);
            userDetail.setEmail(email);
            userDetail.setAddress(address);
            userDetail.setImageLocation(fname);
            userDetail.setDesc(desc);
            userDetail.setCodedDescription(finalDesc);
            userDetail.setImageLocation("");
            userDetail.setGender(gender);
        }
        realm.commitTransaction();
        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
        finish();
    }

    private void addData(String fullname, String email, String address, String desc, String finalDesc) {

        realm.beginTransaction();
        int n = 10000;
        Random generator = new Random();
        n = generator.nextInt(n);
        UserDetail userDetails = realm.createObject(UserDetail.class, n);
        userDetails.setFullName(fullname);
        userDetails.setGender(gender);
        userDetails.setAddress(address);
        userDetails.setEmail(email);
        userDetails.setImageLocation(fname);
        userDetails.setDesc(desc);
        userDetails.setCodedDescription(finalDesc);
        realm.commitTransaction();
        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();
        finish();
    }


    private void removeAllWarnings() {
        binding.btnProceed.setVisibility(View.VISIBLE);
        binding.tlFullName.setError(null);
        binding.tlAddress.setError(null);
        binding.tlEmail.setError(null);
        binding.tlDescription.setError(null);
    }

    private boolean isValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(binding.etFullName.getText().toString())) {
            isValid = false;
            binding.tlFullName.setError("Please enter full name");
        }
        if (TextUtils.isEmpty(binding.etEmail.getText().toString())) {
            isValid = false;
            binding.tlEmail.setError("Please enter email address");
        }
        else {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()){
                isValid = false;
                binding.tlEmail.setError("Invalid Email");
            }
        }
        if (TextUtils.isEmpty(binding.etAddress.getText().toString())) {
            isValid = false;
            binding.tlAddress.setError("Please enter address");
        }
        if (TextUtils.isEmpty(binding.etDescription.getText().toString())) {
            isValid = false;
            binding.tlDescription.setError("Please enter Description");
        }
        if (binding.rgGender.getCheckedRadioButtonId() == -1) {
            isValid = false;
            showSnackBar("Select Gender");
        }
        else if (thumb_bitmap==null){
            isValid=false;
            showSnackBar("Please add profile picture");
        }
        return isValid;
    }
}