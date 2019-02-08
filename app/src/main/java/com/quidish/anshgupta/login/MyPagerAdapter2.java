package com.quidish.anshgupta.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MyPagerAdapter2 extends FragmentStatePagerAdapter {
    MyPagerAdapter2(FragmentManager fm){
        super(fm);
    }
    @Override    public Fragment getItem(int position) {
        switch (position){
            case 0: return new activefrag();
            case 1: return new inactivefrag();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override    public CharSequence getPageTitle(int position) {        switch (position){
        case 0: return "ACTIVE ADS";
        case 1: return "INACTIVE ADS";
        default: return null;
    }
    }
}
