package kr.co.alphacare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.co.alphacare.listview.CustomAdapter;
import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.Constants;


public class MenuActivity extends AppCompatActivity {

    private final String tag = "MenuActivity";
    private Context mContext;

    private ListView mListView;

    private final BroadcastReceiver finishAtivityReceiver = new finishAtivityReceiver();
    private final IntentFilter finishAtivityFilter = new IntentFilter(Constants.ACTION_FINISH_ATIVITY);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu);

        mContext = this;

        LocalBroadcastManager.getInstance(mContext).registerReceiver(finishAtivityReceiver, finishAtivityFilter);


        ((TextView)findViewById(R.id.txtBirthDay)).setText(AppGlobals.INSTANCE.getEmail());
        showMenuList();

    }

    private void showMenuList(){
        String menuProfile, menuAddPet, menuFAQ, menuSetting, menuCustomer, menuPrivacy, menuServiceTerms;

        menuProfile = mContext.getResources().getString(R.string.menu_profile);
        menuAddPet = mContext.getResources().getString(R.string.menu_add_pet);
        menuFAQ = mContext.getResources().getString(R.string.menu_faq);
        menuSetting = mContext.getResources().getString(R.string.menu_setting);
        menuCustomer = mContext.getResources().getString(R.string.menu_customer_center);
        menuPrivacy = mContext.getResources().getString(R.string.menu_privacy);
        menuServiceTerms = mContext.getResources().getString(R.string.menu_service_terms);

        CustomAdapter mMyAdapter = new CustomAdapter(mContext);

        mMyAdapter.addItem(R.drawable.menu_profile, menuProfile, "", Color.parseColor("#000000"), R.drawable.arrow_right);
        mMyAdapter.addItem(R.drawable.menu_plus, menuAddPet, "", Color.parseColor("#000000"), R.drawable.arrow_right);
        mMyAdapter.addItem(R.drawable.menu_faq, menuFAQ, "", Color.parseColor("#000000"), R.drawable.arrow_right);
        mMyAdapter.addItem(R.drawable.menu_setup, menuSetting, "", Color.parseColor("#000000"), R.drawable.arrow_right);
        mMyAdapter.addItem(0, "", "", 0, 0);
        mMyAdapter.addItem(R.drawable.menu_custom_center, menuCustomer, "", Color.parseColor("#000000"), R.drawable.arrow_right);
        mMyAdapter.addItem(R.drawable.menu_privacy, menuPrivacy, "", Color.parseColor("#000000"), R.drawable.arrow_right);
        mMyAdapter.addItem(R.drawable.menu_terms, menuServiceTerms, "", Color.parseColor("#000000"), R.drawable.arrow_right);

        mListView = findViewById(R.id.lvMenuList);

        /* 리스트뷰에 어댑터 등록 */
        mListView.setAdapter(mMyAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;
                switch (position) {
                    case 0:
                        intent = new Intent(mContext, ProfileActivity.class);
                        break;
                    case 1:
                        intent = new Intent(Constants.ACTION_ADD_PET);
                        // LocalBroadcastManager 쓰면 클릭이 누적됨
                        //LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        AppGlobals.INSTANCE.getContext().sendBroadcast(intent);
                        finish();
                        intent = null;
                        break;
                    case 2:
                        intent = new Intent(mContext, FaqActivity.class);
                        break;
                    case 3:
                        intent = new Intent(mContext, SettingActivity.class);
                        break;
                    case 4:
                        break;
                    case 5:
                        intent = new Intent(mContext, ServiceCenterActivity.class);
                        break;
                    case 6:
                        intent = new Intent(mContext, PrivacyActivity.class);
                        break;
                    case 7:
                        intent = new Intent(mContext, ServiceTermsActivity.class);
                        break;
                }

                if (intent != null) startActivity(intent);


            }
        });

    }

    public void btnShowMenu(View v) {

    }
    public void btnShowTestResult(View v) {

    }

    public void btnGoTest(View v) {

    }

    public void btnDeletePet(View v) {

    }

    public void btnGoHome(View v) {
        finish();
    }

    public class finishAtivityReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "foreced finish()");
            finish();
        }
    }



    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(finishAtivityReceiver);

    }

}
