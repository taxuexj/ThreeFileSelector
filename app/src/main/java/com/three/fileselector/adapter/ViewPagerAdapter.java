package com.three.fileselector.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页适配器
 * @author lqd 2018-08-08
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

	private List<Fragment> list;
	private String[] titles;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		list = new ArrayList<Fragment>();
	}
	
	public void addFragment(Fragment fragment) {
		list.add(fragment);
	}
	
	public void clear() {
		list.clear();
	}

	public void setTitle(String[] strings) {
		this.titles = strings;
	}
	
	public String[] getTitles() {
		return titles;
	}

	public Fragment getItem(int pos) {
		return list == null ? null : list.get(pos);
	}

	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

}
