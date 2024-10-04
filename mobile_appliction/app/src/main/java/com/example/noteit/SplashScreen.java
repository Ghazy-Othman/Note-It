package com.example.noteit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String token = _getToken() ;
            if(token.isEmpty()){
                Intent intent = new Intent(SplashScreen.this , RegisterScreen.class) ;
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(SplashScreen.this , MainActivity.class) ;
                startActivity(intent);
                finish();
            }

        } , 2000) ;

    }


    String _getToken(){
        SharedPreferences preferences = getSharedPreferences("Token_shp" , MODE_PRIVATE) ;
        return preferences.getString("token",  "") ;
    }
}