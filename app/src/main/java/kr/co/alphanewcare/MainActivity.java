                         package kr.co.alphanewcare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.alphanewcare.graph.GraphActivity;
import kr.co.alphanewcare.listview.HomeTestListAdapter;
import kr.co.alphanewcare.main.MainAction;
import kr.co.alphanewcare.main.MainUISetup;
import kr.co.alphanewcare.pets.PetKind;
import kr.co.alphanewcare.tutorial.TutorialActivity;
import kr.co.alphanewcare.utils.CommonUtils;
import kr.co.alphanewcare.utils.Constants;
import kr.co.alphanewcare.utils.HttpAsyncTask;
import kr.co.alphanewcare.utils.HttpString;
import kr.co.alphanewcare.utils.PetInfo;
import kr.co.alphanewcare.utils.SoapClient;
import kr.co.alphanewcare.utils.TestHistory;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity {
    private final String tag = "MainActivity";

    Context mContext;
    ImageView imgTab1, imgTab2, imgTab3, imgTab4, imgTab5;
    int nCommType; // 0: add history, 1: get history;
    private boolean bLoading;

    private PetInfo mPetInfo;
    private List<TestHistory> testHistoryList;

    private ViewPager viewPager = null;
    private HomePagerAdapter pagerAdapter = null;

    private final BroadcastReceiver serverResponseReceiver = new serverResponseReceiver();
    private final IntentFilter serverResponseFilter = new IntentFilter(Constants.ACTION_MAIN_ACTIVITY);
    private final BroadcastReceiver addPetReceiver = new addPetReceiver();
    private final IntentFilter addPetFilter = new IntentFilter(Constants.ACTION_ADD_PET);
    private final BroadcastReceiver receiveMatchingIndex = new receiveMatchingIndex();
    private final IntentFilter receiveMatchingFilter = new IntentFilter(Constants.MATCHING_INDEX);
    private final BroadcastReceiver finishAtivityReceiver = new finishAtivityReceiver();
    private final IntentFilter finishAtivityFilter = new IntentFilter(Constants.ACTION_FINISH_ATIVITY);

    public static final int REQ_KEY_PERMISSION_CODE = 1000;

    private MainUISetup uiSetup;
    private MainAction action;

    private DrawerLayout mDrawerLayout;
    private RelativeLayout mMenu_1;
    private RelativeLayout mMenu_2;
    private RelativeLayout mMenu_3;
    private RelativeLayout mMenu_4;
    private RelativeLayout mMenu_5;
    private RelativeLayout mMenu_6;
    private RelativeLayout mMenu_7;
    private RelativeLayout mMenu_8;
    private RelativeLayout mMenu_9;

    private ListView mTestListView;
    private HomeTestListAdapter mTestListAdapter;

    // 신규 ============
    private String mMyEmail = "";
    private ProgressDialog mDialog;

    // 등록관련 메뉴팝업
    private RelativeLayout mRelativeAddMenu;
    private RelativeLayout mRelativeBtnAddMenu_1;
    private RelativeLayout mRelativeBtnAddMenu_2;
    private RelativeLayout mRelativeBtnAddMenu_3;
    private RelativeLayout mRelativeBtnAddMenu_4;

    private ImageView mIvAddPopup;

    // 관리내역보기 관련 메뉴팝업
    private LinearLayout mLinearContentMenu;
    private TextView mTvContentMenu_1;
    private TextView mTvContentMenu_2;
    private TextView mTvContentMenu_3;
    private Boolean granted = false;
    private Boolean granted2 = false;

    // 신규 end =======

    // 알람 테스트용
    private TimePicker mTimePicker;
    private Calendar mCalendar;

    public static final String TOOTH_LIST = "tooth";
    public static final String SKIN_LIST = "skin";
    public static final String EAR_LIST = "ear";


    private void setAlarmTime()
    {
        mCalendar.setTimeInMillis((System.currentTimeMillis()));
        mCalendar.set(Calendar.HOUR_OF_DAY, 16);
        mCalendar.set(Calendar.MINUTE, 50);
        mCalendar.set(Calendar.SECOND, 0);

        if(mCalendar.before(Calendar.getInstance())){
            mCalendar.add(Calendar.DATE, 1);
        }
    }
    // 알람 테스트용 end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

//        setAlarmTime();

        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
        if(AppGlobals.petInfoList != null)
        {
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FCM Log", "getInstanceId failed", task.getException());
                                return;
                            }
                            String token = task.getResult().getToken();
                            Log.d("FCM Log", "FCM 토큰: " + token);
                            SoapClient client;
                            client = new SoapClient(mContext, Constants.ACTION_CHANGE_PASSWORD_ACTIVITY);
                            client.SetToken(AppGlobals.INSTANCE.getEmail(), token);
                        }
                        private void sendRegistrationToserver(String token) {
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = new FormBody.Builder()
                                    .add("Token", token)
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://121.152.44.12")
                                    .post(body)
                                    .build();
                            try {
                                client.newCall(request).execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            mContext = getApplicationContext();
            mMyEmail = AppGlobals.INSTANCE.getEmail();
            ((TextView) findViewById(R.id.tv_email)).setText(mMyEmail);
            // InitAppGlobals();
            bLoading = false;

            testHistoryList = new ArrayList<>();


            // ===== 신규 =====

            // ===== 신규 =====
            pagerAdapter = new HomePagerAdapter();
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    //Log.e("TTTTTTTTTTT", "onPageScrolled, position: " + position + ", positionOffset: " + positionOffset + "positionOffsetPixels: " + positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    Log.e("KKKKKKKK", "position: " + position);
                    AppGlobals.INSTANCE.setCurrentPage(position);
                    nCommType = 1;
                    UpdatePetBar();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    //Log.e("TTTTTTTTTTT", "onPageScrollStateChanged, state: " + state);
                }
            });

            View.OnTouchListener touchListener = new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        // if pressed
                        case MotionEvent.ACTION_DOWN: {
                            changeTabImage(v, true);
                            break;
                        }
                        // if released
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP: {
                            changeTabImage(v, false);
                            v.performClick();
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    return false;
                }
            };

            imgTab1 = (ImageView) findViewById(R.id.imgTab1);
            imgTab1.setOnTouchListener(touchListener);
            imgTab1.setImageResource(R.drawable.tab_menu_off);

            imgTab2 = (ImageView) findViewById(R.id.imgTab2);
            imgTab2.setOnTouchListener(touchListener);
            imgTab2.setImageResource(R.drawable.tab_result_off);

            imgTab3 = (ImageView) findViewById(R.id.imgTab3);
            imgTab3.setOnTouchListener(touchListener);
            imgTab3.setImageResource(R.drawable.tab_test);

            imgTab4 = (ImageView) findViewById(R.id.imgTab4);
            imgTab4.setOnTouchListener(touchListener);
            imgTab4.setImageResource(R.drawable.tab_modify_off);

            imgTab5 = (ImageView) findViewById(R.id.imgTab5);
            imgTab5.setOnTouchListener(touchListener);
            imgTab5.setImageResource(R.drawable.tab_home);

            init();
        }else{
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setMessage(getResources().getString(R.string.refresh));
            ab.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    intent = new Intent(MainActivity.this, IntroActivity.class);
                    startActivity(intent);
                }
            });
            ab.show();
        }
    }

    public ArrayList<String> checkPermission(Activity activity, String[] permissions, int reqCode) {
        ArrayList<String> permissionList = new ArrayList<>();
        for(String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(activity, permission);

            if(result != PackageManager.PERMISSION_GRANTED) permissionList.add(permission);
        }
        return permissionList;
    }


    private PermissionListener permissionListener1 = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                int currentPage = viewPager.getCurrentItem();
                PetInfo info = AppGlobals.INSTANCE.getPetInfo(currentPage);
                if(info != null) {
                    intent = new Intent(MainActivity.this, TutorialActivity.class);
                    intent.putExtra(TutorialActivity.REQ_MODE, URSDefine.PaperMode.litmus10.getValue());
                    startActivity(intent);
                    mRelativeAddMenu.setVisibility(View.GONE);
                } else{
                    Toast.makeText(MainActivity.this, mContext.getString(R.string.no_pet), Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {}
    };

    private PermissionListener permissionListener2= new PermissionListener() {
        @Override
        public void onPermissionGranted() {

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                int currentPage = viewPager.getCurrentItem();
                PetInfo info = AppGlobals.INSTANCE.getPetInfo(currentPage);
                if(info != null) {
                    intent = new Intent(MainActivity.this, TutorialActivity.class);
                    intent.putExtra(TutorialActivity.REQ_MODE, URSDefine.PaperMode.litmus10.getValue());
                    startActivity(intent);
                    mRelativeAddMenu.setVisibility(View.GONE);
                } else{
                    Toast.makeText(MainActivity.this, mContext.getString(R.string.no_pet), Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {}
    };

    private View.OnClickListener mOnClickListenerPopup = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId())
            {
                // 소변검사 메뉴
                case R.id.relative_popup_menu_1:
                    int currentPage = viewPager.getCurrentItem();
                    PetInfo info = AppGlobals.INSTANCE.getPetInfo(currentPage);
                    if(info != null)
                    {
                        intent = new Intent(MainActivity.this, TutorialActivity.class);
                        intent.putExtra(TutorialActivity.REQ_MODE, URSDefine.PaperMode.litmus10.getValue());
                        startActivity(intent);
                        mRelativeAddMenu.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(MainActivity.this, mContext.getString(R.string.no_pet), Toast.LENGTH_SHORT).show();
                    }

                    break;

                // 치아, 피부, 귀 관리 등록 메뉴
                case R.id.relative_popup_menu_2:
                    intent = new Intent(MainActivity.this, CareAddActivity.class);
                    intent.putExtra("category", TOOTH_LIST);
                    CareAddActivity.ACTIVITY_MODE = 1;  // 등록모드
                    startActivity(intent);
                    mRelativeAddMenu.setVisibility(View.GONE);
                    break;
                case R.id.relative_popup_menu_3:
                    intent = new Intent(MainActivity.this, CareAddActivity.class);
                    intent.putExtra("category", SKIN_LIST);
                    CareAddActivity.ACTIVITY_MODE = 1;  // 등록모드
                    startActivity(intent);
                    mRelativeAddMenu.setVisibility(View.GONE);
                    break;
                case R.id.relative_popup_menu_4:
                    intent = new Intent(MainActivity.this, CareAddActivity.class);
                    intent.putExtra("category", EAR_LIST);
                    CareAddActivity.ACTIVITY_MODE = 1;  // 등록모드
                    startActivity(intent);
                    mRelativeAddMenu.setVisibility(View.GONE);
                    break;
                // 치아, 피부, 귀 관리 등록 메뉴

                // 등록 팝업 버튼
                case R.id.iv_add_popup:
                    TedPermission.with(MainActivity.this)
                            .setPermissionListener(permissionListener1)
                            .setDeniedMessage(mContext.getString(R.string.diary_permission_guilde))
                            .setPermissions(
                                    Manifest.permission.CAMERA
                            ).check();

                    TedPermission.with(MainActivity.this)
                            .setPermissionListener(permissionListener2)
                            .setDeniedMessage(mContext.getString(R.string.diary_permission_guilde2))
                            .setPermissions(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            ).check();

                    break;

                // 리스트쪽 팝업 메뉴
                case R.id.tv_popup_menu_sub_1:
                    intent = new Intent(MainActivity.this, CareListActivity.class);
                    intent.putExtra("category", TOOTH_LIST);
                    startActivity(intent);
                    break;

                case R.id.tv_popup_menu_sub_2:
                    intent = new Intent(MainActivity.this, CareListActivity.class);
                    intent.putExtra("category", SKIN_LIST);
                    startActivity(intent);
                    break;

                case R.id.tv_popup_menu_sub_3:
                    intent = new Intent(MainActivity.this, CareListActivity.class);
                    intent.putExtra("category", EAR_LIST);
                    startActivity(intent);
                    break;
                // 리스트쪽 팝업 메뉴 end
            }
        }
    };



    public void changeTabImage(View v, boolean pressed)
    {
        ImageView image = (ImageView) v;
        //image.setImageResource(R.drawable.main_tab01_on);
/*
        switch (v.getId()) {

            case R.id.imgTab01:
                if (bNewMsg) {
                    if (pressed) image.setImageResource(R.drawable.main_tab01_new_on);
                    else image.setImageResource(R.drawable.main_tab01_new);
                }else {
                    if (pressed) image.setImageResource(R.drawable.main_tab01_on);
                    else image.setImageResource(R.drawable.main_tab01);
                }
                if (!pressed) ShowMessage();
                break;
            case R.id.imgTab02:
                if (pressed) image.setImageResource(R.drawable.main_tab02_on);
                else image.setImageResource(R.drawable.main_tab02);

                if (!pressed) SetupApplication();
                break;
            case R.id.imgTab03:
                if (bNoSafe) {
                    if (pressed) image.setImageResource(R.drawable.main_tab03_new_on);
                    else image.setImageResource(R.drawable.main_tab03_new);
                }else {
                    if (pressed) image.setImageResource(R.drawable.main_tab03_on);
                    else image.setImageResource(R.drawable.main_tab03);
                }

                if (!pressed) AddDevice();
                break;
            case R.id.imgTab04:
                if (pressed) image.setImageResource(R.drawable.main_tab04_on);
                else image.setImageResource(R.drawable.main_tab04);

                if (!pressed) ShowProductInfo();
                break;
        }*/
    }

    public void showTutorialActivity(View view)
    {
        Log.e("dd","");
//        if (AppGlobals.INSTANCE.getPetCount() > 0) {
//            Intent intent = new Intent(this, TutorialActivity.class);
//            intent.putExtra(TutorialActivity.REQ_MODE, mode.getValue());
//            startActivity(intent);
//        }else {
//            CommonUtils.alertOK(MainActivity.this, this,mContext.getResources().getString(R.string.urine_test), mContext.getResources().getString(R.string.no_reg_pet) + " " + mContext.getResources().getString(R.string.use_reg_pet));
//
//            //AddPet();
//        }
    }

    public void showGraphActivity()
    {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    private void init()
    {
        mArrPetPicture = new ArrayList();
//        uiSetup = new MainUISetup(this, this);
//        action = new MainAction(this, this);
//
//        uiSetup.setAction(action);
//        action.setUiSetup(uiSetup);

//        uiSetup.setupViews();
//        uiSetup.setupButtons();

        registerReceiver(serverResponseReceiver, serverResponseFilter);
        registerReceiver(addPetReceiver, addPetFilter);
        registerReceiver(receiveMatchingIndex, receiveMatchingFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(finishAtivityReceiver, finishAtivityFilter);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ((View) findViewById(R.id.drawer)).setVisibility(View.VISIBLE);
        mMenu_1 = findViewById(R.id.menu_1);
        mMenu_2 = findViewById(R.id.menu_2);
        mMenu_3 = findViewById(R.id.menu_3);
        mMenu_4= findViewById(R.id.menu_4);
        mMenu_5 = findViewById(R.id.menu_5);
        mMenu_6 = findViewById(R.id.menu_6);
        mMenu_7 = findViewById(R.id.menu_7);
        mMenu_8 = findViewById(R.id.menu_8);
        mMenu_9 = findViewById(R.id.menu_9);
        mMenu_1.setOnClickListener(mOnClickListener);
        mMenu_2.setOnClickListener(mOnClickListener);
        mMenu_3.setOnClickListener(mOnClickListener);
        mMenu_4.setOnClickListener(mOnClickListener);
        mMenu_5.setOnClickListener(mOnClickListener);
        mMenu_6.setOnClickListener(mOnClickListener);
        mMenu_7.setOnClickListener(mOnClickListener);
        mMenu_8.setOnClickListener(mOnClickListener);
        mMenu_9.setOnClickListener(mOnClickListener);

        mRelativeAddMenu = findViewById(R.id.relative_add_menu);
        mRelativeBtnAddMenu_1 = findViewById(R.id.relative_popup_menu_1);
        mRelativeBtnAddMenu_2 = findViewById(R.id.relative_popup_menu_2);
        mRelativeBtnAddMenu_3 = findViewById(R.id.relative_popup_menu_3);
        mRelativeBtnAddMenu_4 = findViewById(R.id.relative_popup_menu_4);
        mIvAddPopup = findViewById(R.id.iv_add_popup);
        mIvAddPopup.setOnClickListener(mOnClickListenerPopup);
        mRelativeBtnAddMenu_1.setOnClickListener(mOnClickListenerPopup);
        mRelativeBtnAddMenu_2.setOnClickListener(mOnClickListenerPopup);
        mRelativeBtnAddMenu_3.setOnClickListener(mOnClickListenerPopup);
        mRelativeBtnAddMenu_4.setOnClickListener(mOnClickListenerPopup);

        mLinearContentMenu = findViewById(R.id.linear_content_menu);
        mTvContentMenu_1 = findViewById(R.id.tv_popup_menu_sub_1);
        mTvContentMenu_2 = findViewById(R.id.tv_popup_menu_sub_2);
        mTvContentMenu_3 = findViewById(R.id.tv_popup_menu_sub_3);
        mTvContentMenu_1.setOnClickListener(mOnClickListenerPopup);
        mTvContentMenu_2.setOnClickListener(mOnClickListenerPopup);
        mTvContentMenu_3.setOnClickListener(mOnClickListenerPopup);
    }

    public boolean isGranted(int[] grantResults)
    {
        boolean isGranted = true;
        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        return isGranted;
    }

    private void DeleteViewPager() {
        LinearLayout linearLayout;

        //linearLayout = (LinearLayout)pagerAdapter.getView(0);
        linearLayout = (LinearLayout)pagerAdapter.getView (viewPager.getCurrentItem());
        int pageIndex = pagerAdapter.removeView (viewPager, linearLayout);

        AppGlobals.INSTANCE.deletePetInfo(pageIndex);
        ArrayList arrTemp = new ArrayList();
        arrTemp = mArrPetPicture;
        mArrPetPicture = new ArrayList();
        if(arrTemp.size() != 1)
        {
            for(int i = 0 ; i < arrTemp.size(); i++)
            {
                if(mSelectedPetNum != i){
                    mArrPetPicture.add(arrTemp.get(i));
                }
            }
        }

        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == pagerAdapter.getCount()) {
            pageIndex--;
        }


        pagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem (pageIndex);

        if (pageIndex < 0) {
            LoadViewPager(1);
            ClearTestList();
        }
        else UpdatePetBar();

    }

    public static ArrayList mArrPetPicture;
    private void LoadViewPager(final int count) {
        Log.e("LoadViewPager", " count = " + count);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout linearLayout = null;
        int pageIndex;
        String realPath = "";
        String[] realPathStr = new String[count];
        circleImageViews = new CircleImageView[count];
        for (int i = 0; i < count; i++) {
            linearLayout = (LinearLayout) inflater.inflate(R.layout.view_pager_content, null);
            if (pagerAdapter.getCount() == 0) {
                pageIndex = pagerAdapter.addView(linearLayout, 0);

            } else {
                pageIndex = pagerAdapter.addView(linearLayout);
            }

            pagerAdapter.notifyDataSetChanged();
            //viewPager.setCurrentItem(pageIndex, true);

            PetInfo info = AppGlobals.INSTANCE.getPetInfo(pageIndex);
            if (info != null) {
                // 신규
                ImageView symbol = linearLayout.findViewById(R.id.iv_pet_symbol);
                int petType = info.getPetType();
                if (petType == 1) {
                    symbol.setImageResource(R.drawable.ic_food_dog);
                } else {
                    symbol.setImageResource(R.drawable.ic_food_cat);
                }
                // 신규 end
                TextView textView = linearLayout.findViewById(R.id.txtName);
                textView.setText(info.getPetName());

                int petKind;
                petKind = info.getBreed();

                textView = linearLayout.findViewById(R.id.tv_pet_info);
                textView.setText(PetKind.GetPetKind(mContext, petType, petKind));

                textView = linearLayout.findViewById(R.id.txtRegisterNumber);
                textView.setText((mContext.getResources().getString(R.string.pet_reg_number) + ": " + info.getPetID()));

                textView = linearLayout.findViewById(R.id.txtBirth);
                textView.setText("" + info.getBirthDay());

                textView = linearLayout.findViewById(R.id.txtWeight);
                textView.setText("" + info.getWeight() + "kg");

                realPath = info.getPetImagePath();
                realPathStr[i] = realPath;
                final CircleImageView civ = linearLayout.findViewById(R.id.circleImageView);
                final int finalI = i;
//                if (realPathStr[i].length() > 0) {
                try {
                    Glide.with(MainActivity.this).load(realPathStr[i]).asBitmap().listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            mThreadCnt++;
                            circleImageViews[finalI] = civ;
                            if (mThreadCnt == circleImageViews.length) {
                                addViewUpdate();
                            }
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            final Bitmap bitmap = resource;
                            Log.e("onResourceReady", bitmap.toString() + "       " + finalI);
                            civ.setImageBitmap(bitmap);
                            circleImageViews[finalI] = civ;
                            mThreadCnt++;
                            if (mThreadCnt == circleImageViews.length) {
                                addViewUpdate();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        viewPager.setCurrentItem(0, true);
        UpdatePetBar();
    }

    private int mThreadCnt = 0;
    public static CircleImageView circleImageViews[];
    private void addViewUpdate()
    {
        for(int i = 0 ; i < circleImageViews.length ; i++)
        {
            if(((BitmapDrawable)circleImageViews[i].getDrawable()).getBitmap() != null)
            {
                mArrPetPicture.add(((BitmapDrawable)circleImageViews[i].getDrawable()).getBitmap());
            }else{
                mArrPetPicture.add(null);
            }
        }
    }

    private void AddViewPager() {

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout frameLayout;
        int pageIndex;

        // Create an initial view to display; must be a subclass of FrameLayout.
        if (AppGlobals.INSTANCE.getPetCount() == 1) {
            frameLayout = (LinearLayout) pagerAdapter.getView(0);
            pageIndex = viewPager.getCurrentItem();

        } else {
            frameLayout = (LinearLayout) inflater.inflate(R.layout.view_pager_content, null);
            if (pagerAdapter.getCount() == 0) pageIndex = pagerAdapter.addView(frameLayout, 0);
            else pageIndex = pagerAdapter.addView(frameLayout);
        }

        pagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(pageIndex, true);

        //Log.e(tag, "pageindex: " + pageIndex + ", petcnt: " + AppGlobals.INSTANCE.getPetCount());

        if (AppGlobals.INSTANCE.getPetCount() > 0) {
            //PetInfo info = new PetInfo();

            (frameLayout.findViewById(R.id.linear_content_view_bottom)).setVisibility(View.VISIBLE);

            PetInfo info = AppGlobals.INSTANCE.getPetInfo(pageIndex);
            if (info != null) {

                TextView textView = (TextView) frameLayout.findViewById(R.id.txtName);
                textView.setText(info.getPetName());

                int petKind, petType;
                petType = info.getPetType();
                petKind = info.getBreed();

                // 신규
                ImageView symbol = frameLayout.findViewById(R.id.iv_pet_symbol);
                if(petType == 1)
                {
                    symbol.setImageResource(R.drawable.ic_food_dog);
                }else{
                    symbol.setImageResource(R.drawable.ic_food_cat);
                }
                // 신규 end

                textView = frameLayout.findViewById(R.id.tv_pet_info);
                textView.setText(PetKind.GetPetKind(mContext, petType, petKind));

                textView = frameLayout.findViewById(R.id.txtRegisterNumber);
                textView.setText((mContext.getResources().getString(R.string.pet_reg_number) + ": " + info.getPetID()));

                textView = frameLayout.findViewById(R.id.txtBirth);
                textView.setText(info.getBirthDay());

                textView = frameLayout.findViewById(R.id.txtWeight);
                textView.setText(info.getWeight() + "kg");

                String realPath = info.getPetImagePath();
                final CircleImageView civ = frameLayout.findViewById(R.id.circleImageView);
                Glide.with(MainActivity.this).load(realPath).asBitmap().listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        civ.setImageResource(R.drawable.ic_add_camera);
                        mArrPetPicture.add(null);
                        UpdatePetBar();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                }).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        final Bitmap bitmap = resource;
                        civ.setImageBitmap(bitmap);
                        mArrPetPicture.add(bitmap);
                        UpdatePetBar();
                    }
                });
//                if (realPath.length() > 0) {
//                    CircleImageView civ = frameLayout.findViewById(R.id.circleImageView);
//                    Uri petImage = CommonUtils.getUriFromPath(mContext, realPath);
//                    //Log.e(tag, "path:" + realPath);
//                    if (petImage != null) {
//                        //Bitmap bmp = CommonUtils.ResizePicture(mContext, petImage, 100);
//                        Bitmap bmp = resize(mContext, petImage, 100);
//                        if (bmp != null)
//                            try{
//                                ExifInterface ei = new ExifInterface(CommonUtils.getRealPathFromURI(MainActivity.this, petImage));
//                                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//                                switch (orientation)
//                                {
//                                    case ExifInterface.ORIENTATION_ROTATE_90:
//                                        bmp = rotateImage(bmp, 90);break;
//
//                                    case ExifInterface.ORIENTATION_ROTATE_180:
//                                        bmp = rotateImage(bmp, 180);break;
//
//                                    case ExifInterface.ORIENTATION_ROTATE_270:
//                                        bmp = rotateImage(bmp, 270);break;
//                                }
//                            }catch (IOException e){
//                                e.printStackTrace();
//                            }
//                            civ.setImageBitmap(bmp);
//                            mArrPetPicture.add(bmp);
//                     }
//                }else{
//                    mArrPetPicture.add(null);
//                }

            }
        }
    }

    Handler handler = new Handler();
    private void UpdateViewPager() {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout frameLayout;
        int pageIndex;

        pageIndex = viewPager.getCurrentItem();

        frameLayout = (LinearLayout) pagerAdapter.getView(pageIndex);

        pagerAdapter.notifyDataSetChanged();

        PetInfo info = AppGlobals.INSTANCE.getPetInfo(pageIndex);
        if (info != null) {
            TextView textView = (TextView) frameLayout.findViewById(R.id.txtName);
            textView.setText(info.getPetName());

            int petType, petKind;
            petType = info.getPetType();
            petKind = info.getBreed();

            // 신규
            ImageView symbol = frameLayout.findViewById(R.id.iv_pet_symbol);
            if(petType == 1)
            {
                symbol.setImageResource(R.drawable.ic_food_dog);
            }else{
                symbol.setImageResource(R.drawable.ic_food_cat);
            }
            // 신규 end

            textView = frameLayout.findViewById(R.id.tv_pet_info);
            textView.setText(PetKind.GetPetKind(mContext, petType, petKind));

            textView = frameLayout.findViewById(R.id.txtRegisterNumber);
            textView.setText((mContext.getResources().getString(R.string.pet_reg_number) + ": " + info.getPetID()));

            textView = frameLayout.findViewById(R.id.txtBirth);
            textView.setText(info.getBirthDay());

            textView = frameLayout.findViewById(R.id.txtWeight);
            textView.setText(info.getWeight() + "kg");

            final String realPath = info.getPetImagePath();
            if (realPath.length() > 0) {
                final CircleImageView civ = frameLayout.findViewById(R.id.circleImageView);

//                final Uri petImage = CommonUtils.getUriFromPath(mContext, realPath);
                final Bitmap[] bmp = new Bitmap[1];
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(realPath);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(MainActivity.this).load(realPath).asBitmap().listener(new RequestListener<String, Bitmap>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                                            civ.setImageBitmap(null);
                                            ArrayList arrTemp;
                                            arrTemp = mArrPetPicture;
                                            mArrPetPicture = new ArrayList();
                                            for(int i = 0 ; i < arrTemp.size() ; i++)
                                            {
                                                if(mSelectedPetNum != i){
                                                    mArrPetPicture.add(arrTemp.get(i));
                                                }else{
                                                    mArrPetPicture.add(null);
                                                }
                                            }
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            return false;
                                        }
                                    }).into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            final Bitmap bitmap = resource;
                                            civ.setImageBitmap(bitmap);
                                            ArrayList arrTemp;
                                            arrTemp = mArrPetPicture;
                                            mArrPetPicture = new ArrayList();
                                            for(int i = 0 ; i < arrTemp.size() ; i++)
                                            {
                                                if(mSelectedPetNum != i){
                                                    mArrPetPicture.add(arrTemp.get(i));
                                                }else{
                                                    mArrPetPicture.add(bitmap);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                Log.e(tag, "path:" + realPath);
                //Bitmap bmp = CommonUtils.ResizePicture(mContext, petImage, 100);
//                Bitmap bmp = resize(mContext, petImage, 100);
            }
        }
    }

    private Bitmap resize(Context context, Uri uri, int resize) {
        Bitmap resizeBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;


            while (true) {
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap = bitmap;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }

    public static int mSelectedPetNum;
    private void UpdatePetBar() {
//        mDialog.show();
        //int count = pagerAdapter.getCount();
        int count = AppGlobals.INSTANCE.getPetCount();
        int selected = viewPager.getCurrentItem();
        mSelectedPetNum = selected;
        if(count > 0){
            mDialog.show();
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout lyView = (LinearLayout) pagerAdapter.getView(selected);
        LinearLayout ly = (LinearLayout) findViewById(R.id.lySelectedPet);
        //inflater.inflate(R.layout.select_pet, ly, true);
        if (ly.getChildCount() != 0) ly.removeAllViews();


        Log.e(tag, "count: " + count + ", selected: " + selected);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 0, 0, 0);
//        ly.setLayoutParams(lp);


        ImageView imgView;
        for (int i = 0; i < count; i++) {
            imgView = (ImageView) inflater.inflate(R.layout.view_pager_indicator, ly, false);
            if (i == selected) imgView.setImageResource(R.drawable.test_result_dot_red);
            else imgView.setImageResource(R.drawable.test_result_dot_pink);
            imgView.setLayoutParams(lp);
            ly.addView(imgView);

        }

        GetPetUrineHistory(selected);
    }

//    private void UpdatePetInfo(View view, int count, int selected) {
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
//        LinearLayout ly = (LinearLayout) view.findViewById(R.id.lySelectedPet);
//        //inflater.inflate(R.layout.select_pet, ly, true);
//        if (ly.getChildCount() != 0) ly.removeAllViews();
//
//        ImageView imgView;
//        for (int i = 1; i < count + 1; i++) {
//            imgView = (ImageView) inflater.inflate(R.layout.view_pager_indicator, ly, false);
//            if (i == selected) imgView.setImageResource(R.drawable.ellipse_blue);
//            else imgView.setImageResource(R.drawable.ellipse_gray);
//            ly.addView(imgView);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_ADD_PET:
                    AddViewPager();
                    break;
                case Constants.REQUEST_CODE_MODIFY_PET:
                    if (data != null && data.hasExtra("PAGE_NUMBER")) DeleteViewPager();
                    else UpdateViewPager();
                    //UpdateViewPager(AppGlobals.INSTANCE.getCurrentPage());
                    break;
            }
        }

    }


    private void ClearTestList() {
        TextView textView = findViewById(R.id.txtTestNone);
        textView.setText(mContext.getResources().getString(R.string.no_reg_pet));
        textView.setVisibility(View.VISIBLE);
        findViewById(R.id.txtTestHistory).setVisibility(View.GONE);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mContext, TestResult.class);
            intent.putExtra("list_index",position);
            startActivity(intent);
        }
    };

    private void UpdateTestList() {
        PetInfo petInfo;
        petInfo = AppGlobals.INSTANCE.getPetInfo(viewPager.getCurrentItem());
        if (petInfo == null) {
            Log.e(tag, "UpdateTestList: Pet info is null");
            return;
        }

        mTestListView = findViewById(R.id.listview_test);
        mTestListView.setOnItemClickListener(mOnItemClickListener);
        mTestListAdapter = new HomeTestListAdapter(MainActivity.this, petInfo);
        mTestListView.setAdapter(mTestListAdapter);

        int testCount = petInfo.getTestHistorySize();
        TextView textView = findViewById(R.id.txtTestNone);
        if (testCount > 0) {
            if (testCount > 3) testCount = 3;

            findViewById(R.id.txtTestHistory).setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.txtTestHistory).setVisibility(View.GONE);
            textView.setText(mContext.getResources().getString(R.string.no_test_history));
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void ShowTestResult() {
        PetInfo info;
        info = AppGlobals.INSTANCE.getPetInfo(AppGlobals.INSTANCE.getCurrentPage());
        if (info == null) {
            CommonUtils.alertOK(MainActivity.this, mContext.getResources().getString(R.string.tab_result), mContext.getResources().getString(R.string.no_reg_pet));
            return;
        }else if (info.getTestHistorySize() == 0) {
            CommonUtils.alertOK(MainActivity.this, mContext.getResources().getString(R.string.tab_result), mContext.getResources().getString(R.string.no_test_history));
            return;
        }
        Intent intent = new Intent(mContext, TestResult.class);
        intent.putExtra("list_index",0);
        startActivity(intent);
    }

    private void GetPetUrineHistory(int index) {
        SoapClient client;
        AppGlobals.INSTANCE.setCurrentPage(index);
        PetInfo info = AppGlobals.INSTANCE.getPetInfo(index);
        if (info == null) return;
        nCommType = 1;
        Log.e(tag, "Pet Index: " + info.getPetIndex());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.PET_URINE_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.GET_PET_URINE_HISTORY);
            jsonObject.put("sEmail", AppGlobals.INSTANCE.getEmail());
            jsonObject.put("sValues", info.getPetIndex());
            new HttpAsyncTask(MainActivity.this, HttpString.GET_PET_URINE_HISTORY, jsonObject, mHandler).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AddTestHistory(String data) {
        SoapClient client;
        PetInfo info;
        String testResult;
        int currentPage;
        currentPage = viewPager.getCurrentItem();
        info = AppGlobals.INSTANCE.getPetInfo(currentPage);
        AppGlobals.INSTANCE.setCurrentPage(currentPage);
        testResult = info.getPetIndex();
        testResult += "," + data;
        nCommType = 0;
        Log.e(tag, AppGlobals.INSTANCE.getEmail() + ", test result: " + testResult);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.PET_URINE_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.ADD_PET_URINE_RESULT);
            jsonObject.put("sEmail", AppGlobals.INSTANCE.getEmail());
            jsonObject.put("sValues", testResult);
            new HttpAsyncTask(MainActivity.this, HttpString.ADD_PET_URINE_RESULT, jsonObject, mHandler).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    JSONObject jsonObj = mJsonResponseData;
                    if(mNowConnectionAPI.equals(HttpString.ADD_PET_URINE_RESULT))  // 검사결과 저장
                    {
                        Log.e("kwon", "ADD_PET_URINE_RESULT : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if(result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
                                JSONArray jsonArrData = jsonObj.getJSONArray("data");

                                int count = jsonArrData.length();
                                if (count > 0) {
                                    PetInfo info = AppGlobals.INSTANCE.getPetInfo(AppGlobals.INSTANCE.getCurrentPage());
                                    if (info != null) {
                                        if (info.getTestHistorySize() > 0) info.clearTestHistory();
                                        for (int i = 0; i < count; i++) {
                                            JSONObject data = jsonArrData.getJSONObject(i);
                                            info.addTestHistory(new TestHistory(
                                                    count - i,
                                                    data.getString("OccultBlood"),
                                                    data.getString("Bilirubin"),
                                                    data.getString("Urobilinogen"),
                                                    data.getString("Ketones"),
                                                    data.getString("Protein"),
                                                    data.getString("Nitrite"),
                                                    data.getString("Glucose"),
                                                    data.getString("pH"),
                                                    data.getString("SpecificGravity"),
                                                    data.getString("Leukocytes"),
                                                    data.getString("CreateTime")));
                                        }
                                    }
                                }
                                UpdateTestList();
                                if (nCommType == 0) ShowTestResult();
                            }else if(result_code.equals("fail")){ // 애러
                                CommonUtils.alertOK(MainActivity.this, mContext.getResources().getString(R.string.data_receive), msgStr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mJsonResponseData = new JSONObject();
                    }else if(mNowConnectionAPI.equals(HttpString.GET_PET_URINE_HISTORY)){  // 검사결과 불러오기
                        mDialog.dismiss();
                        Log.e("kwon", "GET_PET_URINE_HISTORY : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if(result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
                                JSONArray jsonArrData = jsonObj.getJSONArray("data");
                                int count = jsonArrData.length();
                                PetInfo info = AppGlobals.INSTANCE.getPetInfo(AppGlobals.INSTANCE.getCurrentPage());
                                if (count > 0) {
                                    if (info != null) {
                                        if (info.getTestHistorySize() > 0) info.clearTestHistory();
                                        for (int i = 0; i < count; i++) {
                                            JSONObject data = jsonArrData.getJSONObject(i);
                                            info.addTestHistory(new TestHistory(
                                                    count - i,
                                                    data.getString("OccultBlood"),
                                                    data.getString("Bilirubin"),
                                                    data.getString("Urobilinogen"),
                                                    data.getString("Ketones"),
                                                    data.getString("Protein"),
                                                    data.getString("Nitrite"),
                                                    data.getString("Glucose"),
                                                    data.getString("pH"),
                                                    data.getString("SpecificGravity"),
                                                    data.getString("Leukocytes"),
                                                    data.getString("CreateTime")));
                                        }
                                    }
                                }else{
                                    info.clearTestHistory();
                                }
                                UpdateTestList();
                                if (nCommType == 0) ShowTestResult();
                            }else if(result_code.equals("fail")){ // 애러
                                CommonUtils.alertOK(MainActivity.this, mContext.getResources().getString(R.string.data_receive), msgStr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
    // 소변검사결과 reoponse
    public class serverResponseReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "serverResponseReceiver");
            String data = null;
            data = intent.getStringExtra("DATA");
            Log.e(tag, "data: " + data);
            if (intent.getBooleanExtra("RESULT", true)) {
                String[] split = data.split(",");
                Log.e("split", "split = " + split.toString());
                if ("0".equals(split[0])) {
                    CommonUtils.alertOK(MainActivity.this, mContext.getResources().getString(R.string.data_receive), split[1]);
                } else {
                    int count = Integer.parseInt(split[1]);
                    if (count > 0) {
                        String[] column;
                        PetInfo info = AppGlobals.INSTANCE.getPetInfo(AppGlobals.INSTANCE.getCurrentPage());
                        if (info != null) {
                            if (info.getTestHistorySize() > 0) info.clearTestHistory();
                            for (int i = 0; i < count; i++) {
                                //Log.e("TTTTTT", "data: " + split[2 + i]);
                                column = split[2 + i].split("\\|");
                                info.addTestHistory(new TestHistory(count - i, column[1], column[2], column[3], column[4], column[5], column[6], column[7], column[8], column[9], column[10], column[11]));
                                TestHistory history = info.getTestHistory(i);
                            }
                        }
                    }
                    UpdateTestList();
                    if (nCommType == 0) ShowTestResult();
                }
            } else {
                CommonUtils.ShowToast(mContext, "" + data);
            }
        }
    }

    public class addPetReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            AddPet();
        }
    }

    public class receiveMatchingIndex extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "match index: " + intent.getStringExtra("MATCHING_INDEX"));

            //String data = "2,6,10,16,21,25,29,34,40,48";
            String data = intent.getStringExtra("MATCHING_INDEX");
            String[] split;
            String szData = "";
            int val = 0;
            split = data.split(",");
            if (split.length == 10) {
                szData += (Integer.parseInt(split[0]) - val + 1) + ",";
                val += 5;
                szData += (Integer.parseInt(split[1]) - val + 1) + ",";
                val += 2;
                szData += (Integer.parseInt(split[2]) - val + 1) + ",";
                val += 5;
                szData += (Integer.parseInt(split[3]) - val + 1) + ",";
                val += 6;
                szData += (Integer.parseInt(split[4]) - val + 1) + ",";
                val += 7;
                szData += (Integer.parseInt(split[5]) - val + 1) + ",";
                val += 5;
                szData += (Integer.parseInt(split[6]) - val + 1) + ",";
                val += 7;
                szData += (Integer.parseInt(split[7]) - val + 1) + ",";
                val += 6;
                szData += (Integer.parseInt(split[8]) - val + 1) + ",";
                val += 4;
                if (split[9].length() != 2) {
                    CommonUtils.alertOK(mContext, mContext.getResources().getString(R.string.urine_test), mContext.getResources().getString(R.string.dialog_retake));
                    return;
                }
                szData += (Integer.parseInt(split[9]) - val + 1);

                split = szData.split(",");
                szData = split[5];
                szData +="," + split[8];
                szData +="," + split[2];
                szData +="," + split[7];
                szData +="," + split[3];
                szData +="," + split[1];
                szData +="," + split[9];
                szData +="," + split[4];
                szData +="," + split[6];
                szData +="," + split[0];

                Log.d("test", "szData : " +  szData);
                AddTestHistory(szData);
            }else {
                Log.e(tag, "receiveMatchingIndex: data index is not ten: " + split.length);
            }
        }
    }

    public class finishAtivityReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "foreced finish()");
            finish();
        }
    }
    String mLanguage = "ko";
    // 하단 탭 메뉴 이벤트
    @SuppressLint("WrongConstant")
    public void btnShowMenu(View v) {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    public void btnShowTestResult(View v) {
        ShowTestResult();
    }

    public void btnModifyPet(View v) {

        /**
         * 210114 기존 처럼 바로 수정할 수 있게 수정
         * */

        btnAddPet(v);


//        if(mLanguage.equals("ko"))  // 한국어 버전일때만 스캔관련 팝업
//        {
//            if(mLinearContentMenu.getVisibility() == View.VISIBLE) {
//                mLinearContentMenu.setVisibility(View.GONE);
//                mRelativeAddMenu.setVisibility(View.GONE);
//            }else{
//                mLinearContentMenu.setVisibility(View.VISIBLE);
//                mRelativeAddMenu.setVisibility(View.GONE);
//            }
//        }else{
//            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
//            ab.setMessage(getResources().getString(R.string.setting_update_nest));
//            ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {@Override public void onClick(DialogInterface dialog, int which) {}});
//            ab.show();
//        }



    }

    public void btnGoHome(View v) {
        // 기존 홈 버튼 기능 없애고 그 자리에 펫 추가버튼으로 변경
        AddPet();
    }

    public  void AddPet() {
        Intent intent = new Intent(mContext, AddPet.class);
        startActivityForResult(intent, Constants.REQUEST_CODE_ADD_PET);
    }

    public void btnAddPet(View v) {
        if (AppGlobals.INSTANCE.getPetCount() > 0) {
            try {
                AppGlobals.INSTANCE.setCurrentPage(viewPager.getCurrentItem());
                Intent intent = new Intent(mContext, ModifyPet.class);
                startActivityForResult(intent, Constants.REQUEST_CODE_MODIFY_PET);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
        ab.setMessage(getResources().getString(R.string.app_finish));
        ab.setPositiveButton(getResources().getText(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        ab.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) {}});
        ab.show();
    }

    @Override
    public void onStart(){
        super.onStart();
        //각종 리스너 등록
    }

    int mPetCount = 0;
    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume","onresume");
        Log.d("test","onresume");
        //사용자에게 보여질 데이터 등 가져오기
        if(imgTab5 != null) {
            imgTab5.setImageResource(R.drawable.tab_home);

            //if (AppGlobals.INSTANCE.getPetCount() > 0) GetPetUrineHistory(0);
            if (!bLoading) {
                if(AppGlobals.petInfoList != null) {
                    int petCnt = AppGlobals.INSTANCE.getPetCount();

                    if (petCnt == 0) {
                        petCnt = 1;
                        ClearTestList();
                    }

                    LoadViewPager(petCnt);

                    bLoading = true;
                }else{
                    appFInish();
                }
            }
        }else{
            appFInish();
        }
    }

    private void appFInish()
    {
//        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
//        ab.setMessage(getResources().getString(R.string.refresh));
//        ab.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                intent = new Intent(MainActivity.this, IntroActivity.class);
//                startActivity(intent);
//            }
//        });
//        ab.show();
        intent = new Intent(MainActivity.this, IntroActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause(){
        super.onPause();
        //사용자에게 보여지지 않을 때 임시로 뭔가 저장하거나 자원 해제 등 작성
    }


    /*
     * When the activity is destroyed, make a call to super class
     */
//    @Override
////    public void onDestroy() {
////        super.onDestroy();
////        if(serverResponseReceiver != null)
////        {
////            unregisterReceiver(serverResponseReceiver);
////            unregisterReceiver(addPetReceiver);
////            unregisterReceiver(receiveMatchingIndex);
////            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(finishAtivityReceiver);
////        }
////    }



    private Intent intent = null;
    private boolean mIsLogoutClicked = false;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId())
            {
                case R.id.menu_1:
                    intent = new Intent(mContext, ProfileActivity.class);
                    break;
                case R.id.menu_2:
                    // 삭제될 메뉴
//                    intent = new Intent(Constants.ACTION_ADD_PET);
//                    AppGlobals.INSTANCE.getContext().sendBroadcast(intent);
                    break;
                case R.id.menu_3:
                    intent = new Intent(mContext, FaqActivity.class);
                    break;
                case R.id.menu_4:
                    intent = new Intent(mContext, SettingActivity.class);
                    break;
                case R.id.menu_5:
                    intent = new Intent(mContext, ServiceCenterActivity.class);
                    break;
                case R.id.menu_6:
                    intent = new Intent(mContext, PrivacyActivity.class);
                    break;
                case R.id.menu_7:
                    intent = new Intent(mContext, ServiceTermsActivity.class);
                    break;
                case R.id.menu_8:
                    mIsLogoutClicked = true;
                    AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                    ab.setMessage(getResources().getString(R.string.menu_logout));
                    ab.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            mIsLogoutClicked = false;
                        }
                    });
                    ab.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent livedActivity = new Intent(Constants.ACTION_FINISH_ATIVITY);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(livedActivity);

                            AppGlobals.INSTANCE.setEmail(null);
                            intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("LOGOUT", "true");
                            startActivity(intent);
                            finish();
                        }
                    });
                    ab.show();
                    break;
                case R.id.menu_9:
                    intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
                    break;
            }
            if(!mIsLogoutClicked)
            {
                startActivity(intent);
            }
        }
    };
}

