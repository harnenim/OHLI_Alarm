package moe.noitamina.ohli;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NotiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent().setClass(getApplicationContext(), MainActivity.class));
        startActivity(new Intent(Intent.ACTION_VIEW).setData(getIntent().getData()));
        finish();
    }
}
