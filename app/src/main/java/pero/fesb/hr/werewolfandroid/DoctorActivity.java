package pero.fesb.hr.werewolfandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class DoctorActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;
    final Handler playersListHandler = new Handler();
    private static String playersListBuffer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        final TextView roomIdTextView = (TextView) findViewById(R.id.roomIdTextView);
        final TextView playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        final TextView playerRoleTextView = (TextView) findViewById(R.id.playerRoleTextView);
        final TextView werewolvesCountTextView = (TextView) findViewById(R.id.werewolvesCountTextView);
        final TextView villagersCountTextView = (TextView) findViewById(R.id.villagersCountTextView);
        final ListView playersList = (ListView) findViewById(R.id.playersList);
        final Button confirmButton = (Button) findViewById(R.id.confirmButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        roomIdTextView.setText(myPreferences.getString("roomId"));
        playerNameTextView.setText(myPreferences.getString("playerName"));
        playerRoleTextView.setText(myPreferences.getString("playerRole"));

        RequestParams requestParams = new RequestParams();
        requestParams.add("roomId", myPreferences.getString("roomId"));
        asyncHttpClient.post(API_URL + "fetch-count", requestParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                String[] array = responseString.split(Pattern.quote("||"));
                werewolvesCountTextView.setText(array[0]);
                villagersCountTextView.setText(array[1]);
            }
        });

        playersListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                asyncHttpClient.post(API_URL+"players-list-doctor", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(playersListBuffer.equals("") || !responseString.equals(playersListBuffer)) {

                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
                            Gson mGson = new Gson();
                            PlayersDataStorage.players = mGson.fromJson(responseString, Player[].class);
                            PlayersDataStorage.fillData();
                            playersList.setAdapter(new PlayersAdapter(getApplicationContext()));
                            playersList.setSelection(playersList.getCount());

                            // On player click send vote as action
                            playersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Player player = PlayersDataStorage.playersListViewData.get(i);
                                    RequestParams requestParams = new RequestParams();
                                    requestParams.add("roomId", myPreferences.getString("roomId"));
                                    requestParams.add("playerName", myPreferences.getString("playerName"));
                                    requestParams.add("action", player.getPlayerName().split(Pattern.quote(" "))[0]);
                                    asyncHttpClient.post(API_URL+"doctor-vote", requestParams, new TextHttpResponseHandler() {
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }

                        // End on player click

                        playersListBuffer = responseString;
                    }
                });
                playersListHandler.postDelayed(this, 2000);
            }
        }, 0);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                asyncHttpClient.post(API_URL + "doctor-confirm", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();

                        if (responseString.equals("HealSuccessful")) {
                            Toast.makeText(getApplicationContext(), "HealSuccessful", Toast.LENGTH_SHORT).show();
                            playersListBuffer = "";
                            playersListHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(DoctorActivity.this, SleepActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
            }
        });
    }
}
