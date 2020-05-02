package com.example.myownwhatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private EditText edtUpdateProfileName;
    private Button btnUpdateName,btnUpdateProfilePicture,btnSelectImage;
    private ImageView imageProfile;
    private String profileName;
    private  String changedName ="";
    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtUpdateProfileName = findViewById(R.id.edtUpdateProperName);
        imageProfile = findViewById(R.id.profileImageView);
        btnSelectImage = findViewById(R.id.btncCickToSelectImage);
        btnSelectImage.setOnClickListener(this);
        btnUpdateName = findViewById(R.id.btnUpdateProfileName);
        btnUpdateName.setOnClickListener(this);
        btnUpdateProfilePicture = findViewById(R.id.btnUpdateProfilePicture);
        btnUpdateProfilePicture.setOnClickListener(this);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {

                profileName = object.get("name").toString();
                edtUpdateProfileName.setText(profileName);
                ParseFile image = (ParseFile) object.get("picture");
                image.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (data != null && e == null) {
                            Bitmap profileImg = BitmapFactory.decodeByteArray
                                    (data, 0, data.length);
                            imageProfile.setImageBitmap(profileImg);
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateProfileName:
                if (edtUpdateProfileName.getText().toString().equals("")) {
                    Toast.makeText(this, "Empty Name,can't be assigned!",
                            Toast.LENGTH_SHORT).show();
                    edtUpdateProfileName.setText(profileName);
                } else if (edtUpdateProfileName.getText().toString().equals(profileName)) {
                    Toast.makeText(this, "This name is already assigned!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    changedName = edtUpdateProfileName.getText().toString();
                    profileName = changedName;
                    //so that the earlier conition check can be updated,
                    // as it was comparing to the previous name.

                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("name", changedName);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(Profile.this, "Name Changed!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                break;

            case R.id.btncCickToSelectImage:
                if (Build.VERSION.SDK_INT >= 23 &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.
                            READ_EXTERNAL_STORAGE}, 1000);
                } else {
                    selectImage();
                }
                break;

            case R.id.btnUpdateProfilePicture:
                if (imageBitmap == null) {
                    Toast.makeText(this, "Please Select a different image", Toast.LENGTH_SHORT).show();
                }
                else {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] imgBytes = stream.toByteArray();
                    ParseFile img = new ParseFile("dp.png",imgBytes);



                    ParseObject user = ParseUser.getCurrentUser();
                    user.put("picture", img);

                    final ProgressDialog dialog = new ProgressDialog(this
                    );
                    dialog.setMessage("Uploading Profile Picture.....");
                    dialog.show();

                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(Profile.this, "Image Updated", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Profile.this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }

                    });

                    break;

                }
        }
    }

    public void selectImage(){
        Intent intentToSelectImage =new Intent(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       startActivityForResult(intentToSelectImage,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000){
            if(resultCode==RESULT_OK && data!=null ){
                Uri imageUri = data.getData();
                String[] pathToGet = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(imageUri,pathToGet,
                        null,null,null);
                cursor.moveToFirst();
                int colomnIndexToFetch = cursor.getColumnIndex(pathToGet[0]);
                String imagePth = cursor.getString(colomnIndexToFetch);
                cursor.close();

                imageBitmap = BitmapFactory.decodeFile(imagePth);
                imageProfile.setImageBitmap(imageBitmap);

            }

            else{
                Toast.makeText(this,"Unable to process,Try later" +
                                "..", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
    @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);

        if(requestCode==1000){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                AlertDialog.Builder dialog = new
                        AlertDialog.Builder(this);
                dialog.setTitle("PERMISSION DENIED");
                dialog.setMessage("Unable to choose Image"
                +"\n" + "Without permission to read storage");
                dialog.setCancelable(false);
                dialog.setNegativeButton("OKAY",
                        new DialogInterface.OnClickListener() {
                            @Override
        public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            }
        }
    }


}
