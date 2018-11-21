package l.com.newapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import l.com.newapp.rest.RestClient;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText mEdtUsername;
    private EditText mEdtPassword;
    private Button mBtnLogin;
    String user, pass;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEdtUsername = findViewById(R.id.edtUsername);
        mEdtPassword = findViewById(R.id.edtPassword);
        mBtnLogin = findViewById(R.id.btnLogin);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = mEdtUsername.getText().toString();
                pass =mEdtPassword.getText().toString();
               if (!user.isEmpty()&&!pass.isEmpty()){
                   showProgess();
                   Call<JsonElement> callLogin = RestClient.getApiInterface().callLogin(user,pass);
                   callLogin.enqueue(new Callback<JsonElement>() {
                       @Override
                       public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                           dialog.dismiss();

                           if (response.isSuccessful()) {
                               JsonElement jsonElement2 = response.body();
                               JsonObject login = jsonElement2.getAsJsonObject();
                               String status = login.get("status").getAsString();

                               if (status.equalsIgnoreCase("ok")) {
                                   String auth = Credentials.basic(user, pass);
                                   Intent intent = new Intent(LoginActivity.this, ViewActivity.class);
                                   intent.putExtra("auth",auth);
                                   startActivity(intent);
                               } else {
                                   String error = login.get("error").getAsString();
                                   if (error.equalsIgnoreCase("Invalid username and/or password.")) {
                                       final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                       builder.setTitle("Sai tên tài khoản hoặc mật khẩu");
                                       builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               dialogInterface.dismiss();
                                           }
                                       });
                                       builder.show();

                                   }
                               }
                           }
                       }

                       @Override
                       public void onFailure(Call<JsonElement> call, Throwable t) {
                           Toast.makeText(LoginActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                       }
                   });
               }
            }
        });
    }
    private void showProgess(){
        dialog= new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Loading...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
