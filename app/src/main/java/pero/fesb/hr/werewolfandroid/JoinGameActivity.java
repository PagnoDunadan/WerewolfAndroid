package pero.fesb.hr.werewolfandroid;


import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Field;

import cz.msebera.android.httpclient.Header;

public class JoinGameActivity extends Activity {
    private static String API_URL = MainActivity.API_URL;

    // Theme related
    public static void setCursorColor(EditText view, @ColorInt int color) {
        // Huge thanks to Jared Rummler https://stackoverflow.com/a/26543290
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }
    // End theme

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        final LinearLayout mainLayout = findViewById(R.id.mainLayout);
        final TextView textView1 = findViewById(R.id.textView1);
        final TextView textView2 = findViewById(R.id.textView2);
        final TextView textView3 = findViewById(R.id.textView3);

        final EditText roomCodeEditText = findViewById(R.id.roomCodeEditText);
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final Button joinGameButton = findViewById(R.id.joinGameButton);
        final Button cancelButton = findViewById(R.id.cancelButton);

        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        final MyPreferences myPreferences = new MyPreferences(this);

        //Toast.makeText(getApplicationContext(), API_URL, Toast.LENGTH_LONG).show();

        // Theme related
        if (myPreferences.getString("themeColor").equals("pink")) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            textView1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            textView2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            textView3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));

            roomCodeEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            nameEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));

            setCursorColor(roomCodeEditText, ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            setCursorColor(nameEditText, ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));

            ViewCompat.setBackgroundTintList(nameEditText, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters)));
            ViewCompat.setBackgroundTintList(roomCodeEditText, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters)));
        }
        // End theme

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
