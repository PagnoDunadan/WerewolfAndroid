package pero.fesb.hr.werewolfandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ShowRolesActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;
    final Handler gamePhaseHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_roles);

        final Button readyButton = findViewById(R.id.readyButton);
        final TextView roleTextView = findViewById(R.id.roleTextView);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        RequestParams requestParams = new RequestParams();
        requestParams.add("roomId", myPreferences.getString("roomId"));
        requestParams.add("playerName", myPreferences.getString("playerName"));
        asyncHttpClient.post(API_URL + "fetch-role", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                myPreferences.setString("playerRole", responseString);
                roleTextView.setText(responseString);
            }
        });

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                requestParams.add("playerName", myPreferences.getString("playerName"));
                asyncHttpClient.post(API_URL + "show-roles-ready", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(responseString.equals("EveryoneReady")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(ShowRolesActivity.this, SleepActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("Reconnecting")) {
                            // TODO: Posalji do tocnog aktivitija
                            Toast.makeText(getApplicationContext(), "Reconnect successful", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(ShowRolesActivity.this, SleepActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                            if(readyButton.getText().equals("I'm ready!")) {
                                readyButton.setText("Waiting for other players");
                                gamePhaseHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        RequestParams requestParams = new RequestParams();
                                        requestParams.add("roomId", myPreferences.getString("roomId"));
                                        asyncHttpClient.post(API_URL + "get-phase", requestParams, new TextHttpResponseHandler() {
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                                            }
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                if(!responseString.equals("showRoles")) {
                                                    gamePhaseHandler.removeCallbacksAndMessages(null);
                                                    Intent myIntent = new Intent(ShowRolesActivity.this, SleepActivity.class);
                                                    startActivity(myIntent);
                                                    finish();
                                                }
                                            }
                                        });
                                        gamePhaseHandler.postDelayed(this, 1000);
                                    }
                                }, 0);
                            }
                        }
                    }
                });
            }
        });
    }
}
