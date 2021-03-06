package pero.fesb.hr.werewolfandroid;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    public static String API_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        final Button createGameButton = (Button) findViewById(R.id.createGameButton);
        final Button joinGameButton = (Button) findViewById(R.id.joinGameButton);
        final Button optionsButton = (Button) findViewById(R.id.optionsButton);

        final MyPreferences myPreferences = new MyPreferences(this);

        // Theme related
        if (myPreferences.getString("themeColor").equals("pink")) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }
        // End theme

        API_URL = myPreferences.getString("API_URL");

        myPreferences.setString("roomId", "");
        myPreferences.setString("playerName", "");
        myPreferences.setString("playerRole", "");

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CreateGameActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, JoinGameActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
