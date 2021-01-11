package kr.co.alphacare;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.Constants;
import kr.co.alphacare.utils.HttpAsyncTask;
import kr.co.alphacare.utils.HttpString;
import kr.co.alphacare.utils.SoapClient;
import okhttp3.Credentials;

public class LoginActivity extends BaseActivity {

    private final String tag = "LoginActivity";
    private Context mContext;
    private  ImageView imgBleState;
    private TextView textBack;
    private TextView txtMsg;
    private CheckBox mCheckBox;
    EditText editEmail, editPassword;
    private boolean bLogout;
    private RelativeLayout rlAlertMsg;
    private boolean mIsChecked = false;

    private final BroadcastReceiver serverResponseReceiver = new serverResponseReceiver();
    private final IntentFilter serverResponseFilter = new IntentFilter(Constants.ACTION_LOGIN_ACTIVITY);

    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_login);
        mContext = this;

        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
        // 커스텀 툴바 타이틀
//        TextView textView = findViewById(R.id.txtTitle);
//        textView.setText(mContext.getResources().getString(R.string.login));
//        findViewById(R.id.imgBack).setVisibility(View.GONE);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                rlAlertMsg.setVisibility(View.INVISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        mCheckBox = findViewById(R.id.checkbox_auto_login);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsChecked = isChecked;
            }
        });
        txtMsg = findViewById(R.id.txtMsg);
        editEmail = findViewById(R.id.editEmail);
        editEmail.addTextChangedListener(textWatcher);
        editPassword = findViewById(R.id.editPassword);
        editPassword.addTextChangedListener(textWatcher);
        rlAlertMsg = findViewById(R.id.rlAlertMsg);

        //editEmail.setText("isong7@hotmail.com");

        registerReceiver(serverResponseReceiver, serverResponseFilter);

        Intent intent = getIntent();
        if (intent.getStringExtra("LOGOUT") != null) bLogout = true;
        else bLogout = false;

    }

    private  void VerifyUser() {
        try {
//            mDialog.show();
            JSONObject jsonObject = new JSONObject();
            try {
                mDialog.show();
                jsonObject.put(HttpString.SERVICE, HttpString.ACCOUNT_SERVICE);
                jsonObject.put(HttpString.MODE, HttpString.VERIFY_USER);
                jsonObject.put("sEmail" , editEmail.getText().toString());
                jsonObject.put("sPasswd" , editPassword.getText().toString());
                new HttpAsyncTask(LoginActivity.this, HttpString.VERIFY_USER, jsonObject, mHandler).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            client = new SoapClient(mContext, Constants.ACTION_LOGIN_ACTIVITY);
//            client.VerifyUser(editEmail.getText().toString(), AES256Cipher.AES_Encode(editPassword.getText().toString(), Constants.AES_KEY));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case Constants.REQUEST_CODE_CREATE_ACCOUNT:
                    editEmail.setText(intent.getStringExtra(Constants.EXTRA_DATA_EMAIL));
                    editPassword.requestFocus();
                    break;
                case Constants.REQUEST_CODE_RESET_PASSWORD:
                    editEmail.setText(intent.getStringExtra(Constants.EXTRA_DATA_EMAIL));
                    editEmail.requestFocus();
                    break;
                default:
                    break;
            }
        }
    }

    public void btnResetPassword(View v) {
        Intent intent = new Intent(mContext, ResetPasswordActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE_RESET_PASSWORD);
    }

    public void btnCreateAccount(View v) {
        Intent intent = new Intent(mContext, CreateAccountActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_ACCOUNT);

    }

    public void btnVerifyUser(View v) {

        boolean valid = false;

        if (editEmail.length() == 0)  {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_email));
            editEmail.requestFocus();
        }
        else if (!CommonUtils.validateEmail(editEmail.getText().toString()))
        {
            txtMsg.setText(mContext.getResources().getString(R.string.email_format_invalid));
            editEmail.requestFocus();
        }
        else if (editPassword.length() == 0)  {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_password));
            editPassword.requestFocus();
        }else valid = true;

        if (!valid) {
            rlAlertMsg.setVisibility(View.VISIBLE);
        }
        else VerifyUser();
    }

    public class serverResponseReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            mDialog.dismiss();
            Log.e(tag, "serverResponseReceiver");

            String data = null;

            data = intent.getStringExtra("DATA");

            if (intent.getBooleanExtra("RESULT", true)) {

                String[] split = data.split(",");
                if ("0".equals(split[0])) {
                    rlAlertMsg.setVisibility(View.VISIBLE);
                    txtMsg.setText(split[1]);
                }else {
                    AuthOK();
                }

            }else {
                CommonUtils.ShowToast(mContext, "" + data);
            }
        }
    }

    private void AuthOK() {
        if(bLogout)
        {
            AppGlobals.INSTANCE.setEmail(editEmail.getText().toString());
            Intent intent = new Intent(mContext, IntroActivity.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent();
            intent.putExtra("RESULT", true);
            intent.putExtra(Constants.EXTRA_DATA_EMAIL, editEmail.getText().toString());
            setResult(RESULT_OK, intent);
            AppGlobals.INSTANCE.setAutoLogin(mIsChecked);
        }
        finish();
    }


    public void btnBack(View v) { }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
        ab.setMessage(getResources().getString(R.string.app_finish));
        ab.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.finishAffinity(LoginActivity.this);
            }
        });
        ab.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) {} });
        ab.show();
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

        unregisterReceiver(serverResponseReceiver);
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
                    if(mNowConnectionAPI.equals(HttpString.VERIFY_USER))  // 로그인
                    {
                        mDialog.dismiss();
                        mJsonResponseData = (JSONObject)msg.obj;
                        JSONObject jsonObj = mJsonResponseData;
                        Log.e("kwon", "VERIFY_USER : " + jsonObj.toString());
                        try {
                            String result_code = "";
                            String msgStr = "";
                            result_code = jsonObj.getString("result");
                            msgStr = jsonObj.getString("msg");

                            if(result_code.equals("ok"))
                            {
                                Log.e("result_code", result_code);
                                JSONObject data = jsonObj.getJSONObject("data");
                                AuthOK();
                            }else if(result_code.equals("fail")){
                                rlAlertMsg.setVisibility(View.VISIBLE);
                                txtMsg.setText(msgStr);
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
}
