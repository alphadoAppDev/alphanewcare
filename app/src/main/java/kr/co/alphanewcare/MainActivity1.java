package kr.co.alphanewcare;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.alphanewcare.graph.GraphActivity;
import kr.co.alphanewcare.main.MainAction;
import kr.co.alphanewcare.main.MainUISetup;
import kr.co.alphanewcare.utils.CommonUtils;
import kr.co.alphanewcare.utils.Constants;
import kr.co.alphanewcare.utils.PetInfo;
import kr.co.alphanewcare.utils.SoapClient;
import kr.co.alphanewcare.utils.TestHistory;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity1 extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home1);
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

        // InitAppGlobals();
        bLoading = false;

        testHistoryList = new ArrayList<>();


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
                UpdatePetBar();
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                }
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

        String[] permission = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        checkPermission(this, permission, REQ_KEY_PERMISSION_CODE);

        init();



        requestReadExternalStoragePermission();
    }

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

    public void showTutorialActivity(URSDefine.PaperMode mode)
    {
        /*
        if (AppGlobals.INSTANCE.getPetCount() > 0) {
            Intent intent = new Intent(this, TutorialActivity.class);
            intent.putExtra(TutorialActivity.REQ_MODE, mode.getValue());
            startActivity(intent);
        }else {
            CommonUtils.alertOK(MainActivity1.this, this,mContext.getResources().getString(R.string.urine_test), mContext.getResources().getString(R.string.no_reg_pet) + " " + mContext.getResources().getString(R.string.use_reg_pet));

            //AddPet();
        }*/
    }

    public void showGraphActivity()
    {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    private void init()
    {/*
        uiSetup = new MainUISetup(this, this);
        action = new MainAction(this, this);

        uiSetup.setAction(action);
        action.setUiSetup(uiSetup);

        uiSetup.setupViews();
        uiSetup.setupButtons();

        registerReceiver(serverResponseReceiver, serverResponseFilter);
        registerReceiver(addPetReceiver, addPetFilter);
        registerReceiver(receiveMatchingIndex, receiveMatchingFilter);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(finishAtivityReceiver, finishAtivityFilter);
        */
    }

    public boolean checkPermission(Activity activity, String[] permissions, int reqCode)
    {
        ArrayList<String> permissionList = new ArrayList<>();

        for(String permission : permissions)
        {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if(result != PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(permission);
            }
        }

        if(permissionList.size() != 0)
        {
            String arr[] = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(activity, arr, reqCode);
            return false;
        }

        return true;
    }

    public boolean isGranted(int[] grantResults)
    {
        boolean isGranted = true;

        for(int result : grantResults)
        {
            if(result != PackageManager.PERMISSION_GRANTED)
            {
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

        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == pagerAdapter.getCount()) {
            pageIndex--;
        }


        pagerAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem (pageIndex);

        if (pageIndex < 0) {
            LoadViewPager(1);
            // ClearTestList();
        }
        else UpdatePetBar();

    }

    private void LoadViewPager(int count) {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout linearLayout;
        int pageIndex;

        for (int i = 0; i < count; i++) {
            linearLayout = (LinearLayout) inflater.inflate(R.layout.view_pager_content, null);

            if (pagerAdapter.getCount() == 0) pageIndex = pagerAdapter.addView(linearLayout, 0);
            else pageIndex = pagerAdapter.addView(linearLayout);

            pagerAdapter.notifyDataSetChanged();
            //viewPager.setCurrentItem(pageIndex, true);

            PetInfo info = AppGlobals.INSTANCE.getPetInfo(pageIndex);
            if (info != null) {
                TextView textView = (TextView) linearLayout.findViewById(R.id.txtName);
                textView.setText(info.getPetName());

                textView = linearLayout.findViewById(R.id.txtRegisterNumber);
                textView.setText((mContext.getResources().getString(R.string.pet_reg_number) + ": " + info.getPetID()));

                textView = linearLayout.findViewById(R.id.txtBirth);
                textView.setText((mContext.getResources().getString(R.string.pet_birth_day) + " " + info.getBirthDay()));

                textView = linearLayout.findViewById(R.id.txtWeight);
                textView.setText((mContext.getResources().getString(R.string.weight) + " " + info.getWeight() + "kg"));

                String realPath = info.getPetImagePath();
                if (realPath.length() > 0) {
                    CircleImageView civ = linearLayout.findViewById(R.id.circleImageView);
                    Uri petImage = CommonUtils.getUriFromPath(mContext, realPath);
                    Log.e(tag, "path:" + realPath);
                    if (petImage != null) {
                        //Bitmap bmp = CommonUtils.ResizePicture(mContext, petImage, 100);
                        Bitmap bmp = resize(mContext, petImage, 100);
                        if (bmp != null)
                            civ.setImageBitmap(bmp);
                    }
                }
            }
        }
        viewPager.setCurrentItem(0, true);
        UpdatePetBar();
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
            PetInfo info = AppGlobals.INSTANCE.getPetInfo(pageIndex);
            if (info != null) {
                TextView textView = (TextView) frameLayout.findViewById(R.id.txtName);
                textView.setText(info.getPetName());

                textView = frameLayout.findViewById(R.id.txtRegisterNumber);
                textView.setText((mContext.getResources().getString(R.string.pet_reg_number) + ": " + info.getPetID()));

                textView = frameLayout.findViewById(R.id.txtBirth);
                textView.setText((mContext.getResources().getString(R.string.pet_birth_day) + " " + info.getBirthDay()));

                textView = frameLayout.findViewById(R.id.txtWeight);
                textView.setText((mContext.getResources().getString(R.string.weight) + " " + info.getWeight() + "kg"));

                String realPath = info.getPetImagePath();
                if (realPath.length() > 0) {
                    CircleImageView civ = frameLayout.findViewById(R.id.circleImageView);
                    Uri petImage = CommonUtils.getUriFromPath(mContext, realPath);
                    //Log.e(tag, "path:" + realPath);
                    if (petImage != null) {
                        //Bitmap bmp = CommonUtils.ResizePicture(mContext, petImage, 100);
                        Bitmap bmp = resize(mContext, petImage, 100);
                        if (bmp != null)
                            civ.setImageBitmap(bmp);
                    }
                }

                UpdatePetBar();
            }
        }
    }

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

            textView = frameLayout.findViewById(R.id.txtRegisterNumber);
            textView.setText((mContext.getResources().getString(R.string.pet_reg_number) + ": " + info.getPetID()));

            textView = frameLayout.findViewById(R.id.txtBirth);
            textView.setText((mContext.getResources().getString(R.string.pet_birth_day) + " " + info.getBirthDay()));

            textView = frameLayout.findViewById(R.id.txtWeight);
            textView.setText((mContext.getResources().getString(R.string.weight) + " " + info.getWeight() + "kg"));

            String realPath = info.getPetImagePath();
            if (realPath.length() > 0) {
                CircleImageView civ = frameLayout.findViewById(R.id.circleImageView);
                Uri petImage = CommonUtils.getUriFromPath(mContext, realPath);
                Log.e(tag, "path:" + realPath);
                //Bitmap bmp = CommonUtils.ResizePicture(mContext, petImage, 100);
                Bitmap bmp = resize(mContext, petImage, 100);
                if (bmp != null)
                    civ.setImageBitmap(bmp);
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

    private void UpdatePetBar() {
        //int count = pagerAdapter.getCount();
        int count = AppGlobals.INSTANCE.getPetCount();
        int selected = viewPager.getCurrentItem();


        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout lyView = (LinearLayout) pagerAdapter.getView(selected);
        LinearLayout ly = (LinearLayout) lyView.findViewById(R.id.lySelectedPet);
        //inflater.inflate(R.layout.select_pet, ly, true);
        if (ly.getChildCount() != 0) ly.removeAllViews();


        Log.e(tag, "count: " + count + ", selected: " + selected);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);
        //ly.setLayoutParams(lp);


        ImageView imgView;
        for (int i = 0; i < count; i++) {
            imgView = (ImageView) inflater.inflate(R.layout.view_pager_indicator, ly, false);
            if (i == selected) imgView.setImageResource(R.drawable.ellipse_blue);
            else imgView.setImageResource(R.drawable.ellipse_gray);
            ly.addView(imgView);
        }

        GetPetUrineHistory(selected);
    }

    private void UpdatePetInfo(View view, int count, int selected) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout ly = (LinearLayout) view.findViewById(R.id.lySelectedPet);
        //inflater.inflate(R.layout.select_pet, ly, true);
        if (ly.getChildCount() != 0) ly.removeAllViews();

        ImageView imgView;
        for (int i = 1; i < count + 1; i++) {
            imgView = (ImageView) inflater.inflate(R.layout.view_pager_indicator, ly, false);
            if (i == selected) imgView.setImageResource(R.drawable.ellipse_blue);
            else imgView.setImageResource(R.drawable.ellipse_gray);
            ly.addView(imgView);
        }
    }


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


    private void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1001);
                // MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case REQ_KEY_PERMISSION_CODE:
            {
                if(isGranted(grantResults))
                {
                    return;
                }

                CommonUtils.alertOK(MainActivity1.this, mContext.getResources().getString(R.string.permission_agree), mContext.getResources().getString(R.string.permission_argee_msg));

            } break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void ClearTestList() {
        TextView textView = findViewById(R.id.txtTestNone);

        textView.setText(mContext.getResources().getString(R.string.no_reg_pet));
        textView.setVisibility(View.VISIBLE);
        findViewById(R.id.txtTestHistory).setVisibility(View.GONE);
    }

    private void UpdateTestList() {
        LinearLayout ll;
        TextView txtTestNumber, txtTestDate, txtTestDay, txtTestNoraml, txtTestAlert;
        ImageView imgTestNormal, imgTestAlert;
        TestHistory history;
        PetInfo petInfo;

        petInfo = AppGlobals.INSTANCE.getPetInfo(viewPager.getCurrentItem());
        if (petInfo == null) {
            Log.e(tag, "UpdateTestList: Pet info is null");
            return;
        }

        //petInfo.sortTestHistory();
        //petInfo.printTestHistory();

        //int testCount = testHistoryList.size();
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
            CommonUtils.alertOK(MainActivity1.this, mContext.getResources().getString(R.string.tab_result), mContext.getResources().getString(R.string.no_reg_pet));
            return;
        }else if (info.getTestHistorySize() == 0) {
            CommonUtils.alertOK(MainActivity1.this, mContext.getResources().getString(R.string.tab_result), mContext.getResources().getString(R.string.no_test_history));
            return;
        }

        Intent intent = new Intent(mContext, TestResult.class);
        startActivity(intent);
    }

    private void GetPetUrineHistory(int index) {
        SoapClient client;

        AppGlobals.INSTANCE.setCurrentPage(index);

        PetInfo info = AppGlobals.INSTANCE.getPetInfo(index);
        if (info == null) return;

        //if (testHistoryList.size() > 0) testHistoryList.clear();


        nCommType = 1;

        Log.e(tag, "Pet Index: " + info.getPetIndex());
        try {
            client = new SoapClient(mContext, Constants.ACTION_MAIN_ACTIVITY);
            client.GetPetUrineHistory(AppGlobals.INSTANCE.getEmail(), info.getPetIndex());
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
            client = new SoapClient(mContext, Constants.ACTION_MAIN_ACTIVITY);
            client.AddPetUrineResult(AppGlobals.INSTANCE.getEmail(), testResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class serverResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "serverResponseReceiver");

            String data = null;
            data = intent.getStringExtra("DATA");

            Log.e(tag, "data: " + data);

            if (intent.getBooleanExtra("RESULT", true)) {

                String[] split = data.split(",");

                if ("0".equals(split[0])) {
                    CommonUtils.alertOK(MainActivity1.this, mContext.getResources().getString(R.string.data_receive), split[1]);
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


                    // UpdateTestList();
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
                val += 4;
                szData += (Integer.parseInt(split[1]) - val + 1) + ",";
                val += 4;
                szData += (Integer.parseInt(split[2]) - val + 1) + ",";
                val += 6;
                szData += (Integer.parseInt(split[3]) - val + 1) + ",";
                val += 5;
                szData += (Integer.parseInt(split[4]) - val + 1) + ",";
                val += 6;
                szData += (Integer.parseInt(split[5]) - val + 1) + ",";
                val += 2;
                szData += (Integer.parseInt(split[6]) - val + 1) + ",";
                val += 5;
                szData += (Integer.parseInt(split[7]) - val + 1) + ",";
                val += 6;
                szData += (Integer.parseInt(split[8]) - val + 1) + ",";
                val += 8;
                if (split[9].length() != 2) {
                    CommonUtils.alertOK(mContext, mContext.getResources().getString(R.string.urine_test), mContext.getResources().getString(R.string.dialog_retake));
                    return;
                }
                szData += (Integer.parseInt(split[9]) - val + 1);

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


    // 하단 탭 메뉴 이벤트
    public void btnShowMenu(View v) {
        Intent intent = new Intent(mContext, MenuActivity.class);
        startActivity(intent);
    }

    public void btnShowTestResult(View v) {
        ShowTestResult();
    }

    /*
    public void btnGoTest(View v) {
        CommonUtils.alertOK(MainActivity.this, "탭 메뉴", "구현중입니다.");
    }*/

    public void btnModifyPet(View v) {
        if (AppGlobals.INSTANCE.getPetCount() > 0) {
            AppGlobals.INSTANCE.setCurrentPage(viewPager.getCurrentItem());

            Intent intent = new Intent(mContext, ModifyPet.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_MODIFY_PET);
        }
    }

    public void btnGoHome(View v) {
        //Intent intent = new Intent(mContext, FaqActivity.class);
        //startActivity(intent);
    }


    public  void AddPet() {
        Intent intent = new Intent(mContext, AddPet.class);
        startActivityForResult(intent, Constants.REQUEST_CODE_ADD_PET);
    }

    public void btnAddPet(View v) {
        AddPet();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onStart(){
        super.onStart();
        //각종 리스너 등록

    }

    @Override
    public void onResume() {
        super.onResume();
        //사용자에게 보여질 데이터 등 가져오기

        imgTab5.setImageResource(R.drawable.tab_home);

        //if (AppGlobals.INSTANCE.getPetCount() > 0) GetPetUrineHistory(0);
        if (!bLoading) {
            int petCnt = AppGlobals.INSTANCE.getPetCount();

            if (petCnt == 0) {
                petCnt = 1;
                //ClearTestList();
            }

            LoadViewPager(petCnt);

            bLoading = true;

        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //사용자에게 보여지지 않을 때 임시로 뭔가 저장하거나 자원 해제 등 작성
    }


    /*
     * When the activity is destroyed, make a call to super class
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serverResponseReceiver);
        unregisterReceiver(addPetReceiver);
        unregisterReceiver(receiveMatchingIndex);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(finishAtivityReceiver);
    }
}
