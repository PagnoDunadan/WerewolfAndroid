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

public class JoinGameActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        final EditText roomCodeEditText = findViewById(R.id.roomCodeEditText);
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final Button joinGameButton = findViewById(R.id.joinGameButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roomCodeEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter room code", Toast.LENGTH_SHORT).show();
                else if(nameEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("roomId", roomCodeEditText.getText().toString());
                    requestParams.add("playerName", nameEditText.getText().toString());
                    asyncHttpClient.post(API_URL + "join-game", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            if (responseString.equals("RoomNotFound")) {
                                Toast.makeText(getApplicationContext(), "Room not found", Toast.LENGTH_SHORT).show();
                            }
                            else if (responseString.equals("NameInUse")) {
                                Toast.makeText(getApplicationContext(), "Name is already in use", Toast.LENGTH_SHORT).show();
                            }
                            else if (responseString.equals("JoinGameSuccessful")) {
                                myPreferences.setString("roomId", roomCodeEditText.getText().toString());
                                myPreferences.setString("playerName", nameEditText.getText().toString());
                                Intent myIntent = new Intent(JoinGameActivity.this, LobbyActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                            else if (responseString.equals("ReconnectSuccessful")) {
                                Toast.makeText(getApplicationContext(), "Reconnect successful", Toast.LENGTH_SHORT).show();
                                myPreferences.setString("roomId", roomCodeEditText.getText().toString());
                                myPreferences.setString("playerName", nameEditText.getText().toString());
                                Intent myIntent = new Intent(JoinGameActivity.this, ShowRolesActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                            else if (responseString.equals("MatchInProgress")) {
                                Toast.makeText(getApplicationContext(), "Cannot join match in progress", Toast.LENGTH_SHORT).show();
                            }
                            else if (responseString.equals("GameFinished")) {
                                Toast.makeText(getApplicationContext(), "Cannot join finished game", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(JoinGameActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(JoinGameActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}
