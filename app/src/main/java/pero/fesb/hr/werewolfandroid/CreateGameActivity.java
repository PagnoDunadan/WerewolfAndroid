package pero.fesb.hr.werewolfandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class CreateGameActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_game);

        final EditText nameEditText = findViewById(R.id.nameEditText);
        final Button startGameButton = findViewById(R.id.startGameButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("playerName", nameEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "create-game", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            myPreferences.setString("roomId", responseString);
                            myPreferences.setString("playerName", nameEditText.getText().toString());
                            Intent myIntent = new Intent(CreateGameActivity.this, LobbyActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CreateGameActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(CreateGameActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}
