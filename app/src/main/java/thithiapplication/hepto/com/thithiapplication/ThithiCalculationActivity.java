package thithiapplication.hepto.com.thithiapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class ThithiCalculationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtxtDate, edtxtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String[] savedValues;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thithicalculation);

        edtxtDate = (EditText)findViewById(R.id.edtxtDate);
        edtxtTime = (EditText)findViewById(R.id.edtxtTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
        String currentDateandTime = sdf.format(new Date());
        String[] cDT = currentDateandTime.split(" ");
        edtxtDate.setText(cDT[0]);
        edtxtTime.setText(cDT[1]);

        final AutoCompleteTextView actxtName = (AutoCompleteTextView) findViewById(R.id.actxtName);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        List<Details> allSavedThithi = dbHelper.getAllThithi();
        dbHelper.close();

        if(allSavedThithi != null && allSavedThithi.size() != 0) {

            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutAutoComplete);
            layout.setVisibility(View.VISIBLE);

            savedValues = new String[allSavedThithi.size()];
            for (int i = 0; i < allSavedThithi.size(); i++) {
                savedValues[i] = allSavedThithi.get(i).getName();
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
                List<Details> allSavedThithi = dbHelper.getAllThithi();
                EditText edtxtName = (EditText) findViewById(R.id.edtxtName);
                edtxtName.setText(actxtName.getText().toString());

                edtxtDate.setText(allSavedThithi.get(pos).getDate());
                edtxtTime.setText(allSavedThithi.get(pos).getTime().replace(".",":"));


            }
        });


        edtxtDate.setOnClickListener(this);
        edtxtTime.setOnClickListener(this);

        Button btnCalcThithi = (Button) findViewById(R.id.btnCalcThithi);
        btnCalcThithi.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(getApplicationContext(),"Enter Date Between 1951 to 2020",Toast.LENGTH_LONG).show();
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
                    float thithivalue;
                    if(Float.parseFloat(moon) > Float.parseFloat(sun) )
                    {
                        thithivalue = (Float.parseFloat(moon) - Float.parseFloat(sun)) / 12;
                    }
                    else
                    {
                        thithivalue = ((360 + Float.parseFloat(moon)) - Float.parseFloat(sun)) / 12;
                    }

                    if(thithivalue > 0 && thithivalue <= 1)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச பிரதமை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;' > திதி:" + "சுக்லபட்ச பிரதமை" + "</p>";
                    }
                    else if(thithivalue > 1 && thithivalue <= 2)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச துதியை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> திதி:" + "சுக்லபட்ச துதியை" + "</p>";
                    }
                    else if(thithivalue > 2 && thithivalue <= 3)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச திருதியை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச திருதியை" + "</p>";
                    }
                    else if(thithivalue > 3 && thithivalue <= 4)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச சதுர்த்தி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச சதுர்த்தி" + "</p>";
                    }
                    else if(thithivalue > 4 && thithivalue <= 5)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச பஞ்சமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச பஞ்சமி" + "</p>";
                    }
                    else if(thithivalue > 5 && thithivalue <= 6)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச சஷ்டி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச சஷ்டி" + "</p>";
                    }
                    else if(thithivalue > 6 && thithivalue <= 7)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச சப்தமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச சப்தமி" + "</p>";
                    }
                    else if(thithivalue > 7 && thithivalue <= 8)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச அஷ்டமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச அஷ்டமி" + "</p>";
                    }
                    else if(thithivalue > 8 && thithivalue <= 9)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச நவமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "ுக்லபட்ச நவமி" + "</p>";
                    }
                    else if(thithivalue > 9 && thithivalue <= 10)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச தசமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச தசமி" + "</p>";
                    }
                    else if(thithivalue > 10 && thithivalue <= 11)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச எகாதசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச எகாதசி" + "</p>";
                    }
                    else if(thithivalue > 11 && thithivalue <= 12)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச துவாதசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச துவாதசி" + "</p>";
                    }
                    else if(thithivalue > 12 && thithivalue <= 13)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச திரயோதசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச திரயோதசி" + "</p>";
                    }
                    else if(thithivalue > 13 && thithivalue <= 14)
                    {
                        Toast.makeText(getApplicationContext(),"சுக்லபட்ச சதுர்த்தசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "சுக்லபட்ச சதுர்த்தசி" + "</p>";
                    }
                    else if(thithivalue > 14 && thithivalue <= 15)
                    {
                        Toast.makeText(getApplicationContext(),"பெளர்ணமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "பெளர்ணமி" + "</p>";
                    }
                    else if(thithivalue > 15 && thithivalue <= 16)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச பிரதமை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச பிரதமை" + "</p>";
                    }
                    else if(thithivalue > 16 && thithivalue <= 17)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச துதியை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச துதியை" + "</p>";
                    }
                    else if(thithivalue > 17 && thithivalue <= 18)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச திருதியை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச திருதியை" + "</p>";
                    }
                    else if(thithivalue > 18 && thithivalue <= 19)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச சதுர்த்தி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச சதுர்த்தி" + "</p>";
                    }
                    else if(thithivalue > 19 && thithivalue <= 20)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச பஞ்சமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச பஞ்சமி" + "</p>";
                    }
                    else if(thithivalue > 20 && thithivalue <= 21)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச சஷ்டி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச சஷ்டி" + "</p>";
                    }
                    else if(thithivalue > 21 && thithivalue <= 22)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச சப்தமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச சப்தமி" + "</p>";
                    }
                    else if(thithivalue > 22 && thithivalue <= 23)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச அஷ்டமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச அஷ்டமி" + "</p>";
                    }
                    else if(thithivalue > 23 && thithivalue <= 24)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச நவமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச நவமி" + "</p>";
                    }
                    else if(thithivalue > 24 && thithivalue <= 25)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச தசமி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச தசமி" + "</p>";
                    }
                    else if(thithivalue > 25 && thithivalue <= 26)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச ஏகாதசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச ஏகாதசி" + "</p>";
                    }
                    else if(thithivalue > 26 && thithivalue <= 27)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச துவாதசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச துவாதசி" + "</p>";
                    }
                    else if(thithivalue > 27 && thithivalue <= 28)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச திரயோதசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச திரயோதசி\"" + "</p>";
                    }
                    else if(thithivalue > 28 && thithivalue <= 29)
                    {
                        Toast.makeText(getApplicationContext(),"கிருஷ்ணபட்ச சதுர்த்தசி",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "கிருஷ்ணபட்ச சதுர்த்தசி" + "</p>";
                    }
                    else if(thithivalue > 29 && thithivalue <= 30)
                    {
                        Toast.makeText(getApplicationContext(),"அமாவாசை",Toast.LENGTH_LONG).show();
                        html = html + "<p style='color:blue;background:lime;'> Thithi:" + "அமாவாசை" + "</p>";

                    }

                    html = html + "<p> திதிup to 2020</p>";
                    html = html + "<table style=\"width:100%\"><tr><th>திதிReturn</th><th>திதி ஆரம்பம்</th><th>திதி   முடிவு</th></tr>";

                    if(Float.parseFloat(sun) > 0 && Float.parseFloat(sun) < 30)
                    {

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 0 AND sun < 30 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 30 AND sun < 60 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 60 AND sun < 90 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                         selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 90 AND sun < 120 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 120 AND sun < 150 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 150 AND sun < 180 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 180 AND sun < 210 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 210 AND sun < 240 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 240 AND sun < 270 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 270 AND sun < 300 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 300 AND sun < 330 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                        selectQuery = "SELECT  * FROM thithi_star_data_2020 where edate > '" + date2 + "' AND sun > 330 AND sun < 360 AND thithifrom < " + thithivalue + " AND thithito > " + thithivalue;
                        Cursor c3 = db.rawQuery(selectQuery, null);

                        if (c3.moveToFirst()) {
                            do {
                                eDate2 = c3.getString((c3.getColumnIndex("edate")));
                                sunRaise2 = c3.getFloat((c3.getColumnIndex("sunraise")));
                                sun5302 = c3.getFloat((c3.getColumnIndex("sun")));
                                moon5302 = c3.getFloat((c3.getColumnIndex("moon")));

                                Float thithifrom1 = c3.getFloat((c3.getColumnIndex("thithifrom")));
                                Float thithito1 = c3.getFloat((c3.getColumnIndex("thithito")));


                                int addMins = (int) ((((thithivalue - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                int a = (int) thithivalue;
                                int b = a +1;

                                addMins = (int) ((((a - thithifrom1) / (thithito1 - thithifrom1)) * 24) * 60);

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

                                addMins = (int) ((((b - thithito1) / (thithito1 - thithifrom1)) * 24) * 60);

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
                    html = html + "<p style='color:gray;font-size:11 ;'> *Note:வருடாந்திர திதி இரண்டு முறை வந்தால் முதலில் வரும் திதியைத்தான் எடுத்துக்கொள்ளவேண்டும்</p> " ;
                    html = html + "<p style='color:gray;font-size:11 ;'> சூரியஉதயத்தின் பொழுது என்ன திதி வருகின்றதோ அந்த திதிதான் அன்றையநாள் முழவதற்கும் . எனவே திதிகொடுக்கும் தேதியை அதற்கு ஏற்றவாறு தேர்ந்தெடுத்துக்கொள்ளவும்</p> " ;
                    Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                    intent.putExtra("aKey", html);
                    startActivity(intent);


                }

            }
        });



        Button btnCalcThithiSave = (Button) findViewById(R.id.btnCalcThithiSave);
        btnCalcThithiSave.setOnClickListener(new View.OnClickListener() {
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
                    dbHelper.createThithi(details);
                    List<Details> allSavedThithi = dbHelper.getAllThithi();
                    savedValues = new String[allSavedThithi.size()];
                    for(int i = 0; i < allSavedThithi.size(); i++)
                    {
                        savedValues[i] = allSavedThithi.get(i).getName();
                    }
                    dbHelper.close();

                    if(adapter != null)
                        adapter.notifyDataSetChanged();

                    adapter = new ArrayAdapter(ThithiCalculationActivity.this,android.R.layout.simple_list_item_1,savedValues);
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
