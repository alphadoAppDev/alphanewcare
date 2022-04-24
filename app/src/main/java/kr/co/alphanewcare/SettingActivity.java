package kr.co.alphanewcare;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class SettingActivity extends AppCompatActivity {

    private final String tag = "SettingActivity";
    private Context mContext;
    private Switch switchLogin, switchTestAlarm;

    private TextView mTvBtnDelRecord;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_setting);

        mContext = this;

        // 커스텀 툴바 타이틀
        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(mContext.getResources().getString(R.string.setting));

        InitView();

        mContext = this;
    }

    private void InitView() {
        TextView textView;
        String appVersion;

        switchLogin = findViewById(R.id.switchLogin);
        switchLogin.setChecked(AppGlobals.INSTANCE.getAutoLogin());
        switchLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppGlobals.INSTANCE.setAutoLogin(isChecked);
            }
        });

        switchTestAlarm = findViewById(R.id.switchTestAlarm);
        switchTestAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });


        // 버전
        appVersion = AppGlobals.INSTANCE.getAppVersion();
        textView = findViewById(R.id.txtVersion);
        textView.setText((mContext.getResources().getString(R.string.current_version) + " " + appVersion));
        textView = findViewById(R.id.txtUpdate);
        textView.setText(mContext.getResources().getString(R.string.latest_version));

        mTvBtnDelRecord = findViewById(R.id.tv_btn_del_record);
        mTvBtnDelRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ab = new AlertDialog.Builder(SettingActivity.this);
                ab.setMessage(getResources().getString(R.string.setting_update_nest));
                ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {@Override public void onClick(DialogInterface dialog, int which) {}});
                ab.show();
            }
        });
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
