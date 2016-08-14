package thithiapplication.hepto.com.thithiapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import thithiapplication.hepto.com.thithiapplication.database.DatabaseHelper;
import thithiapplication.hepto.com.thithiapplication.settings.AppSettings;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnThithiCalc = (Button) findViewById(R.id.btnThithi);
        Button btnStarBirthDayCalc = (Button) findViewById(R.id.btnStarBirthDay);



        btnThithiCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),ThithiCalculationActivity.class);
                startActivity(intent);
            }
        });

        btnStarBirthDayCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),StarBirthDayCalculationActivity.class);
                startActivity(intent);
            }
        });

        if(AppSettings.getDatabaseStatus(getApplicationContext()))
        {
            DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
            int count = databaseHelper.getThithiCalcTableCount();
            Toast.makeText(getApplicationContext(),count+"",Toast.LENGTH_LONG).show();
        }
        else
        {
            AppSettings.setDatabaseStatus(getApplicationContext(),true);
            new PostTask().execute("");
        }


    }


    private class PostTask extends AsyncTask<String, Integer, String> {

        ProgressDialog progress = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Setting Databse:");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();

        }

        @Override
        protected String doInBackground(String... params) {
            DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
            ThithiData1996 thithiData1996 = new ThithiData1996();
            ThithiData2001 thithiData2001 = new ThithiData2001();
            ThithiData2006 thithiData2006 = new ThithiData2006();
            ThithiData2011 thithiData2011 = new ThithiData2011();
            ThithiData2016 thithiData2016 = new ThithiData2016();

            thithiData1996.insertData(dbHelper);
            thithiData2001.insertData(dbHelper);
            thithiData2006.insertData(dbHelper);
            thithiData2011.insertData(dbHelper);
            thithiData2016.insertData(dbHelper);

            dbHelper.close();
            return "All Done!";
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.dismiss();
        }
    }




}
