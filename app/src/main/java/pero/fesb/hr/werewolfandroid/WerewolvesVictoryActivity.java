package pero.fesb.hr.werewolfandroid;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WerewolvesVictoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werewolves_victory);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        final TextView headlineTextView = (TextView) findViewById(R.id.headlineTextView);
        final TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

        final MyPreferences myPreferences = new MyPreferences(this);

        // Theme related
        if (myPreferences.getString("themeColor").equals("pink")) {
            mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            headlineTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
            descriptionTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
        }
        // End theme
    }
}
