package org.unibl.etf.tokengenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.unibl.etf.tokengenerator.retrofit.RetrofitClient;
import org.unibl.etf.tokengenerator.retrofit.Token;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {

    private static final String PREFS_NAME="MyPrefs";
    private static final String USERNAME_KEY="username";
    private TextView tokenLbl;
    private Button generateTokenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tokenLbl=(TextView) findViewById(R.id.tokenLbl);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getToken();
            }
        });
    }

    private void getToken(){
        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        String password=intent.getStringExtra("password");

        if(username!=null && password!=null) {
            RetrofitClient client = RetrofitClient.getInstance();
            HashMap<String, Object> map=new HashMap<>();
            map.put("username", username);
            map.put("password", password);

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(map)).toString());
            Call<ResponseBody> response=client.getMyApi().createPost(body);
            response.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            tokenLbl.setText(jsonObject.getString("token"));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                        System.out.println("greskaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        t.printStackTrace();
                }

            });
        }
    }
}