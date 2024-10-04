package com.example.noteit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginScreen extends AppCompatActivity {

    EditText email , password ;
    Button login ;
    AlertDialog dialogLoading ;
    boolean state = false ;
    String token ;
    //
    String msg = "Login successfully";
    int res_code = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        email = findViewById(R.id.login_email) ;
        password = findViewById(R.id.login_password) ;
        login = findViewById(R.id.login_btn) ;

        login.setOnClickListener(view -> {
            showLoadingDialog("Logging in ....");
            _userLogin();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                dialogLoading.cancel();
                if(state){
                    Intent intent = new Intent(LoginScreen.this , MainActivity.class) ;
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(LoginScreen.this , msg , Toast.LENGTH_LONG).show();
                }
            } , 5000) ;
        });

    }


    private void _userLogin()   {

        String em = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        OkHttpClient client = new OkHttpClient() ;
        RequestBody body = new FormBody.Builder()
                .add("email" , em)
                .add("password" , pass)
                .build();
        Request request = new Request.Builder()
                .header("Accept","application/json")
                .post(body).url(Constant.userLogin).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                msg = e.getMessage() ;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try{
                    String res = response.body().string();
                    int code = response.code();
                    res_code = code ;
                    msg = res ;
                    if(res!= null){
                        JSONObject obj  = new JSONObject(res) ;
                        String success = obj.getString("success") ;
                        if(success.equals("true")){
                            state = true ;
                            token = obj.getString("token");
                            _saveTokenLocal();
                            msg = "Logging successfully" ;
                        }
                    }
                    else {
                        msg = "Server wrong" ;
                    }
                }catch (IOException | JSONException error) {
                    state = false ;
                    msg = error.getMessage() ;
                }
            }
        });
    }


    private  void _saveTokenLocal(){
        SharedPreferences preferences = getSharedPreferences("Token_shp" , MODE_PRIVATE) ;
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString("token" , token) ;
        editor.apply() ;
    }


    private void showLoadingDialog(String message)  {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText(message);
        tvText.setTextSize(15);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        dialogLoading = builder.create();
        dialogLoading.show();
        Window window = dialogLoading.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialogLoading.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialogLoading.getWindow().setAttributes(layoutParams);
        }
    }

}