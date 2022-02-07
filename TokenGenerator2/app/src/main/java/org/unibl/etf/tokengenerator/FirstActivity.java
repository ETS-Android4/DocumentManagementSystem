package org.unibl.etf.tokengenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameLbl=(EditText) findViewById(R.id.usernameLbl);
        EditText passwordLbl=(EditText) findViewById(R.id.passwordLbl);
        Button button = (Button) findViewById(R.id.proceedBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!usernameLbl.getText().toString().isEmpty() && !passwordLbl.getText().toString().isEmpty()){
                /*    SharedPreferences settings=getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString(USERNAME_KEY, usernameLbl.getText().toString());
                    editor.commit();*/
                    Intent switchActivityIntent = new Intent(FirstActivity.this, SecondActivity.class);
                    switchActivityIntent.putExtra("username", usernameLbl.getText().toString());
                    switchActivityIntent.putExtra("password", passwordLbl.getText().toString());
                    startActivity(switchActivityIntent);
                }
            }
        });

    }
}
