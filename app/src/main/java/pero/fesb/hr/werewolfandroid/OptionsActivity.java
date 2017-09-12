package pero.fesb.hr.werewolfandroid;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        final TextView themeTextView = (TextView) findViewById(R.id.themeTextView);
        final Spinner themeSpinner = (Spinner) findViewById(R.id.themeSpinner);

        final MyPreferences myPreferences = new MyPreferences(this);

        // Theme related
        List<String> list = new ArrayList<String>();
        list.add("original");
        list.add("colorful");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(20);
                return view;
            }
        };

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(dataAdapter);

        if (myPreferences.getString("themeColor").equals("pink")) {
            themeSpinner.setSelection(1);
        }
        else {
            themeSpinner.setSelection(0);
        }

        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    myPreferences.setString("themeColor", "white");
                    mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorOriginalBackground));
                    themeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackLetters));
                    ((TextView) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackLetters));
                }
                else if (position == 1) {
                    myPreferences.setString("themeColor", "pink");
                    mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    themeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
                    ((TextView) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // End theme
    }
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(OptionsActivity.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}
