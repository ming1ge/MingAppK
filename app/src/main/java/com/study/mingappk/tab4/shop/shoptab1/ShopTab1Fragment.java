package com.study.mingappk.tab4.shop.shoptab1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.study.mingappk.R;
import com.study.mingappk.tab4.shop.shoptab1.express.ExpressSendActivity;
import com.study.mingappk.tab4.shop.shoptab1.express.ExpressTakeActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 村店维护
 */
public class ShopTab1Fragment extends Fragment {
    AppCompatActivity mActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_tab1, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4, R.id.item_5, R.id.item_6})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_1://快递寄件
                Intent intent1=new Intent(mActivity,ExpressSendActivity.class);
                startActivity(intent1);
                break;
            case R.id.item_2://快递收件
                Intent intent2=new Intent(mActivity,ExpressTakeActivity.class);
                startActivity(intent2);
                break;
            case R.id.item_3:
                Toast.makeText(mActivity, "云想购", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_4:
                Toast.makeText(mActivity, "特产订单", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_5:
                Toast.makeText(mActivity, "话费流量", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_6:
                Toast.makeText(mActivity, "图书租借", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
