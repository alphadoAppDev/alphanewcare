package kr.co.alphanewcare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import kr.co.alphanewcare.utils.CommonUtils;
import kr.co.alphanewcare.utils.HttpAsyncTask;
import kr.co.alphanewcare.utils.HttpImageUpLoad;
import kr.co.alphanewcare.utils.HttpString;

import static kr.co.alphanewcare.utils.CommonUtils.rotateImage;

public class CareAddActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private ImageView mIvBtnGallery;
    private TextView mTvBtnOk;
    private EditText mEtContent;
    private ImageView mIvGetPhoto;
    private final int GALLERY_CODE = 1000;
    private String mCategory = "";
    private String mMode = "";  // 치아, 피부, 귀 화면 공통으로 사용
    private final String MODE_TOOTH = "tooth";
    private final String MODE_SKIN = "skin";
    private final String MODE_EAR = "ear";

    private ProgressDialog mDialog;

    private String mStatus = "1";  // 정상 = 1, 의심 = 2, 위험 = 3.   (Default = 정상)

    private RadioGroup mRadioGroup;
    private RadioButton mRadioBtn_1;
    private RadioButton mRadioBtn_2;
    private RadioButton mRadioBtn_3;
    private TextView mTvBtnStatus_1;
    private TextView mTvBtnStatus_2;
    private TextView mTvBtnStatus_3;

    public static int ACTIVITY_MODE = 1;
    private int ADD_MODE = 1;
    private int MODIFY_MODE = 2;

    // 수정할때 관련 변수
    private int mClickItemPosition; // 수정모드에서 넘어왔을 때 누른 아이템 Number
    private JSONArray mJsonArrDataFromCareList; // 수정모드에서 넘어온 JSONARRAY DATA
    private String mSeq = "";

    private LinearLayout mLinearEmptyPic;

    // 스캐너 관련
    private TextView mTvBtnToothViewOpen;

    // 치아 디테일 관련
    private ImageView mIvBtnToothDetailClose;
    private View mViewToothDetail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tooth);
        init();
    }

    private void init()
    {
        ((RelativeLayout) findViewById(R.id.relative_tooth_view)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {}});
        mDialog = new ProgressDialog(CareAddActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        String title = "";
        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");
        mIvGetPhoto = findViewById(R.id.iv_get_photo);
        mEtContent = findViewById(R.id.et_content);
        if(mCategory.equals(MainActivity.TOOTH_LIST)){
            mMode = MODE_TOOTH;
            if(ACTIVITY_MODE == ADD_MODE) title = "치아관리등록";
            else if(ACTIVITY_MODE == MODIFY_MODE) title = "치아관리수정";
            mEtContent.setHint("치아관련메모");
            ((View) findViewById(R.id.multi_option_tooth)).setVisibility(View.VISIBLE);
        }else if(mCategory.equals(MainActivity.SKIN_LIST)){
            mMode = MODE_SKIN;
            if(ACTIVITY_MODE == ADD_MODE) title = "피부관리등록";
            else if(ACTIVITY_MODE == MODIFY_MODE) title = "피부관리수정";
            mEtContent.setHint("피부관련메모");
            ((View) findViewById(R.id.multi_option_skin)).setVisibility(View.VISIBLE);
        }else if(mCategory.equals(MainActivity.EAR_LIST)){
            mMode = MODE_EAR;
            if(ACTIVITY_MODE == ADD_MODE) title = "귀관리등록";
            else if(ACTIVITY_MODE == MODIFY_MODE) title = "귀관리수정";
            mEtContent.setHint("귀관련메모");
            ((View) findViewById(R.id.multi_option_ear)).setVisibility(View.VISIBLE);
        }

        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {finish();}});
        ((TextView) findViewById(R.id.txtTitle)).setText(title);
        mIvBtnGallery = findViewById(R.id.iv_btn_gallery);
        mIvBtnGallery.setOnClickListener(mOnClickListener);

        mTvBtnOk = findViewById(R.id.tv_btn_ok);
        mTvBtnOk.setOnClickListener(mOnClickListener);
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(CareAddActivity.this);
        mRadioBtn_1 = findViewById(R.id.radio_btn_1);
        mRadioBtn_2 = findViewById(R.id.radio_btn_2);
        mRadioBtn_3 = findViewById(R.id.radio_btn_3);
        mTvBtnStatus_1 = findViewById(R.id.tv_btn_status_1);
        mTvBtnStatus_2 = findViewById(R.id.tv_btn_status_2);
        mTvBtnStatus_3 = findViewById(R.id.tv_btn_status_3);
        mTvBtnStatus_1.setOnClickListener(mOnClickListener);
        mTvBtnStatus_2.setOnClickListener(mOnClickListener);
        mTvBtnStatus_3.setOnClickListener(mOnClickListener);
        mLinearEmptyPic = findViewById(R.id.linear_empty_pic);
        if(ACTIVITY_MODE == MODIFY_MODE)
        {
            buttonDefault();
            mLinearEmptyPic.setVisibility(View.GONE);
            mTvBtnOk.setText("수정하기");
            mClickItemPosition = Integer.valueOf(intent.getStringExtra("position"));
            mIvGetPhoto.setImageBitmap(CareListActivity.bitmaps[mClickItemPosition]);
            String jsonArrStr = intent.getStringExtra("json_arr_data");
            try {
                mJsonArrDataFromCareList = new JSONArray(jsonArrStr);
                JSONObject jsonObject = mJsonArrDataFromCareList.getJSONObject(mClickItemPosition);
                String memo = jsonObject.getString("memo");
                String radioBtnStatus = jsonObject.getString("Status");
                mSeq = jsonObject.getString("seq");
                mEtContent.setText(memo);
                if(radioBtnStatus.equals("1")) {
                    mRadioBtn_1.setChecked(true);
                    mTvBtnStatus_1.setTextColor(getResources().getColor(R.color.colorWhite));
                    mTvBtnStatus_1.setBackground(getResources().getDrawable(R.drawable.round_green));
                    mStatus = "1";
                }
                else if(radioBtnStatus.equals("2")){
                    mRadioBtn_2.setChecked(true);
                    mTvBtnStatus_2.setTextColor(getResources().getColor(R.color.colorWhite));
                    mTvBtnStatus_2.setBackground(getResources().getDrawable(R.drawable.round_yellow));
                    mStatus = "2";
                }
                else if(radioBtnStatus.equals("3")){
                    mRadioBtn_3.setChecked(true);
                    mTvBtnStatus_3.setTextColor(getResources().getColor(R.color.colorWhite));
                    mTvBtnStatus_3.setBackground(getResources().getDrawable(R.drawable.round_red));
                    mStatus = "3";
                }
                Log.e("mJsonArrCareList", mJsonArrDataFromCareList.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 스캐너관련
        mTvBtnToothViewOpen = findViewById(R.id.tv_btn_tooth_view);
        mTvBtnToothViewOpen.setOnClickListener(mOnClickListener);

        // 치아 디테일 관련
        mViewToothDetail = findViewById(R.id.view_tooth_detail);
        mIvBtnToothDetailClose = findViewById(R.id.iv_btn_tooth_detail_close);
        mIvBtnToothDetailClose.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onBackPressed() {
        if(mViewToothDetail.getVisibility() == View.VISIBLE)
        {
            mViewToothDetail.setVisibility(View.GONE);
        }else{
            finish();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.iv_btn_gallery:
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, GALLERY_CODE);
                    break;
                case R.id.tv_btn_ok:
                    if(mEtContent.getText().toString().equals(""))
                    {
                        CommonUtils.alertOK(CareAddActivity.this, mMode, getResources().getString(R.string.enter_inquery));
                        return;
                    }
                    else if(mFile == null){
                        if(ACTIVITY_MODE == ADD_MODE){  // 등록 모드일때만 반드시 사진 필요
                            CommonUtils.alertOK(CareAddActivity.this, mMode, getResources().getString(R.string.must_picture));
                            return;
                        }
                    }
                    if(mIsFileUpload) // 갤러리에서 사진을 이미지뷰에 가져왔을 경우
                    {
                        new HttpImageUpLoad(CareAddActivity.this, HttpString.IMAGE_UPLOAD, mFile, BaseActivity.mMemidx, mHandler).execute();
                        mDialog.show();
                    }else{
                        mDialog.show();
                        try {
                            String service = "";
                            if(mMode.equals(MODE_TOOTH)){
                                service = HttpString.PET_TOOTH_SERVICE;
                            }else if(mMode.equals(MODE_SKIN)){
                                service = HttpString.PET_SKIN_SERVICE;
                            }else if(mMode.equals(MODE_EAR)){
                                service = HttpString.PET_EAR_SERVICE;
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(HttpString.SERVICE, service);
                            jsonObject.put("memidx", BaseActivity.mMemidx);
                            jsonObject.put("filepath", "");
                            JSONObject listFromObj = mJsonArrDataFromCareList.getJSONObject(mClickItemPosition);
                            jsonObject.put("filepath", listFromObj.getString("filepath"));
                            jsonObject.put("Status", mStatus);
                            jsonObject.put("memo", mEtContent.getText().toString());
                            jsonObject.put("seq", mSeq);
                            jsonObject.put(HttpString.MODE, HttpString.MODIFY);
                            new HttpAsyncTask(CareAddActivity.this, HttpString.MODIFY, jsonObject, mHandler).execute();  // 수정
                            Log.e("수정수정", jsonObject.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.tv_btn_tooth_view:
                    mViewToothDetail.setVisibility(View.VISIBLE);
                    break;

                case R.id.iv_btn_tooth_detail_close:
                    mViewToothDetail.setVisibility(View.GONE);
                    break;

                    // 정상, 의심, 위험 버튼
                case R.id.tv_btn_status_1:
                    onClickStatusButton(1);
                    break;

                case R.id.tv_btn_status_2:
                    onClickStatusButton(2);
                    break;

                case R.id.tv_btn_status_3:
                    onClickStatusButton(3);
                    break;
            }

        }
    };
    File mFile = null;
    Uri mImageUri = null;
    private boolean mIsFileUpload = false;  // 갤러리에서 이미지뷰에 사진을 가져왔을경우 true
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            mFile = new File(getRealPathFromURI(mImageUri));
            if (mImageUri != null) {
                Bitmap bmp = CommonUtils.ResizePicture(CareAddActivity.this, mImageUri, 1000);
                if(bmp != null)
                {
                    try{
                        ExifInterface ei = new ExifInterface(CommonUtils.getRealPathFromURI(CareAddActivity.this, mImageUri));
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        switch (orientation)
                        {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                bmp = rotateImage(bmp, 90);break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                bmp = rotateImage(bmp, 180);break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                bmp = rotateImage(bmp, 270);break;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                mIvGetPhoto.setImageBitmap(bmp);
                mIsFileUpload = true;
                mLinearEmptyPic.setVisibility(View.GONE);
            }
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(Uri.parse(contentUri.toString()), proj, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ERROR:
                    try{
                        mJsonResponseData = new JSONObject((String) msg.obj);
                    }catch (Exception e){}
                    break;
                case OK:
                    mJsonResponseData = (JSONObject)msg.obj;
                    if(mNowConnectionAPI.equals(HttpString.IMAGE_UPLOAD))
                    {
                        mDialog.dismiss();
                        JSONObject jsonObj = mJsonResponseData;
                        JSONArray jsonArrData;
                        String result = "";
                        String uploadFile = "";
                        try {
                            result = jsonObj.getString("result");
                            jsonArrData = jsonObj.getJSONArray("data");
                            JSONObject data_1 = jsonArrData.getJSONObject(0);
                            uploadFile = data_1.getString("uploadFile");
                            Log.e("mJsonResponseData", "mJsonResponseData = " + mJsonResponseData.toString());

                            if(result.equals("ok"))
                            {
                                String service = "";
                                if(mMode.equals(MODE_TOOTH)){
                                    service = HttpString.PET_TOOTH_SERVICE;
                                }else if(mMode.equals(MODE_SKIN)){
                                    service = HttpString.PET_SKIN_SERVICE;
                                }else if(mMode.equals(MODE_EAR)){
                                    service = HttpString.PET_EAR_SERVICE;
                                }

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(HttpString.SERVICE, service);

                                jsonObject.put("memidx", BaseActivity.mMemidx);
                                if(mFile == null)  // 사진을 가져오지 않았을 경우 파일경로 ""
                                {
                                    jsonObject.put("filepath", "");
                                    if(ACTIVITY_MODE == MODIFY_MODE)  // 수정모드에서는 사진을 수정 안 할 경우 기존사진 경로 그대로 업로드
                                    {
                                        JSONObject listFromObj = mJsonArrDataFromCareList.getJSONObject(mClickItemPosition);
                                        jsonObject.put("filepath", listFromObj.getString("filepath"));
                                    }
                                }else{
                                    jsonObject.put("filepath", uploadFile);
                                }

                                Log.e("uploadFile = " , uploadFile);
                                jsonObject.put("Status", mStatus);
                                jsonObject.put("memo", mEtContent.getText().toString());
                                if(ACTIVITY_MODE == MODIFY_MODE)
                                {
                                    jsonObject.put("seq", mSeq);
                                    jsonObject.put(HttpString.MODE, HttpString.MODIFY);
                                    new HttpAsyncTask(CareAddActivity.this, HttpString.MODIFY, jsonObject, mHandler).execute();  // 수정
                                    Log.e("수정수정", jsonObject.toString());
                                }else{
                                    jsonObject.put(HttpString.MODE, HttpString.WRITE);
                                    new HttpAsyncTask(CareAddActivity.this, HttpString.WRITE, jsonObject, mHandler).execute();  // 등록
                                    Log.e("등록등록", jsonObject.toString());
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(mNowConnectionAPI.equals(HttpString.WRITE) || mNowConnectionAPI.equals(HttpString.MODIFY))  // 펫정보 수정하기
                    {
                        mDialog.dismiss();
                        JSONObject jsonObj = mJsonResponseData;
                        Log.e("CareAddActivity", "WRITE : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if(result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
                                finishPopup();
                            }else if(result_code.equals("fail")){ // 애러

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mJsonResponseData = new JSONObject();
                    }
                    break;
            }
        }
    };

    private void buttonDefault()
    {
        mTvBtnStatus_1.setTextColor(getResources().getColor(R.color.test_result_green));
        mTvBtnStatus_2.setTextColor(getResources().getColor(R.color.test_result_yellow));
        mTvBtnStatus_3.setTextColor(getResources().getColor(R.color.test_result_red));
        mTvBtnStatus_1.setBackground(getResources().getDrawable(R.drawable.round_gray));
        mTvBtnStatus_2.setBackground(getResources().getDrawable(R.drawable.round_gray));
        mTvBtnStatus_3.setBackground(getResources().getDrawable(R.drawable.round_gray));
    }
    private void onClickStatusButton(int status)
    {
        buttonDefault();
        if(status == 1)
        {
            mTvBtnStatus_1.setTextColor(getResources().getColor(R.color.colorWhite));
            mTvBtnStatus_1.setBackground(getResources().getDrawable(R.drawable.round_green));
            mStatus = "1";
        }
        else if(status == 2)
        {
            mTvBtnStatus_2.setTextColor(getResources().getColor(R.color.colorWhite));
            mTvBtnStatus_2.setBackground(getResources().getDrawable(R.drawable.round_yellow));
            mStatus = "2";
        }
        else if(status == 3)
        {
            mTvBtnStatus_3.setTextColor(getResources().getColor(R.color.colorWhite));
            mTvBtnStatus_3.setBackground(getResources().getDrawable(R.drawable.round_red));
            mStatus = "3";
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group == mRadioGroup){
            if(checkedId == R.id.radio_btn_1){
                mStatus = "1";
            }
            else if(checkedId == R.id.radio_btn_2){
                mStatus = "2";
            }
            else if(checkedId == R.id.radio_btn_3){
                mStatus = "3";
            }
        }
    }

    private void finishPopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CareAddActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cardview_alert_ok, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Button btnOk;
        TextView txtTitle, txtMsg;
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(mMode);
        txtMsg = view.findViewById(R.id.txtMsg);
        txtMsg.setText("등록이 완료되었습니다.");
        btnOk = view.findViewById(R.id.btnOK);
        btnOk.setText("OK");
        btnOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
                Intent intent = new Intent(CareAddActivity.this, CareListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("category",mMode);
                startActivity(intent);
            }
        }) ;
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
