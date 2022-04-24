package kr.co.alphanewcare;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.alphanewcare.utils.AES256Cipher;
import kr.co.alphanewcare.utils.Constants;
import kr.co.alphanewcare.utils.HttpAsyncTask;
import kr.co.alphanewcare.utils.HttpString;
import kr.co.alphanewcare.utils.SoapClient;

public class ChangePasswordActivity extends BaseActivity {

    private final String tag = "ChangePasswordActivity";
    private Context mContext;
    private TextView txtMsg;
    private EditText editOne, editTwo, editThree;
    private TextView btnChange;
    private RelativeLayout rlAlertMsg;
    private boolean mChangeOk;

    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_change_password);
        mContext = this;
        mDialog = new ProgressDialog(ChangePasswordActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
        // 커스텀 툴바 타이틀
        TextView textView = findViewById(R.id.txtTitle);

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

        mChangeOk = false;

        editOne = findViewById(R.id.editOne);
        editOne.addTextChangedListener(textWatcher);
        editTwo = findViewById(R.id.editTwo);
        editTwo.addTextChangedListener(textWatcher);
        editThree = findViewById(R.id.editThree);
        editThree.addTextChangedListener(textWatcher);

//        txtSubTitle = findViewById(R.id.txtSubTitle);
        txtMsg = findViewById(R.id.txtMsg);
        btnChange = findViewById(R.id.btnChange);
        btnChange.setText(mContext.getResources().getString(R.string.change_password));

        rlAlertMsg = findViewById(R.id.rlAlertMsg);
    }

    private boolean CheckInputData() {
        boolean valid = false;

        if (editOne.length() == 0) {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_current_password));
            editOne.requestFocus();
        }
        else if (editTwo.length() == 0)  {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_new_password));
            editTwo.requestFocus();
        }
        else if (editThree.length() == 0)  {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_password_confirm));
            editThree.requestFocus();
        }
        else if (!editTwo.getText().toString().equals(editThree.getText().toString()))
        {
            txtMsg.setText(mContext.getResources().getString(R.string.new_password_missmatch));
        }
        else valid = true;

        if (!valid)
        {
            rlAlertMsg.setVisibility(View.VISIBLE);
            return false;
        }

        return valid;
    }

    private  void ChangePassword() {
        SoapClient client;
        try {
            String curEncPass, chEncPass;
            curEncPass = AES256Cipher.AES_Encode(editOne.getText().toString(), Constants.AES_KEY);
            chEncPass = AES256Cipher.AES_Encode(editTwo.getText().toString(), Constants.AES_KEY);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.ACCOUNT_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.CHANGE_PASSWORD);
            jsonObject.put("sEmail", AppGlobals.INSTANCE.getEmail());
            jsonObject.put("sCurrPasswd", editOne.getText().toString());
            jsonObject.put("sChgPasswd", editTwo.getText().toString());
            new HttpAsyncTask(ChangePasswordActivity.this, HttpString.CHANGE_PASSWORD, jsonObject, mHandler).execute();

//            client = new SoapClient(mContext, Constants.ACTION_CHANGE_PASSWORD_ACTIVITY);
//            client.ChangePassword(AppGlobals.INSTANCE.getEmail(), curEncPass, chEncPass);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnChangePassword(View v) {
        if (mChangeOk) {
            finish();
        }
        else if (CheckInputData()) {
            ChangePassword();
        }
    }

    public void btnBack(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
                    if(mNowConnectionAPI.equals(HttpString.CHANGE_PASSWORD))  // 비밀번호 변경하기
                    {
                        JSONObject jsonObj = mJsonResponseData;
                        Log.e("kwon", "CHANGE_PASSWORD : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if (result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
//                                JSONObject data = jsonObj.getJSONObject("data");
                                AlertDialog.Builder ab = new AlertDialog.Builder(ChangePasswordActivity.this);
                                ab.setMessage(R.string.password_changed);
                                ab.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                ab.show();
                            } else if (result_code.equals("fail")) { // 애러
                                rlAlertMsg.setVisibility(View.VISIBLE);
                                txtMsg.setText(msgStr);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mJsonResponseData = new JSONObject();
                        break;
                    }
            }
        }
    };
}
