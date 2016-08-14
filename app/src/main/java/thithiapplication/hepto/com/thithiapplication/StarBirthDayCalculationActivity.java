package thithiapplication.hepto.com.thithiapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import thithiapplication.hepto.com.thithiapplication.database.DatabaseHelper;
import thithiapplication.hepto.com.thithiapplication.model.Details;

public class StarBirthDayCalculationActivity extends AppCompatActivity implements View.OnClickListener{


    EditText edtxtDate, edtxtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String[] savedValues;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_birthdaycalculation);


        edtxtDate = (EditText)findViewById(R.id.edtxtDate);
        edtxtTime = (EditText)findViewById(R.id.edtxtTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        String currentDateandTime = sdf.format(new Date());
        String[] cDT = currentDateandTime.split(" ");
        edtxtDate.setText(cDT[0]);
        edtxtTime.setText(cDT[1]);

        final AutoCompleteTextView actxtName = (AutoCompleteTextView) findViewById(R.id.actxtName);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Details> allSavedStarBirthday = dbHelper.getAllStar();
        dbHelper.close();

        if(allSavedStarBirthday != null && allSavedStarBirthday.size() != 0) {

            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutAutoComplete);
            layout.setVisibility(View.VISIBLE);

            savedValues = new String[allSavedStarBirthday.size()];
            for (int i = 0; i < allSavedStarBirthday.size(); i++) {
                savedValues[i] = allSavedStarBirthday.get(i).getName();
            }

            adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,savedValues);
            actxtName.setAdapter(adapter);
            actxtName.setThreshold(0);
        }



        actxtName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                int pos = -1;

                for (int i = 0; i < savedValues.length; i++) {
                    if (savedValues[i].equals(selection)) {
                        pos = i;
                        break;
                    }
                }
                System.out.println("Position " + pos);

                DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                List<Details> allSavedStarBirthday = dbHelper.getAllStar();

                EditText edtxtName = (EditText) findViewById(R.id.edtxtName);
                edtxtName.setText(actxtName.getText().toString());

                edtxtDate.setText(allSavedStarBirthday.get(pos).getDate());
                edtxtTime.setText(allSavedStarBirthday.get(pos).getTime().replace(".",":"));


            }
        });


        edtxtDate.setOnClickListener(this);
        edtxtTime.setOnClickListener(this);

        Button btnCalcStarBirthday = (Button) findViewById(R.id.btnCalcStarBirthday);
        btnCalcStarBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date = edtxtDate.getText().toString();
                String time = edtxtTime.getText().toString();
                EditText edtxtName = (EditText) findViewById(R.id.edtxtName);
                String name = edtxtName.getText().toString();
                if(date.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Enter Date",Toast.LENGTH_LONG).show();
                }
                else if(time.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Enter Time",Toast.LENGTH_LONG).show();
                }
                else if(!((Integer.parseInt(date.split("-")[0])) >= 1951 && (Integer.parseInt(date.split("-")[0])) <= 2020))
                {
                    Toast.makeText(getApplicationContext(),"Enter Date Between 2001 to 2020",Toast.LENGTH_LONG).show();
                }
                else {


                    String html = "";

                    if(name.length() != 0)
                    {
                        html = html + "<p> Name:" + name + "</p>";
                    }
                    html = html + "<p> Date:" + date + "</p>";
                    html = html + "<p> Time:" + time + "</p>";

                    float realTime = Float.parseFloat(time.replace(":","."));
                    String date1;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(sdf.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    date1 = sdf1.format(calendar.getTime());


                    String date2;
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(sdf.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    calendar.add(Calendar.DATE, -1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                    sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    date2 = sdf1.format(calendar.getTime());

                    String eDate = null;
                    float sunRaise  = 0;
                    float sun530  = 0;
                    float moon530 = 0;

                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                    String selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate = '" + date + "'";
                    SQLiteDatabase db = databaseHelper.getReadableDatabase();
                    Cursor c = db.rawQuery(selectQuery, null);


                    if (c.moveToFirst()) {
                        do {
                            eDate = c.getString((c.getColumnIndex("edate")));
                            sunRaise = c.getFloat((c.getColumnIndex("sunraise")));
                            sun530 = c.getFloat((c.getColumnIndex("sun")));
                            moon530 = c.getFloat((c.getColumnIndex("moon")));
                        } while (c.moveToNext());
                    }

                    String eDate1 = null;
                    float sunRaise1 = 0;
                    float sun5301 = 0;
                    float moon5301 = 0;


                    selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate = '" + date1 + "'";
                    Cursor c1 = db.rawQuery(selectQuery, null);

                    if (c1.moveToFirst()) {
                        do {
                            eDate1 = c1.getString((c1.getColumnIndex("edate")));
                            sunRaise1 = c1.getFloat((c1.getColumnIndex("sunraise")));
                            sun5301 = c1.getFloat((c1.getColumnIndex("sun")));
                            moon5301 = c1.getFloat((c1.getColumnIndex("moon")));
                        } while (c1.moveToNext());
                    }

                    String eDate2 = null;
                    float sunRaise2 = 0;
                    float sun5302 = 0;
                    float moon5302 = 0;


                    selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate = '" + date2 + "'";
                    Cursor c2 = db.rawQuery(selectQuery, null);

                    if (c2.moveToFirst()) {
                        do {
                            eDate2 = c2.getString((c2.getColumnIndex("edate")));
                            sunRaise2 = c2.getFloat((c2.getColumnIndex("sunraise")));
                            sun5302 = c2.getFloat((c2.getColumnIndex("sun")));
                            moon5302 = c2.getFloat((c2.getColumnIndex("moon")));
                        } while (c2.moveToNext());
                    }

                    String sun = null;
                    String moon = null;

                    if(realTime > 5.30) {
                        if (sun530 < sun5301) {
                            sun = String.format("%.4f", sun530 + (((sun5301 - sun530) / 24) * (realTime - 5.5)));
                        } else if ((sun530 + (((sun5301 + 360 - sun530) / 24) * (realTime - 5.5))) < 360) {
                            sun = String.format("%.2f", sun530 + (((sun5301 + 360 - sun530) / 24) * (realTime - 5.5)));
                        } else {
                            sun = String.format("%.4f", (sun530 + (((sun5301 + 360 - sun530) / 24) * (realTime - 5.5))) - 360);
                        }

                        if (moon530 < moon5301) {
                            moon = String.format("%.4f", moon530 + (((moon5301 - moon530) / 24) * (realTime - 5.5)));
                        } else if ((moon530 + (((moon5301 + 360 - moon530) / 24) * (realTime - 5.5))) < 360) {
                            moon = String.format("%.4f", moon530 + (((moon5301 + 360 - moon530) / 24) * (realTime - 5.5)));
                        } else {
                            moon = String.format("%.4f", (moon530 + (((moon5301 + 360 - moon530) / 24) * (realTime - 5.5))) - 360);
                        }
                    }

                    if(realTime < 5.30) {
                        if (sun530 > sun5302) {
                            sun = String.format("%.4f", sun530 - (((sun530 - sun5302) / 24) * (5.5 - realTime)));
                        } else if ((sun530 - (((sun530 + 360 - sun5302) / 24) * (5.5 - realTime))) < 360) {
                            sun = String.format("%.2f", sun530 - (((sun530 + 360 - sun5302) / 24) * (5.5 - realTime)));
                        } else {
                            sun = String.format("%.4f", (sun530 - (((sun530 + 360 - sun5302) / 24) * (5.5 - realTime))) - 360);
                        }

                        if (moon530 > moon5302) {
                            moon = String.format("%.4f", moon530 - (((moon530 - moon5302) / 24) * (5.5 - realTime)));
                        } else if ((moon530 - (((moon530 + 360 - moon5302) / 24) * (5.5 - realTime))) < 360) {
                            moon = String.format("%.4f", moon530 - (((moon530 + 360 - moon5302) / 24) * (5.5 - realTime)));
                        } else {
                            moon = String.format("%.4f", (moon530 - (((moon530 + 360 - moon5302) / 24) * (5.5 - realTime))) - 360);
                        }
                    }


                    //Toast.makeText(getApplicationContext(),sun + ":" + moon,Toast.LENGTH_LONG).show();
                    float moonvalue = Float.parseFloat(moon);

                    if(moonvalue > 0 && moonvalue <= 3.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அசுவனி, பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அசுவனி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 3.3334 && moonvalue <= 6.66666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அசுவனி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அசுவனி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 6.66667 && moonvalue <= 9.99999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அசுவனி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அசுவனி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 10 && moonvalue <= 13.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அசுவனி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அசுவனி , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 13.3333 && moonvalue <= 16.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பரணி , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பரணி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 16.6666 && moonvalue <= 19.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பரணி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பரணி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 20 && moonvalue <= 23.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பரணி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பரணி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 23.3333 && moonvalue <= 26.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பரணி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பரணி , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 26.6666 && moonvalue <= 29.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கார்த்திகை , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கார்த்திகை , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 30 && moonvalue <= 33.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கார்த்திகை , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கார்த்திகை , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 33.3333 && moonvalue <= 36.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கார்த்திகை , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கார்த்திகை , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 36.6666 && moonvalue <= 39.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கார்த்திகை , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கார்த்திகை , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 40 && moonvalue <= 43.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரோகினி , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரோகினி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 43.3333 && moonvalue <= 46.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரோகினி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரோகினி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 46.6666 && moonvalue <= 49.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரோகினி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரோகினி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 50 && moonvalue <= 53.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரோகினி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரோகினி , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 53.3333 && moonvalue <= 56.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மிருகசீரிஷம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மிருகசீரிஷம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 56.6666 && moonvalue <= 59.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மிருகசீரிஷம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மிருகசீரிஷம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 60 && moonvalue <= 63.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மிருகசீரிஷம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மிருகசீரிஷம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 63.3333 && moonvalue <= 66.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மிருகசீரிஷம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மிருகசீரிஷம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 66.6666 && moonvalue <= 69.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவாதிரை, பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவாதிரை, பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 70 && moonvalue <= 73.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவாதிரை, பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவாதிரை, பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 73.3333 && moonvalue <= 76.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவாதிரை, பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவாதிரை, பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 76.6666 && moonvalue <= 79.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவாதிரை, பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவாதிரை, பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 80 && moonvalue <= 83.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: புனர்பூசம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" புனர்பூசம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 83.3333 && moonvalue <= 86.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: புனர்பூசம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" புனர்பூசம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 86.6666 && moonvalue <= 89.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: புனர்பூசம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" புனர்பூசம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 90 && moonvalue <= 93.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: புனர்பூசம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" புனர்பூசம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 93.3333 && moonvalue <= 96.6666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூசம், பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூசம், பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 96.6666 && moonvalue <= 99.9999)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூசம், பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூசம், பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 100 && moonvalue <= 103.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூசம், பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூசம், பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 103.3333 && moonvalue <= 106.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூசம், பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூசம், பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 106.666 && moonvalue <= 110)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ஆயில்யம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ஆயில்யம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 110 && moonvalue <= 113.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ஆயில்யம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ஆயில்யம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 113.3333 && moonvalue <= 116.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ஆயில்யம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ஆயில்யம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 116.666 && moonvalue <= 120)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ஆயில்யம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" Ashlesha , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 120 && moonvalue <= 123.333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மகம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மகம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 123.333 && moonvalue <= 126.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மகம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மகம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 126.666 && moonvalue <= 130)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மகம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மகம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 130 && moonvalue <= 133.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மகம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மகம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 133.3333 && moonvalue <= 136.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 136.666 && moonvalue <= 140)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 140 && moonvalue <= 143.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 143.3333 && moonvalue <= 146.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 146.666 && moonvalue <= 150)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திரம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திரம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 150 && moonvalue <= 153.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திரம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திரம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 153.3333 && moonvalue <= 156.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திரம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திரம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 156.666 && moonvalue <= 160)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திரம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திரம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 160 && moonvalue <= 163.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அஸ்தம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அஸ்தம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 163.3333 && moonvalue <= 166.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அஸ்தம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அஸ்தம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 166.666 && moonvalue <= 170)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அஸ்தம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அஸ்தம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 170 && moonvalue <= 173.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அஸ்தம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அஸ்தம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 173.3333 && moonvalue <= 176.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சித்திரை , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சித்திரை , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 176.666 && moonvalue <= 180)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சித்திரை , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சித்திரை , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 180 && moonvalue <= 183.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சித்திரை , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சித்திரை , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 183.3333 && moonvalue <= 186.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சித்திரை , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சித்திரை , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 186.666 && moonvalue <= 190)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சுவாதி , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சுவாதி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 190 && moonvalue <= 193.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சுவாதி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சுவாதி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 193.3333 && moonvalue <= 196.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சுவாதி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சுவாதி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 196.666 && moonvalue <= 200)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சுவாதி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சுவாதி , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 200 && moonvalue <= 203.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: விசாகம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" விசாகம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 203.3333 && moonvalue <= 206.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: விசாகம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" விசாகம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 206.666 && moonvalue <= 210)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: விசாகம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" விசாகம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 210 && moonvalue <= 213.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: விசாகம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" விசாகம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 213.3333 && moonvalue <= 216.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அனுஷம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அனுஷம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 216.666 && moonvalue <= 220)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அனுஷம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அனுஷம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 220 && moonvalue <= 223.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அனுஷம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அனுஷம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 223.3333 && moonvalue <= 226.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அனுஷம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அனுஷம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 226.666 && moonvalue <= 230)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கேட்டை , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கேட்டை , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 230 && moonvalue <= 233.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கேட்டை , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கேட்டை , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 233.3333 && moonvalue <= 236.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கேட்டை , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கேட்டை , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 236.666 && moonvalue <= 240)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: கேட்டை , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" கேட்டை , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 240 && moonvalue <= 243.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மூலம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மூலம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 243.3333 && moonvalue <= 246.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மூலம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மூலம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 246.666 && moonvalue <= 250)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மூலம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மூலம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 250 && moonvalue <= 253.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: மூலம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" மூலம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 253.3333 && moonvalue <= 256.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூராடம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூராடம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 256.666 && moonvalue <= 260)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூராடம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூராடம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 260 && moonvalue <= 263.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூராடம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூராடம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 263.3333 && moonvalue <= 266.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூராடம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூராடம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 266.666 && moonvalue <= 270)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திராடம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திராடம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 270 && moonvalue <= 273.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திராடம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திராடம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 273.3333 && moonvalue <= 276.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திராடம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திராடம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 276.666 && moonvalue <= 280)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்திராடம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்திராடம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 280 && moonvalue <= 283.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவோணம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவோணம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 283.3333 && moonvalue <= 286.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவோணம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவோணம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 286.666 && moonvalue <= 290)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவோணம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவோணம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 290 && moonvalue <= 293.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: திருவோணம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" திருவோணம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 293.3333 && moonvalue <= 296.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அவிட்டம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அவிட்டம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 296.666 && moonvalue <= 300)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அவிட்டம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அவிட்டம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 300 && moonvalue <= 303.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அவிட்டம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அவிட்டம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 303.3333 && moonvalue <= 306.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: அவிட்டம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" அவிட்டம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 306.666 && moonvalue <= 310)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சதயம் , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சதயம் , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 310 && moonvalue <= 313.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சதயம் , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சதயம் , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 313.3333 && moonvalue <= 316.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சதயம் , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சதயம் , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 316.666 && moonvalue <= 320)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: சதயம் , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" சதயம் , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 320 && moonvalue <= 323.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரட்டாதி , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரட்டாதி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 323.3333 && moonvalue <= 326.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரட்டாதி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரட்டாதி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 326.666 && moonvalue <= 330)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரட்டாதி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரட்டாதி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 330 && moonvalue <= 333.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: பூரட்டாதி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" பூரட்டாதி , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 333.3333 && moonvalue <= 336.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்ரட்டாதி , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்ரட்டாதி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 336.666 && moonvalue <= 340)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்ரட்டாதி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்ரட்டாதி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 340 && moonvalue <= 343.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்ரட்டாதி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்ரட்டாதி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 343.3333 && moonvalue <= 346.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: உத்ரட்டாதி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" உத்ரட்டாதி , பாதம்: 4" + "</p>";
                    }
                    else if(moonvalue > 346.666 && moonvalue <= 350)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரேவதி , பாதம்: 1",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரேவதி , பாதம்: 1" + "</p>";
                    }
                    else if(moonvalue > 350 && moonvalue <= 353.3333)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரேவதி , பாதம்: 2",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரேவதி , பாதம்: 2" + "</p>";
                    }
                    else if(moonvalue > 353.3333 && moonvalue <= 356.666)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரேவதி , பாதம்: 3",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரேவதி , பாதம்: 3" + "</p>";
                    }
                    else if(moonvalue > 356.666 && moonvalue <= 360)
                    {
                        Toast.makeText(getApplicationContext(),"நட்சத்திரம்: ரேவதி , பாதம்: 4",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:aqua;background:teal;'> நட்சத்திரம்:" +" ரேவதி , பாதம்: 4" + "</p>";
                    }


                    moonvalue = (float) (moonvalue / 13.3333);
                    //Star Birthday Return Calculation

                    html = html + "<p> Star Birthday up to 2020</p>";
                    html = html + "<table style=\"width:100%\"><tr><th>Star Birthday</th><th>Star START TIME</th><th>Star END TIME</th></tr>";

                    if(Float.parseFloat(sun) > 0 && Float.parseFloat(sun) < 30)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 0 AND sun < 30 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));

                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 30 && Float.parseFloat(sun) < 60)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 30 AND sun < 60 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 60 && Float.parseFloat(sun) < 90)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 60 AND sun < 90 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 90 && Float.parseFloat(sun) < 120)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 90 AND sun < 120 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 120 && Float.parseFloat(sun) < 150)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 120 AND sun < 150 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 150 && Float.parseFloat(sun) < 180)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 150 AND sun < 180 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 180 && Float.parseFloat(sun) < 210)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 180 AND sun < 210 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 210 && Float.parseFloat(sun) < 240)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 210 AND sun < 240 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 240 && Float.parseFloat(sun) < 270)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 240 AND sun < 270 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 270 && Float.parseFloat(sun) < 300)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 270 AND sun < 300 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 300 && Float.parseFloat(sun) < 330)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 300 AND sun < 330 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }

                    if(Float.parseFloat(sun) > 330 && Float.parseFloat(sun) < 360)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 330 AND sun < 360 AND starfrom < " + moonvalue + " AND starto > " + moonvalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float starfrom1 = c3.getFloat((c3.getColumnIndex("starfrom")));
                                Float starto1 = c3.getFloat((c3.getColumnIndex("starto")));


                                int addMins = (int) ((((moonvalue - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " 05:30";
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                String date11;
                                SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                Calendar calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<tr><td>"+date11+"</td>";

                                float realTime1 = Float.parseFloat(time.replace(":","."));

                                int a = (int) moonvalue;
                                int b = a +1;

                                addMins = (int) ((((a - starfrom1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());
                                html = html + "<td>"+date11+"</td>";

                                realTime1 = Float.parseFloat(time.replace(":","."));

                                addMins = (int) ((((b - starto1) / (starto1 - starfrom1)) * 24) * 60);

                                eDate2 = eDate2 + " " + time;
                                //float realTime1 = Float.parseFloat(time.replace(":","."));
                                sdf0 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                calendar1 = Calendar.getInstance();
                                try {
                                    calendar1.setTime(sdf0.parse(eDate2));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                calendar1.add(Calendar.DATE, 1);
                                calendar1.add(Calendar.MINUTE, addMins);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                sdf2 = new SimpleDateFormat("yyyy-MM-dd kk:mm");
                                date11 = sdf2.format(calendar1.getTime());

                                html = html + "<td>"+date11+"</td></tr>";
                                realTime1 = Float.parseFloat(time.replace(":","."));





                            } while (c3.moveToNext());
                        }

                    }


                    html = html + "</table>";
                    html = html + "<p style='color:teal;font-size:11;'> குறிப்பு . திருக்கணித முறைப்படி கணிக்கப்பட்டது</p> " ;
                    html = html + "<p style='color:gray;font-size:11 ;'> *Note:வருடாந்திர நட்சத்திரம் இரண்டு முறை வநதால் இரண்டாவதாக வரும் நட்சத்திரம்யைத்தான் எடுத்துக்கொள்ளவேண்டும்</p> " ;
                    html = html + "<p style='color:gray;font-size:11 ;'> சூரியஉதயத்தின் பொழுது என்ன நட்சத்திரம் வருகின்றதோ  அந்த நட்சத்திரம்தான் அன்றையநாள் முழவதற்கும் . எனவே நட்சத்திர  தேதியை அதற்கு ஏற்றவாறு தேர்ந்தெடுத்துக்கொள்ளவும்</p> " ;


                    Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                    intent.putExtra("aKey", html);
                    startActivity(intent);
                }

            }
        });



        Button btnCalcStarBirthdaySave = (Button) findViewById(R.id.btnCalcStarBirthdaySave);
        btnCalcStarBirthdaySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = edtxtDate.getText().toString();
                String time = edtxtTime.getText().toString();
                EditText edtxtName = (EditText) findViewById(R.id.edtxtName);
                String name = edtxtName.getText().toString();
                if(date.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Enter Date",Toast.LENGTH_LONG).show();
                }
                else if(time.length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Enter Time",Toast.LENGTH_LONG).show();
                }
                else if(name.trim().length() == 0)
                {
                    Toast.makeText(getApplicationContext(),"Enter Name",Toast.LENGTH_LONG).show();
                }
                else
                {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layoutAutoComplete);
                    layout.setVisibility(View.VISIBLE);

                    DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
                    Details details = new Details(name,date,time.replace(":","."));
                    dbHelper.createStar(details);
                    List<Details> allSavedStarBirthday = dbHelper.getAllStar();
                    savedValues = new String[allSavedStarBirthday.size()];
                    for(int i = 0; i < allSavedStarBirthday.size(); i++)
                    {
                        savedValues[i] = allSavedStarBirthday.get(i).getName();
                    }
                    dbHelper.close();

                    if(adapter != null)
                        adapter.notifyDataSetChanged();

                    adapter = new ArrayAdapter(StarBirthDayCalculationActivity.this,android.R.layout.simple_list_item_1,savedValues);
                    actxtName.setAdapter(adapter);
                }


            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v == edtxtDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            String date;
                            String month;

                            if(String.valueOf(dayOfMonth).length() == 1)
                            {
                                date = "0" + dayOfMonth;
                            }
                            else
                            {
                                date = String.valueOf(dayOfMonth);
                            }

                            if(String.valueOf(monthOfYear+1).length() == 1)
                            {
                                month = "0" + (monthOfYear+1);
                            }
                            else
                            {
                                month = String.valueOf(monthOfYear+1);
                            }

                            edtxtDate.setText(year+"-"+month+"-"+date);


                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if (v == edtxtTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            String hour;
                            String mins;

                            if(String.valueOf(hourOfDay).length() == 1)
                            {
                                hour = "0" + hourOfDay;
                            }
                            else
                            {
                                hour = String.valueOf(hourOfDay);
                            }

                            if(String.valueOf(minute).length() == 1)
                            {
                                mins = "0" + (minute);
                            }
                            else
                            {
                                mins = String.valueOf(minute);
                            }

                            edtxtTime.setText(hour + ":" + mins);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }


    }
}
