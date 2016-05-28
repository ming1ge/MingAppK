package com.study.mingappk.tmain.userlogin.facelogin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.study.mingappk.R;
import com.study.mingappk.common.utils.BaseTools;
import com.study.mingappk.common.utils.MyGallerFinal;
import com.study.mingappk.common.utils.PhotoOperate;
import com.study.mingappk.model.bean.CheckPhone;
import com.study.mingappk.tmain.BackActivity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;

public class FaceLoginActivity extends BackActivity {

    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.btn_face)
    ImageView btnFace;
    @Bind(R.id.img_face)
    ImageView imgFace;
    @Bind(R.id.takelayout_face)
    RelativeLayout takelayoutFace;
    @Bind(R.id.img_faceretake)
    ImageView imgFaceretake;
    @Bind(R.id.layout_face)
    RelativeLayout layoutFace;
    @Bind(R.id.btn_ok)
    Button btnOk;
    private PhotoInfo photoInfo;
    boolean hasFacePhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_login);
        ButterKnife.bind(this);
        setToolbarTitle(R.string.title_activity_face_login);

        //android6.0 获取运行时权限
        performCodeWithPermission("为正常体验软件，请进行必要的授权！", new PermissionCallback() {
            @Override
            public void hasPermission() {
                //执行获得权限后相关代码
            }

            @Override
            public void noPermission() {

            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @OnClick({R.id.btn_face, R.id.img_faceretake, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_face:
                takePhoto();//拍正面免冠照
                break;
            case R.id.img_faceretake:
                takePhoto();//重拍正面免冠照
                break;
            case R.id.btn_ok:
                String phone = etPhone.getText().toString().trim();
                if (phone.isEmpty()) {
                    Toast.makeText(this, "请输入认证的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!hasFacePhoto) {
                    btnOk.setClickable(true);
                    btnOk.setText("确定");
                    Toast.makeText(this, "请拍摄正面免冠照片", Toast.LENGTH_SHORT).show();
                    return;
                }
                // btnOk.setClickable(false);
                btnOk.setText("认证中,请稍等");
                UserLogin(phone);
                break;
        }
    }

    private void takePhoto() {
        MyGallerFinal aFinal = new MyGallerFinal();
        GalleryFinal.init(aFinal.getCoreConfig(this));
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .build();
        GalleryFinal.openCamera(1001, functionConfig, mOnHanlderResultCallback);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                btnFace.setVisibility(View.GONE);
                imgFaceretake.setVisibility(View.VISIBLE);
                photoInfo = resultList.get(0);
                Glide.with(FaceLoginActivity.this)
                        .load("file://" + photoInfo.getPhotoPath())
                        .into(imgFace);
                hasFacePhoto = true;
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(FaceLoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private void UserLogin(String phone) {
        /**
         *  compid	机构id
         did	设备id
         phone	用户电话号码
         type	类型	1001:人脸验证，1002：指纹验证， 1010：综合验证
         facepic	人脸图片
         fingerpic	指纹图片
         sign	参数和机构KEY组合字符串的加密串
         */
        //1)将除图片外的参数以及机构key组成一个字符串(注意顺序)
        String other = "compid=9&did=123456&phone=" + phone + "&type=1001";
        String str = other + "&key=69939442285489888751746749876227";
        //2)使用MD5算法加密上述字符串
        String sign = BaseTools.md5(str);
        //3)最终得到参数字符串：（注意，KEY参数不带到参数列表,sign参数加入参数列表）
        String str2 = other + "&sign =" + sign;
        //4)把上述字符串做base64加密，最终得到请求:
        String paraString = Base64.encodeToString(str2.getBytes(), Base64.DEFAULT);
        //对图片压缩处理
        File file = null;
        try {
            file = new PhotoOperate(this).scal(photoInfo.getPhotoPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null) {
            //创建okHttpClient对象
            OkHttpClient mOkHttpClient = new OkHttpClient();

            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data",paraString)
                    .addFormDataPart("facepic","123.jpg",fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url("http://capi.nids.com.cn/iras/ver")
                    .post(requestBody)
                    .build();

            mOkHttpClient
                    .newCall(request)
                    .enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            Toast.makeText(FaceLoginActivity.this, "请求错误;" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                            String s=response.body().string();
                            Toast.makeText(FaceLoginActivity.this, "s;" + s, Toast.LENGTH_SHORT).show();
                            Gson gson = new Gson();
                            CheckPhone result = gson.fromJson(new String(Base64.decode(s, Base64.DEFAULT)), CheckPhone.class);
                            Toast.makeText(FaceLoginActivity.this, "ERRO;" + result.getErr(), Toast.LENGTH_SHORT).show();
                            if (result.getErr() == 0) {
                                FaceLogin();
                            }
                        }
                    });

//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//            MyServiceClient.getService()
//                    .postCall_FaceLogin(paraString,requestBody)
//                    .enqueue(new Callback<String>() {
//                        @Override
//                        public void onResponse(Call<String> call, Response<String> response) {
//                            String s=response.body();
//                            Toast.makeText(FaceLoginActivity.this, "s;" + s, Toast.LENGTH_SHORT).show();
//                            Gson gson = new Gson();
//                            CheckPhone result = gson.fromJson(new String(Base64.decode(s, Base64.DEFAULT)), CheckPhone.class);
//                            Toast.makeText(FaceLoginActivity.this, "ERRO;" + result.getErr(), Toast.LENGTH_SHORT).show();
//                            if (result.getErr() == 0) {
//                                FaceLogin();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<String> call, Throwable t) {
//                            Toast.makeText(FaceLoginActivity.this, "请求错误;" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
        }
    }

    private void FaceLogin() {
        Toast.makeText(FaceLoginActivity.this, "人脸登录成功！", Toast.LENGTH_SHORT).show();
    }

    //**************** Android M Permission (Android 6.0权限控制代码封装)*****************************************************
    private int permissionRequestCode = 88;
    private PermissionCallback permissionRunnable;

    public interface PermissionCallback {
        void hasPermission();

        void noPermission();
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param runnable      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback runnable, @NonNull String... permissions) {
        if (permissions.length == 0) return;
//        this.permissionrequestCode = requestCode;
        this.permissionRunnable = runnable;
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)) {
            if (permissionRunnable != null) {
                permissionRunnable.hasPermission();
                permissionRunnable = null;
            }
        } else {
            //permission has not been granted.
            requestPermission(permissionDes, permissionRequestCode, permissions);
        }

    }

    private boolean checkPermissionGranted(String[] permissions) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private void requestPermission(String permissionDes, final int requestCode, final String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.

//            Snackbar.make(getWindow().getDecorView(), requestName,
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction(R.string.common_ok, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            ActivityCompat.requestPermissions(BaseAppCompatActivity.this,
//                                    permissions,
//                                    requestCode);
//                        }
//                    })
//                    .show();
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(permissionDes)
                    .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FaceLoginActivity.this, permissions, requestCode);
                        }
                    }).show();

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(FaceLoginActivity.this, permissions, requestCode);
        }
    }

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        boolean flag = false;
        for (String p : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (permissionRunnable != null) {
                    permissionRunnable.hasPermission();
                    permissionRunnable = null;
                }
            } else {
                Toast.makeText(FaceLoginActivity.this, "正常体验软件，请在系统设置中，为本APP授权：存储空间！", Toast.LENGTH_SHORT).show();
                if (permissionRunnable != null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    //********************** END Android M Permission ****************************************

}