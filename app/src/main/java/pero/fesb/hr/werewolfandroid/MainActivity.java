package pero.fesb.hr.werewolfandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    public static String API_URL = "http://192.168.1.4:8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button createGameButton = (Button) findViewById(R.id.createGameButton);
        final Button joinGameButton = (Button) findViewById(R.id.joinGameButton);

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
    }
}
