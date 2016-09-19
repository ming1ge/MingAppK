package com.study.mingappk.tab4.shop.shoptab1.books;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.TextView;

import com.bilibili.magicasakura.widgets.TintToolbar;
import com.study.mingappk.R;
import com.study.mingappk.tab4.shop.shoptab1.express.ExpressSendFragment;
import com.study.mingappk.tmain.baseactivity.BaseActivity;
import com.study.mingappk.tmain.baseactivity.TabsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BooksActivity extends TabsActivity {

    public static String EXPRESS_STATUS = "express_status";//0为待寄快递；1为已寄快递
    public static final int REQUEST_CODE = 1231;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setToolbarTitle(R.string.title_activity_express_send);

        // 初始化mTitles、mFragments等ViewPager需要的数据
        initData();
    }

    private void initData() {
        // Tab的标题
        mTitles = new String[]{"待寄快递", "已寄快递"};

        //初始化填充到ViewPager中的Fragment集合
        mFragments.add(0, new ExpressSendFragment());
        mFragments.add(1, new ExpressSendFragment());

        //将标记值传给fragment，0为待寄快递；1为已寄快递
        for (int i = 0; i < mFragments.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXPRESS_STATUS, i);
            mFragments.get(i).setArguments(bundle);
        }
        mAdapter.setItem(mTitles, mFragments);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                for (int i = 0; i < mFragments.size(); i++) {
                    ((ExpressSendFragment) mFragments.get(i)).initData();
                }
            }
        }
    }
}