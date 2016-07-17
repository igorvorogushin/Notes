package com.vorogushinigor.notes.activity;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vorogushinigor.notes.R;
import com.vorogushinigor.notes.fragment.FragmentMain;

/**
 * Created by viv on 14.07.2016.
 */
public class ActivityMain extends AppCompatActivity {
    private static final String TAG_FRAGMENT =FragmentMain.class.getName();
    private static final int INT_PERMISSIONS_READ_STORAGE=1;
    private FragmentTransaction mFragmentTransaction;
    private Fragment fragment;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == INT_PERMISSIONS_READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.permission_storage), Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast toast = Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.permission_storage), Toast.LENGTH_LONG);
                    toast.show();
                }

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, INT_PERMISSIONS_READ_STORAGE);
            }
        } else {
            init();
        }
    }


    private void init(){
        setContentView(R.layout.activity_toolbar);
        Toolbar toolbar =(Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
       fragment = getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment == null) {
            fragment = new FragmentMain();
            fragment.setRetainInstance(true);
        }
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.activity_framelayout, fragment, TAG_FRAGMENT);
        mFragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_delete_all:
                ((FragmentMain)fragment).deleteAll();
                break;
            case R.id.menu_main_add:
                ((FragmentMain)fragment).startIntentAddNotes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
