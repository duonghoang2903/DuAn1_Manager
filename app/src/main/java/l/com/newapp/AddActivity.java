package l.com.newapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import l.com.newapp.rest.RestClient2;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {
    private ImageView mImgImageAdd;
    private Button mBtnOpenGallery;
    private Button mBtnAdd;
    private Button mBtnClear;
    private Button mBtnCancel;
    private EditText edtName;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initPermission();
        mImgImageAdd = findViewById(R.id.imgImageAdd);
        mBtnOpenGallery = findViewById(R.id.btnOpenGallery);
        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnClear = findViewById(R.id.btnClear);
        mBtnCancel = findViewById(R.id.btnCancel);
        auth  = getIntent().getStringExtra("auth");
        edtName = findViewById(R.id.edtName);
        mBtnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });
        uploadQuest();
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtName.setText("");
                mImgImageAdd.setImageResource(R.drawable.default_image);
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(AddActivity.this, "Permision Read File is Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddActivity.this, "Permision Write File is Denied", Toast.LENGTH_SHORT).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(AddActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(AddActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }
    String auth;
    String path="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                if(cursor.moveToFirst()){
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    path = cursor.getString(columnIndex);
                }
                Log.e("path",path);
                final InputStream imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mImgImageAdd.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(AddActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(AddActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    private void uploadQuest(){
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!path.isEmpty() && !edtName.getText().toString().isEmpty()){
                    showProgess();
                    String name = edtName.getText().toString();
                    File file = new File(path);
                    RequestBody requestFile =
                            RequestBody.create(MediaType.parse(path), file);
                    MultipartBody.Part uploadPart =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    Call<JsonElement> callUploadQuest = RestClient2.getApiInterface().uploadImage(auth, uploadPart, name);
                    callUploadQuest.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            dialog.dismiss();
                            if (response.body()!=null){
                                JsonObject image = response.body().getAsJsonObject();
                                int id = image.get("id").getAsInt();
                                String name = image.get("title").getAsJsonObject().get("rendered").getAsString();
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                                builder.setMessage("Đăng thành công!");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                builder.show();

                            }else{
                                try {
                                    Log.e("responeError", response.errorBody().string());
                                }catch (Exception e){

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable t) {
                            Log.e("error", t.toString());
                        }
                    });
                }
            }
        });
    }
    private void showProgess(){
        dialog= new ProgressDialog(AddActivity.this);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
