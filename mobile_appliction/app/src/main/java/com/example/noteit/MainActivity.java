package com.example.noteit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNewNote  , refresh;

    boolean state = false ;

    String msg ;
    int res_code ;

    String[] titles ;

    ListView  list ;

    AlertDialog dialogLoading ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewNote = findViewById(R.id.add_note_btn) ;
        refresh = findViewById(R.id.refresh_btn) ;
        list = findViewById(R.id.listview) ;


        showLoadingDialog("Getting notes ....");
        _getALlNotes();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            dialogLoading.cancel();
            if(state){
                ArrayAdapter arrayAdapter =new ArrayAdapter(this, android.R.layout.select_dialog_item,titles);
                list.setAdapter(arrayAdapter);
            }
            else {
                Toast.makeText(MainActivity.this, "Server " , Toast.LENGTH_LONG).show();
            }
        } , 5000);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(MainActivity.this,NoteScreen.class);
                String itemData = (String) parent.getItemAtPosition(position);
                intent.putExtra("id" , position) ;
                startActivity(intent);
            }
        });

        ImageView backicon =findViewById(R.id.backIcon);
        TextView title = findViewById(R.id.app_name);

        backicon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });


        // Refresh
        refresh.setOnClickListener(view -> {
            showLoadingDialog("Getting notes ....");
            _getALlNotes();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                dialogLoading.cancel();
                if(state){
                    ArrayAdapter arrayAdapter =new ArrayAdapter(this, android.R.layout.select_dialog_item,titles);
                    list.setAdapter(arrayAdapter);
                }
                else {
                    Toast.makeText(MainActivity.this, "Server " , Toast.LENGTH_LONG).show();
                }
            } , 5000);
        });

        // Go to add note screen
        addNewNote.setOnClickListener(view -> {
            Intent goToAddNoteScreen = new Intent(MainActivity.this , AddNoteScreen.class) ;
            startActivity(goToAddNoteScreen);
        });

    }


    private void _getALlNotes(){
        OkHttpClient client = new OkHttpClient() ;
        Request request = new Request.Builder()
                .addHeader("Authorization" , "Bearer " + _getToken())
                .header("Accept","application/json")
                .url(Constant.getUserNotes).build();

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
                    if(res!= null){
                        JSONObject obj  = new JSONObject(res) ;
                        String success = obj.getString("success") ;
                        if(success.equals("true")){
                            state = true ;
                            msg = "Successed" ;
                            Constant.user_notes.clear();
                            JSONArray notes = obj.getJSONArray("notes") ;
                            int n = notes.length() ;
                            titles = new String[n] ;
                            for(int i = 0 ; i < n ; i++){
                                JSONObject note = notes.getJSONObject(i) ;
                                int id = note.getInt("id");
                                String title = note.getString("title");
                                String content = note.getString("content");
                                Note x = new Note(id , title , content ) ;
                                titles[i] = title ;
                                Constant.user_notes.add(x) ;
                            }
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


    String _getToken(){
        SharedPreferences preferences = getSharedPreferences("Token_shp" , MODE_PRIVATE) ;
        return preferences.getString("token",  "") ;
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