package com.example.examcountdown;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    BufferedReader reader;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm");
    ArrayList<Exam> exams = new ArrayList<>();
    ArrayList<Exam> curr = new ArrayList<>();
    TextView textView;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        readData();

        Button button_search = findViewById(R.id.button_search);
        Button button_clear = findViewById(R.id.button_clear);
        final EditText search = findViewById(R.id.editText);

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean found = false;
                String code = search.getText().toString().trim().toUpperCase();

                for (int i = 0; i < exams.size(); i++) {
                    final Exam temp = exams.get(i);
                    if (temp.getCode().equals(code)) {
                        // Found
                        found = true;
                        if (t != null ) t.interrupt();
                        curr.add(temp);
                        final int j = i;
                        t = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (!isInterrupted()) {
                                        Thread.sleep(1000);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateTextView();
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                }
                            }
                        };
                        t.start();
                        break;
                    }
                }
                if (!found) {
                    if (t != null) t.interrupt();
                    textView.setText("You have entered an invalid module,\nor the module does not have an exam component!");
                }
            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t != null) t.interrupt();
                curr.clear();
                textView.setText("");
            }
        });
    }

    private void updateTextView() {
        Date now = new Date();
        String str = "";
        for (Exam e : curr) {
            str += "<b>"  + e.toString() + "</b><br />" + "Exam: " + format.format(e.getDateTime()) + "<br />" + getDiff(now, e.getDateTime())+ " remaining" + "<br /><br />";
        }
        textView.setText(Html.fromHtml(str));
    }

    public String getDiff(Date date1, Date date2) {
        long diff = date2.getTime() - date1.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

       return diffDays + " days, " + diffHours + " hours, " + diffMinutes + " minutes, " + diffSeconds + " seconds";
    }

    private void readData() {
        textView = findViewById(R.id.textView);
        InputStream is = this.getResources().openRawResource(R.raw.data3);
        reader = new BufferedReader(new InputStreamReader(is));

        try {
            String data = reader.readLine();
            while(data != null) {
                String[] temp = data.split(",");
                Exam test = new Exam(format.parse(temp[0]), temp[1], temp[2]);
                exams.add(test);
                data = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
