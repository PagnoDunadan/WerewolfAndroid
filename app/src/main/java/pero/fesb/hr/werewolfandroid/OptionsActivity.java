package pero.fesb.hr.werewolfandroid;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_options);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        final TextView apiUrlTextView = (TextView) findViewById(R.id.apiUrlTextView);
        final EditText apiUrlEditText = (EditText) findViewById(R.id.apiUrlEditText);
        final Button apiUrlButton = (Button) findViewById(R.id.apiUrlButton);
        final TextView themeTextView = (TextView) findViewById(R.id.themeTextView);
        final Spinner themeSpinner = (Spinner) findViewById(R.id.themeSpinner);

        final MyPreferences myPreferences = new MyPreferences(this);

        apiUrlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apiUrlEditText.getText().toString().equals("")) Toast.makeText(getApplicationContext(), "Please enter you server address", Toast.LENGTH_SHORT).show();
                else {
                    myPreferences.setString("API_URL", "http://"+apiUrlEditText.getText().toString()+":8000/");
                    apiUrlEditText.setText("");
                    Toast.makeText(getApplicationContext(), "API_URL updated, please restart Werewolf app", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                    apiUrlTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackLetters));
                    apiUrlEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackLetters));
                    setCursorColor(apiUrlEditText, ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    ViewCompat.setBackgroundTintList(apiUrlEditText, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)));
                    themeTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackLetters));
                    ((TextView) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackLetters));
                }
                else if (position == 1) {
                    myPreferences.setString("themeColor", "pink");
                    mainLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    apiUrlTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
                    apiUrlEditText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
                    setCursorColor(apiUrlEditText, ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters));
                    ViewCompat.setBackgroundTintList(apiUrlEditText, ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorWhiteLetters)));
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
