package kr.co.alphacare;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.co.alphacare.listview.ProfileListAdapter;
import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.Constants;
import kr.co.alphacare.utils.PetInfo;


public class ProfileActivity extends Activity {

    private final String tag = "ProfileActivity";
    private Context mContext;
    private  TextView textView;


    // 신규
    private ListView mProfileListView;
    private ProfileListAdapter mProfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_prifile);

        mContext = this;

        // 커스텀 툴바 타이틀
        textView = findViewById(R.id.txtTitle);
        textView.setText(mContext.getResources().getString(R.string.menu_profile));

//        // 이름
//        textView = findViewById(R.id.txtName);
//        textView.setText(AppGlobals.INSTANCE.getName());
//
//        // 이메일
        textView = findViewById(R.id.txtEmail);
        textView.setText(AppGlobals.INSTANCE.getEmail());


        // 신규
        mProfileListView = findViewById(R.id.listview_profile);
        mProfileAdapter = new ProfileListAdapter(ProfileActivity.this);
        mProfileListView.setAdapter(mProfileAdapter);

//        ShowRegisteredPet();


        mContext = this;
    }

    // 등록된 반려동물 이름 동적 추가
    public void ShowRegisteredPet() {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout llPetList;
        LinearLayout llNew;
        TextView textView;
        int petCount;
        PetInfo info;

        petCount = AppGlobals.INSTANCE.getPetCount();
        if (petCount == 0) return;

//        llPetList = findViewById(R.id.llPetList);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 20, 0, 0);

        for (int i = 0; i < petCount; i++) {
            info = AppGlobals.INSTANCE.getPetInfo(i);

            if (info != null) {
                llNew = (LinearLayout) inflater.inflate(R.layout.textview_item, null);
                textView = llNew.findViewById(R.id.txtName);
                textView.setText(info.getPetName());
                llNew.setLayoutParams(lp);
//                llPetList.addView(llNew);
            }
        }

    }

    public void btnLogout(View v) {
        //AppGlobals.INSTANCE.setEmail(null);

        Intent livedActivity = new Intent(Constants.ACTION_FINISH_ATIVITY);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(livedActivity);

        AppGlobals.INSTANCE.setEmail(null);
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.putExtra("LOGOUT", "true");
        startActivity(intent);
        finish();
    }

    public void btnChangePassword(View v) {
        Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    public void btnBack(View v) {
        finish();
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

    }

}
