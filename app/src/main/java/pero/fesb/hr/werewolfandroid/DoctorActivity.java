package pero.fesb.hr.werewolfandroid;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class DoctorActivity extends AppCompatActivity {
    private static String API_URL = MainActivity.API_URL;
    private static String playersListBuffer = "";
    final Handler playersListHandler = new Handler();
    Runnable playersListRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        final TextView headlineTextView = (TextView) findViewById(R.id.headlineTextView);
        final TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

        final Button infoButton = (Button) findViewById(R.id.infoButton);
        final ListView playersList = (ListView) findViewById(R.id.playersList);
        final Button confirmButton = (Button) findViewById(R.id.confirmButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        final MyPreferences myPreferences = new MyPreferences(this);

        // Theme related
        if (myPreferences.getString("themeColor").equals("pink")) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            infoButton.setBackgroundResource(R.drawable.info_white);
            headlineTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            descriptionTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
        }
        // End theme

        playersListRunnable = new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                asyncHttpClient.post(API_URL+"doctor-players-list", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(playersListBuffer.equals("") || !responseString.equals(playersListBuffer)) {
                            int index = playersList.getFirstVisiblePosition();
                            Gson mGson = new Gson();
                            PlayersDataStorage.players = mGson.fromJson(responseString, Player[].class);
                            PlayersDataStorage.fillData();
                            playersList.setAdapter(new PlayersAdapter(getApplicationContext()));
                            playersList.setSelection(index);

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
                                        }
                                    });
                                }
                            });
                        }

                        // End on player click

                        playersListBuffer = responseString;
                    }
                });
                playersListHandler.postDelayed(this, 1000);
            }
        };

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Toast.makeText(getApplicationContext(),
                                "Room: " + myPreferences.getString("roomId") + "\n" +
                                        "Player: " + myPreferences.getString("playerName") + "\n" +
                                        "Role: " + myPreferences.getString("playerRole") + "\n" +
                                        "Werewolves: " + array[0] + "\n" +
                                        "Villagers: " + array[1], Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

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
                        if (responseString.equals("HealSuccessful")) {
                            playersListBuffer = "";
                            playersListHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(DoctorActivity.this, SleepActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if (responseString.equals("NoVote")) {
                            Toast.makeText(getApplicationContext(), "You have to select a player", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onPause() {
        playersListHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }
    @Override
    protected void onResume() {
        playersListHandler.postDelayed(playersListRunnable, 1000);
        super.onResume();
    }
    @Override
    public void onBackPressed() {
    }
}
