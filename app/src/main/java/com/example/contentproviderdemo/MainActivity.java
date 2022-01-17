package com.example.contentproviderdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MyObserver myObserver;
    private EditText currentName;
    private EditText newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getContentResolver().registerContentObserver(EmployeesProvider.CONTENT_URI, true, myObserver);
        myObserver = new MyObserver(new Handler());
        currentName = findViewById(R.id.current_name);
        newName = findViewById(R.id.new_name);
    }

    public void onClickAddDetails(View view) {
        ContentValues values = new ContentValues();
        values.put(EmployeesProvider.name, ((EditText) findViewById(R.id.txtName)).getText().toString());
        getContentResolver().insert(EmployeesProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), "New Record Inserted", Toast.LENGTH_LONG).show();
    }

    public void update(View view) {
        String oldName = currentName.getText().toString();
        String newName1 = newName.getText().toString();
        if (oldName.isEmpty() || newName1.isEmpty()) {
            Toast.makeText(this, "Enter data", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(EmployeesProvider.name, newName1 );
//            String[] whereArgs = {u1};
            getContentResolver().update(EmployeesProvider.CONTENT_URI, values, oldName, null);
            Toast.makeText(this, "Updation Successfull", Toast.LENGTH_SHORT).show();
            currentName.setText("");
            newName.setText("");
        }
    }

    @SuppressLint("Range")
    public void onClickShowDetails(View view) {
      showEmployeeName();
    }

    @SuppressLint("Range")
    private void showEmployeeName() {
        // Retrieve employee records
        TextView resultView= findViewById(R.id.res);
        Cursor cursor = getContentResolver().query(Uri.parse("content://com.example.contentproviderdemo.EmployeesProvider/users"), null, null, null, null);
        if(cursor.moveToFirst()) {
            StringBuilder strBuild=new StringBuilder();
            while (!cursor.isAfterLast()) {
                strBuild.append("\n"+cursor.getString(cursor.getColumnIndex("id"))+ "-"+ cursor.getString(cursor.getColumnIndex("name")));
                cursor.moveToNext();
            }
            resultView.setText(strBuild);
        }
        else {
            resultView.setText("No Records Found");
        }
    }
̀̀̀
    @SuppressLint("NewApi")
    public class MyObserver extends ContentObserver {
        public MyObserver(Handler handler) {
            super(handler);
        }
        @SuppressLint("Range")
        @Override
        public void onChange(boolean selfChange) {
            Log.e("observer triggered", "values updated");
            showEmployeeName();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(myObserver);
    }
}