package com.three.fileselector;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.three.fileselector.adapter.ViewPagerAdapter;
import com.three.fileselector.fragment.FileListFragment;

public class FileSelectorActivity extends AppCompatActivity {

    public static final int SELECTED_REQUEST_CODE = 99;
    public static final String SELECTED_RESULT = "com.three.file.selected.result";
    private TabLayout mTabLayout;
    private ViewPager viewPager;
    private String[] titles = new String[]{"word","xls","txt","ppt","pdf","zip/rar"};
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_file_selector);
            initView();
            initFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mTabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewpager);
    }

    private void initFragment() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.setTitle(titles);
        size = titles.length;
        for (int i = 0; i < size; i++) {
            FileListFragment fileListFragment = FileListFragment.newInstance(i,true,titles[i]);
            adapter.addFragment(fileListFragment);
        }
        viewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(size - 1);
    }

}
