package pero.fesb.hr.werewolfandroid;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_roles);

        final Button readyButton = (Button) findViewById(R.id.readyButton);
        final TextView roleTextView = (TextView) findViewById(R.id.roleTextView);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        final MyPreferences myPreferences = new MyPreferences(this);


        // TODO: Check if player is alive, then fetch his role
        // TODO: Show retry button on failure
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
                asyncHttpClient.post(API_URL + "get-phase", requestParams, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        if(responseString.equals("werewolves")) {
                            if(myPreferences.getString("playerRole").equals("werewolf")) {
                                Intent myIntent = new Intent(ShowRolesActivity.this, WerewolfActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                            else {
                                Intent myIntent = new Intent(ShowRolesActivity.this, SleepActivity.class);
                                startActivity(myIntent);
                                finish();
                            }

                        }
                        else if(responseString.equals("doctor")) {
                            Intent myIntent = new Intent(ShowRolesActivity.this, LobbyActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("seer")) {
                            Intent myIntent = new Intent(ShowRolesActivity.this, LobbyActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else if(responseString.equals("day")) {
                            Intent myIntent = new Intent(ShowRolesActivity.this, LobbyActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                });
            }
        });

        // TODO: STATUS TEXT VIEW, REMOVE IN PRODUCTION
        final TextView statusTextView1 = (TextView) findViewById(R.id.statusTextView1);
        final TextView statusTextView2 = (TextView) findViewById(R.id.statusTextView2);
        final TextView statusTextView3 = (TextView) findViewById(R.id.statusTextView3);
        final TextView statusTextView4 = (TextView) findViewById(R.id.statusTextView4);
        final TextView statusTextView5 = (TextView) findViewById(R.id.statusTextView5);

        statusTextView1.setText(statusTextView1.getText() + myPreferences.getString("roomId"));
        statusTextView2.setText(statusTextView2.getText() + myPreferences.getString("playerName"));
        statusTextView3.setText(statusTextView3.getText() + myPreferences.getString("playerRole"));
        statusTextView4.setText(statusTextView4.getText() + myPreferences.getString("gameStatus"));
        statusTextView5.setText(statusTextView5.getText() + myPreferences.getString("gamePhase"));
    }
}
