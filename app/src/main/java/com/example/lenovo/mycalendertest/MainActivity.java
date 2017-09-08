package com.example.lenovo.mycalendertest;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ldf.calendar.component.CalendarAttr;
import com.ldf.calendar.component.CalendarViewAdapter;
import com.ldf.calendar.interf.OnSelectDateListener;
import com.ldf.calendar.model.CalendarDate;
import com.ldf.calendar.view.Calendar;
import com.ldf.calendar.view.MonthPager;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private MyMonthPager monthPager;
    private TextView textViewYearDisplay;
    private TextView textViewMonthDisplay;
    private TextView backToday;

    private CalendarViewAdapter calendarAdapter;
    private OnSelectDateListener onSelectDateListener;
    private ArrayList<Calendar> currentCalendars = new ArrayList<>();

    private boolean initiated = false;
    private CalendarDate currentDate;
    private int mCurrentPage = MonthPager.CURRENT_DAY_INDEX;

    private CustomDayView customDayView;

    private boolean isCurrentMonth = true;
    private boolean isNextYearCurrentMonth = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        monthPager = (MyMonthPager) this.findViewById(R.id.calendar_view);
        textViewYearDisplay = (TextView) findViewById(R.id.show_year_view);
        textViewMonthDisplay = (TextView) findViewById(R.id.show_month_view);
        backToday = ((TextView) this.findViewById(R.id.back_today_button));

        //初始化默认
        currentDate = new CalendarDate();
        textViewYearDisplay.setText(currentDate.getYear() + "年");
        textViewMonthDisplay.setText(currentDate.getMonth() + "月");
        backToday.setBackgroundResource(0);//"字体今天"背景设置1
        monthPager.setScrollble(1, false);

        initCalendarView();

        //回到今天
        backToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarDate today = new CalendarDate();
                calendarAdapter.notifyDataChanged(today);
                textViewYearDisplay.setText(today.getYear() + "年");
                textViewMonthDisplay.setText(today.getMonth() + "月");
                backToday.setBackgroundResource(0);//"字体今天"背景设置2
                monthPager.setScrollble(1, false);
            }
        });
    }

    private void initCalendarView() {
        initListener();
        customDayView = new CustomDayView(this, R.layout.custom_day);
        calendarAdapter = new CalendarViewAdapter(
                this,
                onSelectDateListener,
                CalendarAttr.CalendayType.MONTH,//显示一个月
                customDayView);
        initMarkData();
        initMonthPager();
    }


    /**
     * 初始化monthPager，MonthPager继承自ViewPager
     *
     * @return void
     */
    private void initMonthPager() {
        monthPager.setAdapter(calendarAdapter);
        monthPager.setCurrentItem(MonthPager.CURRENT_DAY_INDEX);
        monthPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                position = (float) Math.sqrt(1 - Math.abs(position));
                page.setAlpha(position);
            }
        });

        monthPager.addOnPageChangeListener(new MonthPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                currentCalendars = calendarAdapter.getPagers();
                if (currentCalendars.get(position % currentCalendars.size()) instanceof Calendar) {
                    CalendarDate date = currentCalendars.get(position % currentCalendars.size()).getSeedDate();
                    currentDate = date;
                    textViewYearDisplay.setText(date.getYear() + "年");
                    textViewMonthDisplay.setText(date.getMonth() + "月");


                    //本年本月
                    CalendarDate today = new CalendarDate();
                    if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth()) {
                        monthPager.setScrollble(1, false);
                        Log.e("666", "=== 本月===");
                        isCurrentMonth = true;
                        isNextYearCurrentMonth = false;
                        //下年的本月
                    } else if (date.getYear() == today.getYear() + 1 && date.getMonth() == today.getMonth()) {
                        monthPager.setScrollble(-1, false);
                        Log.e("666", "=== 下年本月===");
                        isCurrentMonth = false;
                        isNextYearCurrentMonth = true;
                        //其他月
                    } else {
                        monthPager.setScrollble(0, true);
                        Log.e("666", "=== 其他月 ===");
                        isCurrentMonth = false;
                        isNextYearCurrentMonth = false;
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initListener() {
        onSelectDateListener = new OnSelectDateListener() {
            @Override
            public void onSelectDate(CalendarDate date) {

                //"字体今天"背景设置3
                CalendarDate today = new CalendarDate();
                if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDay() == today.getDay()) {
                    backToday.setBackgroundResource(0);
                } else {
                    backToday.setBackgroundResource(R.drawable.button_bg);
                }


                currentDate = date;
                textViewYearDisplay.setText(date.getYear() + "年");
                textViewMonthDisplay.setText(date.getMonth() + "月");
            }

            @Override
            public void onSelectOtherMonth(int offset) {
                Log.e("666", "=== offset ===" + offset);
                //偏移量 -1表示刷新成上一个月数据 ， 1表示刷新成下一个月数据
                if (!isCurrentMonth && !isNextYearCurrentMonth) {
                    monthPager.selectOtherMonth(offset);
                    Toast.makeText(MainActivity.this, "哈哈，我是本月和下年本月之间的月份", Toast.LENGTH_SHORT).show();
                } else if (isCurrentMonth && offset == 1) {
                    monthPager.selectOtherMonth(1);
                    Toast.makeText(MainActivity.this, "呵呵，我是本月", Toast.LENGTH_SHORT).show();
                } else if (isNextYearCurrentMonth && offset == (-1)) {
                    monthPager.selectOtherMonth(-1);
                    Toast.makeText(MainActivity.this, "嘻嘻，我是下年本月", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * 初始化标记数据，HashMap的形式，可自定义
     *
     * @return void
     */
    private void initMarkData() {
        HashMap<String, String> markData = new HashMap<>();
        markData.put("2017-9-1", "1");
        markData.put("2017-9-2", "0");
        markData.put("2017-9-3", "1");
        markData.put("2017-9-4", "0");
        calendarAdapter.setMarkData(markData);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !initiated) {
            CalendarDate today = new CalendarDate();
            calendarAdapter.notifyDataChanged(today);
            initiated = true;
        }
    }

}
