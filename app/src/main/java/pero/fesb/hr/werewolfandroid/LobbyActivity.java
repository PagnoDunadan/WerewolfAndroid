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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import com.google.gson.Gson;

public class LobbyActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;
    final Handler playersListHandler = new Handler();
    final Handler gamePhaseHandler = new Handler();
    private static String playersListBuffer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        final TextView roomIdTextView = findViewById(R.id.roomIdTextView);
        final ListView playersList = findViewById(R.id.playersList);
        final Button startButton = findViewById(R.id.startButton);
        final Button cancelButton = findViewById(R.id.cancelButton);
        final Button addPlayersButton = findViewById(R.id.addPlayersButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);

        roomIdTextView.setText("Room: " + myPreferences.getString("roomId"));

        playersListHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                asyncHttpClient.post(API_URL+"lobby-players-list", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(playersListBuffer.equals("") || !responseString.equals(playersListBuffer)) {
                            Gson mGson = new Gson();
                            PlayersDataStorage.players = mGson.fromJson(responseString, Player[].class);
                            PlayersDataStorage.fillData();
                            playersList.setAdapter(new PlayersAdapter(getApplicationContext()));
                            playersList.setSelection(playersList.getCount());

                            playersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Player player = PlayersDataStorage.playersListViewData.get(i);
                                    // TODO: On Player Click logika
                                }
                            });

                            playersListBuffer = responseString;
                        }
                    }
                });
                playersListHandler.postDelayed(this, 1000);
            }
        }, 0);

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
                        if(responseString.equals("showRoles")) {
                            playersListBuffer = "";
                            playersListHandler.removeCallbacksAndMessages(null);
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(LobbyActivity.this, ShowRolesActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
                gamePhaseHandler.postDelayed(this, 1000);
            }
        }, 0);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                asyncHttpClient.post(API_URL + "lobby-start-game", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (responseString.equals("GameStarted")) {
                            playersListBuffer = "";
                            playersListHandler.removeCallbacksAndMessages(null);
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(LobbyActivity.this, ShowRolesActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if (responseString.equals("MinimumPlayers7")) {
                            Toast.makeText(getApplicationContext(), "Minimum players 7", Toast.LENGTH_SHORT).show();
                        }
                        else if (responseString.equals("MaximumPlayers16")) {
                            Toast.makeText(getApplicationContext(), "Maximum players 16", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // TODO: Hide addPlayersButton in production
        addPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                requestParams.add("playerName", myPreferences.getString("playerName"));
                asyncHttpClient.post(API_URL + "lobby-add-players", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("roomId", myPreferences.getString("roomId"));
                requestParams.add("playerName", myPreferences.getString("playerName"));
                asyncHttpClient.post(API_URL + "lobby-remove-player", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if (responseString.equals("NoMorePlayers")) {
                            Toast.makeText(getApplicationContext(), "NoMorePlayers", Toast.LENGTH_SHORT).show();
                        }
                        else if (responseString.equals("RemovePlayerSuccessful")) {
                            Toast.makeText(getApplicationContext(), "RemovePlayerSuccessful", Toast.LENGTH_SHORT).show();
                        }
                        playersListBuffer = "";
                        playersListHandler.removeCallbacksAndMessages(null);
                        gamePhaseHandler.removeCallbacksAndMessages(null);
                        Intent myIntent = new Intent(LobbyActivity.this, MainActivity.class);
                        startActivity(myIntent);
                        finish();
                    }
                });
            }
        });
    }
}
