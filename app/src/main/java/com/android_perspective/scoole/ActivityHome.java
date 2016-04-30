package com.android_perspective.scoole;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ActivityHome extends FragmentActivity {

    private static final int NUM_PAGES = 3;

    private static final int CALENDAR_POSITION = 0;

    private static final int ATTENDANCE_POSITION = 1;

    private static final int CHATREQ_POSITION = 2;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private TextView attendanceLabel;

    private TextView calendarLabel;

    private TextView chatreqLabel;

    private EditText getIdentifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        //getIdentifier = (EditText)findViewById(R.id.getidentifier);
        /*getIdentifier.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE && v.getText() != null)
                {
                    startChatListeningService();
                }
                return true;
            }
        });*/

        calendarLabel = (TextView) findViewById(R.id.calendarlabel);
        attendanceLabel = (TextView) findViewById(R.id.attendancelabel);
        chatreqLabel = (TextView) findViewById(R.id.chatreqlabel);

        mPager = (ViewPager) findViewById(R.id.homePager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(ATTENDANCE_POSITION);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                highlightCorrectLabel();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        highlightCorrectLabel();


        calendarLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(CALENDAR_POSITION);
            }
        });

        attendanceLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(ATTENDANCE_POSITION);
            }
        });

        chatreqLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(CHATREQ_POSITION);
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
    /*private void startChatListeningService()
    {
        Toast.makeText(this,"Starting Service",Toast.LENGTH_LONG).show();
        Intent chatServiceIntent = new Intent(this,ChatListeningService.class);
        chatServiceIntent.putExtra("ClientIdentifier",Integer.parseInt(getIdentifier.getText().toString()));
        chatServiceIntent.putExtra("Message","NULL");
        startService(chatServiceIntent);
    }*/
    private void highlightCorrectLabel()
    {
        switch(mPager.getCurrentItem())
        {
            case CALENDAR_POSITION:
            {
                calendarLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                attendanceLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                chatreqLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                calendarLabel.setTextColor(getResources().getColor(R.color.colorAccent));
                attendanceLabel.setTextColor(getResources().getColor(R.color.white));
                chatreqLabel.setTextColor(getResources().getColor(R.color.white));

            };break;
            case ATTENDANCE_POSITION:
            {
                calendarLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                attendanceLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                chatreqLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                calendarLabel.setTextColor(getResources().getColor(R.color.white));
                attendanceLabel.setTextColor(getResources().getColor(R.color.colorAccent));
                chatreqLabel.setTextColor(getResources().getColor(R.color.white));
            };break;
            case CHATREQ_POSITION:
            {
                calendarLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                attendanceLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                chatreqLabel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                calendarLabel.setTextColor(getResources().getColor(R.color.white));
                attendanceLabel.setTextColor(getResources().getColor(R.color.white));
                chatreqLabel.setTextColor(getResources().getColor(R.color.colorAccent));
            };break;
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return new FragmentCalendar();
                case 1: return new FragmentAttendance();
                case 2: return new FragmentChatreq();
            }
            return new FragmentAttendance();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
