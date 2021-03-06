package com.study.mingappk.tab4.applyshoper;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;
import com.study.mingappk.R;
import com.study.mingappk.app.APP;
import com.study.mingappk.model.bean.ApplyInfo2;
import com.study.mingappk.common.base.BackActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ShowApplyingActivity extends BackActivity {

    @Bind(R.id.icon_head)
    ImageView iconHead;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.apply_status)
    ImageView applyStatus;
    @Bind(R.id.name2)
    TextView name2;
    @Bind(R.id.click_user)
    RelativeLayout clickUser;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_sex)
    TextView tvSex;
    @Bind(R.id.tv_data)
    TextView tvData;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_education)
    TextView tvEducation;
    @Bind(R.id.tv_village_name)
    TextView tvVillageName;
    @Bind(R.id.img_photo0)
    ImageView imgPhoto0;
    @Bind(R.id.img_photo1)
    ImageView imgPhoto1;
    @Bind(R.id.img_photo2)
    ImageView imgPhoto2;
    @Bind(R.id.img_photo3)
    ImageView imgPhoto3;
    @Bind(R.id.rootLayout)
    LinearLayout rootLayout;

    public static String STATUS_APPLY="status_apply";
    private String villageName;
    private String villageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_applying);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_show_applying);

        initDatas();
    }

    private void initDatas() {
        //基本信息显示
        ApplyInfo2 data = Hawk.get(APP.APPLY_INFO+Hawk.get(APP.ME_UID));
        if (data != null) {
            villageName=data.getVillageName();
            villageId=data.getVillageId();
            //用户头部信息
            Glide.with(this)
                    .load(data.getHeadUrl())
                    .bitmapTransform(new CropCircleTransformation(this))
                    .error(R.mipmap.defalt_user_circle)
                    .into(iconHead);
            name.setText(data.getShowName());
            name2.setText(data.getShowName2());
            //用户填写信息
            tvName.setText(data.getName());
            tvSex.setText(data.getSex());
            tvData.setText(data.getBrithday());
            tvPhone.setText(data.getPhone());
            tvEducation.setText(data.getEducation());
            tvVillageName.setText(data.getVillageName());
            //用户传递照片信息；
            Glide.with(this)
                    .load(data.getFile1())
                    .into(imgPhoto0);
            Glide.with(this)
                    .load(data.getFile2())
                    .into(imgPhoto1);
            Glide.with(this)
                    .load(data.getOtherImagePath())
                    .into(imgPhoto3);
        }
        //申请状态显示
        String status=getIntent().getStringExtra(STATUS_APPLY);
        switch (status){
            case "0"://申请中
                name.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_apply_ing,0);
                applyStatus.setVisibility(View.GONE);
                break;
            case "1"://申请通过
                applyStatus.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(R.mipmap.ic_apply_passed)
                        .into(applyStatus);
                //申请通过
                setResult(RESULT_OK );
                Hawk.remove(APP.APPLY_INFO_VID+Hawk.get(APP.ME_UID));
                //申请通过后，储存信息。用于不重新登录，直接管理村
                Hawk.chain()
                        .put(APP.MANAGER_ADDRESS,villageName)
                        .put(APP.MANAGER_VID,villageId)
                        .commit();

                break;
            case "2"://申请未通过
                applyStatus.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(R.mipmap.ic_apply_not_passed)
                        .into(applyStatus);
                //清除申请村vid，以便重新申请
                Hawk.remove(APP.APPLY_INFO_VID+Hawk.get(APP.ME_UID));
                break;
        }
    }
}
