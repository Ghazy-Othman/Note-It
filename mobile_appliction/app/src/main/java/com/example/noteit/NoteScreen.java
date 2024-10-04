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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class NoteScreen extends AppCompatActivity {

    EditText title_edit_text , content_edit_text ;
    FloatingActionButton save_btn , share_btn  ;
    String msg = "Server error" ;
    boolean state = false ;
    AlertDialog dialogLoading ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_screen);

        title_edit_text = findViewById(R.id.edt_title) ;
        content_edit_text = findViewById(R.id.edt_content) ;
        save_btn = findViewById(R.id.save_btn) ;
        share_btn = findViewById(R.id.share_btn) ;


        ImageView backicon =findViewById(R.id.backIcon);
        TextView title = findViewById(R.id.app_name);

        backicon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(MainActivity.this, "hi", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra("id" , 0 );

        Note current_note = Constant.user_notes.get(id) ;

        title_edit_text.setText( current_note.title, TextView.BufferType.EDITABLE);
        content_edit_text.setText( current_note.content, TextView.BufferType.EDITABLE);

        save_btn.setOnClickListener(view -> {
            showLoadingDialog("Updating note");
            _update(current_note.id);
            new Handler(Looper.getMainLooper()).postDelayed(()->{
                dialogLoading.cancel();
                if(state){
                    finish();
                }
                else {
                    Toast.makeText(NoteScreen.this , msg , Toast.LENGTH_LONG).show();
                }
            } , 5000) ;
        });

        share_btn.setOnClickListener(view -> {
            _shareNote(current_note) ;
            finish();
        });

    }


    private void _shareNote(Note note){
        Intent intent=new Intent(Intent.ACTION_SEND);
        String data = note.title + "\n" + note.content;
        intent.putExtra(Intent.EXTRA_TEXT,data);
        intent.setType("text/plain");
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    private void _update(int note_id){

        String title = title_edit_text.getText().toString().trim();
        String content = content_edit_text.getText().toString().trim();

        OkHttpClient client = new OkHttpClient() ;
        RequestBody body = new FormBody.Builder()
                .add("title" , title)
                .add("content" , content).build();

        String newUrl = Constant.updateNote + String.valueOf(note_id) ;
        Request request = new Request.Builder()
                .addHeader("Authorization" , "Bearer " + _getToken())
                .header("Accept","application/json")
                .post(body).
                url(newUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                msg = e.getMessage() ;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try{
                    String res = response.body().string();
                    int code = response.code();
                    msg = res;
                    if(res!= null){
                        JSONObject obj  = new JSONObject(res) ;
                        String success = obj.getString("success") ;
                        if(success.equals("true")){
                            state = true ;
//                            msg = "Note updated successfully" ;
                        }
                    }
                    else {
                        msg = "Server wrong" ;
                    }
                }catch (IOException | JSONException error) {
                    state = false ;
//                    msg = error.getMessage() ;
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