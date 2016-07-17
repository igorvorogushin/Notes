package com.vorogushinigor.notes.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vorogushinigor.notes.R;
import com.vorogushinigor.notes.fragment.FragmentShow;

/**
 * Created by viv on 14.07.2016.
 */
public class ActivityShow extends AppCompatActivity {
    private static final String TAG_FRAGMENT = FragmentShow.class.getName();
    private FragmentTransaction mFragmentTransaction;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.activity_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_backspace_white_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fragment = getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment == null) {
            fragment = new FragmentShow();
            fragment.setRetainInstance(true);
        }
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.activity_framelayout, fragment, TAG_FRAGMENT);
        mFragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_delete:
                ((FragmentShow) fragment).menu_items_delete();
                break;
            case R.id.menu_show_edit:
                ((FragmentShow) fragment).menu_items_edit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ((FragmentShow) fragment).onBackPressed();
    }
}
