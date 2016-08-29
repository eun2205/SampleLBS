package example.tacademy.samplelbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class IntentActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent);

//        Intent intent = getIntent();
//        String data = intent.getStringExtra("lat");
//        String data = intent.getStringExtra("hello");
//        Double lat = intent.getExtras().getDouble("lat");
        Bundle b = getIntent().getExtras();
        double result = b.getDouble("lat");

        textView = (TextView)findViewById(R.id.text_intent);
        textView.setText("lat :" +result);
    }
}
