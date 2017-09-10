package pero.fesb.hr.werewolfandroid;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class SleepActivity extends AppCompatActivity {
    private static String API_URL = MainActivity.API_URL;
    final Handler gamePhaseHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        final TextView phaseTextView = (TextView) findViewById(R.id.phaseTextView);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        RequestParams requestParams = new RequestParams();
        requestParams.add("roomId", myPreferences.getString("roomId"));
        requestParams.add("playerName", myPreferences.getString("playerName"));
        asyncHttpClient.post(API_URL + "fetch-player-status", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(responseString.equals("dead")) {
                    gamePhaseHandler.removeCallbacksAndMessages(null);
                    Intent myIntent = new Intent(SleepActivity.this, DeadActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            }
        });

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
                        phaseTextView.setText("Current phase: " + responseString);

                        if(responseString.equals("werewolves") && myPreferences.getString("playerRole").equals("werewolf")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(SleepActivity.this, WerewolfActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("doctor") && myPreferences.getString("playerRole").equals("doctor")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(SleepActivity.this, DoctorActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("seer") && myPreferences.getString("playerRole").equals("seer")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(SleepActivity.this, SeerActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("villagersVictory")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(SleepActivity.this, VillagersVictoryActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("werewolvesVictory")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(SleepActivity.this, WerewolvesVictoryActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("day")) {
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(SleepActivity.this, DayActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
                gamePhaseHandler.postDelayed(this, 2000);
            }
        }, 2000);
    }
}
