package example.tacademy.samplelbs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AAAAAAAAAAAActivity extends AppCompatActivity {
    EditText addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaaaaaaaaaa);
        addressView = (EditText)findViewById(R.id.edit_address);
        addressView.setEnabled(false);

        Button btn = (Button)findViewById(R.id.btn_change);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressView.setEnabled(true);
            }
        });

        addressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AAAAAAAAAAAActivity.this, DomapActivity.class));
            }
        });
    }

}
