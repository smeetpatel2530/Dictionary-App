package com.smeet.dictionary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    DatabaseHelper myDbHelper;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        textView =findViewById(R.id.privacypolicy);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://eager-murdock-4c0cfc.netlify.app/";
                Intent openurl = new Intent(Intent.ACTION_VIEW);
                openurl.setData(Uri.parse(url));
                startActivity(openurl);
            }
        });

        TextView clearHistory = (TextView)findViewById(R.id.clear_history);
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDbHelper = new DatabaseHelper(SettingsActivity.this);
                try {
                    myDbHelper.openDatabase();

                }catch (SQLException e){
                    e.printStackTrace();
                }
                showAlertDialog();
            }
        });

    }

    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this,R.style.MyDialolTheme);
        builder.setTitle("Are you sure?");
        builder.setMessage("All the history will be deleted");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDbHelper.deleteHistory();
                    }
                });
        String negativeText = "No";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog dialog  = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}