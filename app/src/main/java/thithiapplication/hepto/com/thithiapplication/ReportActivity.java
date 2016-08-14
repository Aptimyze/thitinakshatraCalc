package thithiapplication.hepto.com.thithiapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import thithiapplication.hepto.com.thithiapplication.database.DatabaseHelper;
import thithiapplication.hepto.com.thithiapplication.settings.AppSettings;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Intent i = getIntent();
        if (i.hasExtra("aKey")){
            String value = i.getStringExtra("aKey");

            WebView webView = (WebView) findViewById(R.id.webView1);
            webView.loadData(value, "text/html; charset=utf-8", "UTF-8");
        }


    }

}
