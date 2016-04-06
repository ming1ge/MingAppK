package com.study.mingappk.tab3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.study.mingappk.R;
import com.study.mingappk.model.bean.FollowVillageList;
import com.study.mingappk.model.service.MyServiceClient;
import com.study.mingappk.model.bean.Result;
import com.study.mingappk.app.MyApplication;
import com.study.mingappk.common.dialog.Dialog_Model;
import com.study.mingappk.common.utils.MyItemDecoration;
import com.study.mingappk.tab3.addfollow.FollowVillageActivity;
import com.study.mingappk.tab3.village.Tab3BBSListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tab3Fragment extends Fragment implements Tab3Adapter.OnItemClickListener {
    AppCompatActivity mActivity;
    @Bind(R.id.tab3_list)
    XRecyclerView mXRecyclerView;

    private XRecyclerView.LayoutManager mLayoutManager;
    private Tab3Adapter mAdapter;
    List<FollowVillageList.DataEntity.ListEntity> mList;
    private int cnt;//关注村圈数
    final private static int PAGE_SIZE = 20;
    private int nowPage = 2;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_follow) {
            Intent intent = new Intent(mActivity, FollowVillageActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == mActivity.RESULT_OK) {
                    //关注村圈后更新列表
                    String auth = MyApplication.getInstance().getAuth();
                    MyServiceClient.getService().getCall_FollowList(auth, 1, PAGE_SIZE)
                            .enqueue(new Callback<FollowVillageList>() {
                                @Override
                                public void onResponse(Call<FollowVillageList> call, Response<FollowVillageList> response) {
                                    if (response.isSuccessful()) {
                                        FollowVillageList followVillageListResult = response.body();
                                        if (followVillageListResult != null && followVillageListResult.getErr() == 0) {
                                            mList.clear();
                                            mList.addAll(followVillageListResult.getData().getList());
                                            mAdapter.notifyDataSetChanged();
                                            mXRecyclerView.refreshComplete();
                                            nowPage = 2;
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FollowVillageList> call, Throwable t) {
                                }
                            });
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();

        setHasOptionsMenu(true);
        getFollowVillage();//获取followList数据和cnt值
    }

    //配置RecyclerView
    private void configXRecyclerView() {
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mXRecyclerView.setLayoutManager(mLayoutManager);//设置布局管理器
        mXRecyclerView.addItemDecoration(new MyItemDecoration(mActivity, MyItemDecoration.VERTICAL_LIST));//添加分割线
        mXRecyclerView.setHasFixedSize(true);//保持固定的大小,这样会提高RecyclerView的性能
        mXRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画

        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mXRecyclerView.setLaodingMoreProgressStyle(ProgressStyle.BallRotate);
        //  mXRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);//自定义下拉刷新箭头图标

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

                String auth = MyApplication.getInstance().getAuth();
                MyServiceClient.getService().getCall_FollowList(auth, 1, PAGE_SIZE)
                        .enqueue(new Callback<FollowVillageList>() {
                            @Override
                            public void onResponse(Call<FollowVillageList> call, Response<FollowVillageList> response) {
                                if (response.isSuccessful()) {
                                    FollowVillageList followVillageListResult = response.body();
                                    if (followVillageListResult != null && followVillageListResult.getErr() == 0) {
                                        mList.clear();
                                        mList.addAll(followVillageListResult.getData().getList());
                                        mAdapter.notifyDataSetChanged();
                                        mXRecyclerView.refreshComplete();
                                        nowPage = 2;
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<FollowVillageList> call, Throwable t) {
                            }
                        });
            }


            @Override
            public void onLoadMore() {
                int pages = (int) (cnt / PAGE_SIZE + 1);
                if (nowPage <= pages) {
                    String auth = MyApplication.getInstance().getAuth();
                    MyServiceClient.getService().getCall_FollowList(auth, nowPage, PAGE_SIZE)
                            .enqueue(new Callback<FollowVillageList>() {
                                @Override
                                public void onResponse(Call<FollowVillageList> call, Response<FollowVillageList> response) {
                                    if (response.isSuccessful()) {
                                        FollowVillageList followVillageListResult = response.body();
                                        if (followVillageListResult != null && followVillageListResult.getErr() == 0) {
                                            mList.addAll(followVillageListResult.getData().getList());
                                            mAdapter.notifyDataSetChanged();
                                            mXRecyclerView.loadMoreComplete();
                                            nowPage++;
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FollowVillageList> call, Throwable t) {

                                }
                            });

                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            mXRecyclerView.loadMoreComplete();
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void getFollowVillage() {
        String auth = MyApplication.getInstance().getAuth();
        MyServiceClient.getService().getCall_FollowList(auth, 1, PAGE_SIZE)
                .enqueue(new Callback<FollowVillageList>() {
                    @Override
                    public void onResponse(Call<FollowVillageList> call, Response<FollowVillageList> response) {
                        if (response.isSuccessful()) {
                            FollowVillageList followVillageListResult = response.body();
                            if (followVillageListResult != null && followVillageListResult.getErr() == 0) {
                                mList = new ArrayList<>();
                                mList.addAll(followVillageListResult.getData().getList());
                                cnt = Integer.parseInt(followVillageListResult.getData().getCnt());

                                mAdapter = new Tab3Adapter(mActivity, mList);
                                mAdapter.setOnItemClickListener(Tab3Fragment.this);
                                mXRecyclerView.setAdapter(mAdapter);//设置adapter
                                configXRecyclerView();//XRecyclerView配置
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FollowVillageList> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        //点击选项操作
        String vid = mList.get(position).getVillage_id();
        Intent intent=new Intent();
        intent.putExtra("click_vid", vid);
        intent.setClass(mActivity, Tab3BBSListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        //长按选项操作
        LongClick(position);
    }

    /**
     * 长按村，取消关注
     */
    private void LongClick(final int position) {
        String villageName = mList.get(position).getVillage_name();
        Dialog_Model.Builder builder = new Dialog_Model.Builder(mActivity);
        builder.setTitle("提示");
        builder.setMessage("取消关注" + villageName + "?");
        builder.setNegativeButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeFromServer(position);
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (!mActivity.isFinishing()) {
            builder.create().show();
        }
    }

    /**
     * 发送请求，从服务器取消关注村圈
     *
     * @param position 点击项
     */
    private void removeFromServer(final int position) {
        String vid = mList.get(position).getVillage_id();
        String auth = MyApplication.getInstance().getAuth();
        MyServiceClient.getService().getCall_DelFollowList(auth, vid).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    if (result != null && result.getErr() == 0) {
                        // Toast.makeText(mActivity, result.getMsg(), Toast.LENGTH_SHORT).show();
                        mAdapter.notifyItemRemoved(position + 1);
                        mList.remove(position);
                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }
}
