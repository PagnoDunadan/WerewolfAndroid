package pero.fesb.hr.werewolfandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class SleepActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;
    final Handler gamePhaseHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

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
                        if(responseString.equals("werewolves") && myPreferences.getString("playerRole").equals("werewolf")) {
                            Intent myIntent = new Intent(SleepActivity.this, WerewolfActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("doctor") && myPreferences.getString("playerRole").equals("doctor")) {
                            // TODO
//                            Intent myIntent = new Intent(SleepActivity.this, DoctorActivity.class);
//                            startActivity(myIntent);
//                            finish();
                        }
                        else if(responseString.equals("seer") && myPreferences.getString("playerRole").equals("seer")) {
                            // TODO
//                            Intent myIntent = new Intent(SleepActivity.this, SeerActivity.class);
//                            startActivity(myIntent);
//                            finish();
                        }
                        else if(responseString.equals("day")) {
                            // TODO
//                            Intent myIntent = new Intent(SleepActivity.this, VillagerActivity.class);
//                            startActivity(myIntent);
//                            finish();
                        }
                    }
                });

                gamePhaseHandler.postDelayed(this, 2000);
            }
        }, 0);
    }
}
