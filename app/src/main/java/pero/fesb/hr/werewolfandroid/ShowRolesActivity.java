package pero.fesb.hr.werewolfandroid;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class ShowRolesActivity extends AppCompatActivity {
    private static String API_URL = MainActivity.API_URL;
    final Handler gamePhaseHandler = new Handler();
    Runnable gamePhaseRunnable;
    Button readyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_roles);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);

        readyButton = (Button) findViewById(R.id.readyButton);
        final ImageView roleImageView = (ImageView) findViewById(R.id.roleImageView);
        final TextView roleTextView = (TextView) findViewById(R.id.roleTextView);
        final TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        final MyPreferences myPreferences = new MyPreferences(this);

        // Theme related
        if (myPreferences.getString("themeColor").equals("pink")) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            roleTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            descriptionTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
        }
        // End theme

        gamePhaseRunnable = new Runnable() {
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
        };

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
                if (responseString.equals("werewolf")) {
                    roleImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.werewolf_imageview_width);
                    roleImageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.werewolf_imageview_height);
                    roleImageView.setBackgroundResource(R.drawable.werewolf);
                    roleTextView.setText("Werewolf");
                    descriptionTextView.setText("At night you wake up and together with your werewolf friend decide on a victim to kill. During the day you try to pose as a regular villager and not get yourself killed.");
                }
                else if (responseString.equals("doctor")) {
                    roleImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.doctor_imageview_width);
                    roleImageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.doctor_imageview_height);
                    roleImageView.setBackgroundResource(R.drawable.doctor);
                    roleTextView.setText("Doctor");
                    descriptionTextView.setText("At night you awaken and select a player which cannot be killed by the werewolves that night.");
                }
                else if (responseString.equals("seer")) {
                    roleImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.seer_imageview_width);
                    roleImageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.seer_imageview_height);
                    roleImageView.setBackgroundResource(R.drawable.seer);
                    roleTextView.setText("Seer");
                    descriptionTextView.setText("Each night you can uncover the role of another player.");
                }
                else if (responseString.equals("villager")) {
                    roleImageView.getLayoutParams().width = (int) getResources().getDimension(R.dimen.villager_imageview_width);
                    roleImageView.getLayoutParams().height = (int) getResources().getDimension(R.dimen.villager_imageview_height);
                    roleImageView.setBackgroundResource(R.drawable.villager);
                    roleTextView.setText("Villager");
                    descriptionTextView.setText("During the day you discuss with the village who could be a werewolf and decide on a person to kill.");
                }
                roleImageView.invalidate();
                descriptionTextView.invalidate();
                roleImageView.requestLayout();
                descriptionTextView.requestLayout();
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
                            Toast.makeText(getApplicationContext(), "Reconnect successful", Toast.LENGTH_SHORT).show();
                            gamePhaseHandler.removeCallbacksAndMessages(null);
                            Intent myIntent = new Intent(ShowRolesActivity.this, SleepActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_SHORT).show();
                            if(readyButton.getText().equals("I'm ready!")) {
                                readyButton.setText("Waiting for other players");
                                gamePhaseHandler.postDelayed(gamePhaseRunnable, 0);
                            }
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onPause() {
        gamePhaseHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }
    @Override
    protected void onResume() {
        if (readyButton.getText().equals("Waiting for other players")) {
            gamePhaseHandler.postDelayed(gamePhaseRunnable, 1000);
        }
        super.onResume();
    }
    @Override
    public void onBackPressed() {
    }
}
