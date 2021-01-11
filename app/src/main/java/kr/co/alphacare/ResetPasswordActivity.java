package kr.co.alphacare;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.alphacare.utils.AES256Cipher;
import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.Constants;
import kr.co.alphacare.utils.HttpAsyncTask;
import kr.co.alphacare.utils.HttpString;
import kr.co.alphacare.utils.PetInfo;
import kr.co.alphacare.utils.SoapClient;
import kr.co.alphacare.utils.TestHistory;

public class ResetPasswordActivity extends BaseActivity {

    private final String tag = "ResetPasswordActivity";
    private Context mContext;
    private  ImageView imgBleState;
    private TextView txtStep;
    private TextView txtResetMsg, txtMsg;
    private EditText editOne, editTwo;
    private TextView btnReset;
    private RelativeLayout rlAlertMsg;
    private ImageView llStep1, llStep2, llStep3;

    // 신규
    private LinearLayout mLinearEdit_1, mLinearEdit_2;
    private TextView mTvEditTitle_1, mTvEditTitle_2;

    int mResetStep; // 0:리셋요청전, 1:인증번호 입력 대기, 2: 패스워드 입력
    String mEmail, mKey, mPass;

    private final BroadcastReceiver serverResponseReceiver = new serverResponseReceiver();
    private final IntentFilter serverResponseFilter = new IntentFilter(Constants.ACTION_RESET_PASSWORD_ACTIVITY);

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auth_reset_password);

        mContext = this;

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

        mResetStep = 0;
        mEmail = "N";
        mKey = "0";
        mPass = "0";


        txtResetMsg = findViewById(R.id.txtResetMsg);
        txtMsg = findViewById(R.id.txtMsg);
        editOne = findViewById(R.id.editOne);
        editOne.addTextChangedListener(textWatcher);
        editTwo = findViewById(R.id.editTwo);
        editTwo.addTextChangedListener(textWatcher);
        btnReset = findViewById(R.id.btnReset);
        rlAlertMsg = findViewById(R.id.rlAlertMsg);
        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);
        llStep3 = findViewById(R.id.llStep3);
        txtStep = findViewById(R.id.txtStep);

        // 신규
        mLinearEdit_1 = findViewById(R.id.linear_edit_1);
        mLinearEdit_2 = findViewById(R.id.linear_edit_2);
        mTvEditTitle_1 = findViewById(R.id.tv_edit_title_1);
        mTvEditTitle_2 = findViewById(R.id.tv_edit_title_2);

        ChangeControlMsg(mResetStep);

        registerReceiver(serverResponseReceiver, serverResponseFilter);

        mDialog = new ProgressDialog(ResetPasswordActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
    }

    private void ChangeControlMsg(int step) {
        switch (step) {
            case 0:
                txtResetMsg.setText(mContext.getResources().getString(R.string.enter_reset_email));
                //txtMsg.setText("If you do not know the email address you entered when signing up, you will not be able to reset your password.");
                editOne.setText("");
                mLinearEdit_2.setVisibility(View.GONE);
                btnReset.setText(mContext.getResources().getString(R.string.btn_send));
                llStep1.setVisibility(View.VISIBLE);
                llStep2.setVisibility(View.GONE);
                llStep3.setVisibility(View.GONE);
                txtStep.setText("STEP 1");
                break;
            case 1:
                txtResetMsg.setText(mContext.getResources().getString(R.string.send_authentication_key));
                //txtMsg.setVisibility(View.GONE);
                txtMsg.setText("");
                editOne.setText("");
//                editOne.setHint(mContext.getResources().getString(R.string.authentication_key));
                mTvEditTitle_1.setText(getResources().getString(R.string.authentication_key));
                btnReset.setText(mContext.getResources().getString(R.string.btn_send));
                llStep1.setVisibility(View.GONE);
                llStep2.setVisibility(View.VISIBLE);
                llStep3.setVisibility(View.GONE);
                txtStep.setText("STEP 2");
                break;
            case 2:
                txtResetMsg.setText(mContext.getResources().getString(R.string.enter_new_password));
                txtMsg.setText("");
                editOne.setText("");
//                editOne.setHint(mContext.getResources().getString(R.string.password));
                mTvEditTitle_1.setText(getResources().getString(R.string.password));
                editOne.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mLinearEdit_2.setVisibility(View.VISIBLE);
                editTwo.setText("");
//                editTwo.setHint(mContext.getResources().getString(R.string.new_password_confirm));
                mTvEditTitle_2.setText(getResources().getString(R.string.new_password_confirm));
                editTwo.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnReset.setText(mContext.getResources().getString(R.string.btn_send));
                llStep1.setVisibility(View.GONE);
                llStep2.setVisibility(View.VISIBLE);
                llStep3.setVisibility(View.GONE);
                txtStep.setText("STEP 3");

                break;
            case 3:
                txtResetMsg.setText(mContext.getResources().getString(R.string.reset_password_ok));
                txtMsg.setText("");
                editOne.setVisibility(View.INVISIBLE);
                mLinearEdit_1.setVisibility(View.GONE);
                mLinearEdit_2.setVisibility(View.GONE);
                btnReset.setText(mContext.getResources().getString(R.string.go_login));
                llStep1.setVisibility(View.GONE);
                llStep2.setVisibility(View.GONE);
                llStep3.setVisibility(View.VISIBLE);
                txtStep.setText("FINAL STEP");
                break;

        }
    }

    private boolean CheckInputData(int step) {
        boolean valid = false;

        switch (step) {
            case 0:
                if (editOne.length() == 0) {
                    txtMsg.setText(mContext.getResources().getString(R.string.enter_email));
                    editOne.requestFocus();
                }else if (!CommonUtils.validateEmail(editOne.getText().toString())) {
                    txtMsg.setText(mContext.getResources().getString(R.string.email_format_invalid));
                    editOne.requestFocus();
                }else valid = true;

                mEmail = editOne.getText().toString();
                break;
            case 1:
                if (editOne.length() == 0)  {
                    txtMsg.setText(mContext.getResources().getString(R.string.enter_authentication_key));
                    editOne.requestFocus();
                }else valid = true;

                mKey = editOne.getText().toString();

                break;
            case 2:
                if (editOne.length() == 0)  {
                    txtMsg.setText(mContext.getResources().getString(R.string.enter_new_password));
                    editOne.requestFocus();
                }else if (!CommonUtils.validatePassword(editOne.getText().toString())) {
                    txtMsg.setText(mContext.getResources().getString(R.string.password_format_invalid));
                    editOne.requestFocus();
                }else if (editTwo.length() == 0)  {
                    txtMsg.setText(mContext.getResources().getString(R.string.enter_password_confirm));
                    editTwo.requestFocus();
                }else if (!CommonUtils.validatePassword(editTwo.getText().toString())) {
                    txtMsg.setText(mContext.getResources().getString(R.string.password_format_invalid));
                    editTwo.requestFocus();
                }else if (!editOne.getText().toString().equals(editTwo.getText().toString()))
                {
                    txtMsg.setText(mContext.getResources().getString(R.string.new_password_missmatch));
                }else valid = true;

                mPass = editOne.getText().toString();

                break;
        }

        if (!valid) rlAlertMsg.setVisibility(View.VISIBLE);
        else rlAlertMsg.setVisibility(View.INVISIBLE);

        return valid;
    }

    private  void ResetPassword() {
        try {
            String encPass;
            if (mResetStep == 2){
                encPass = mPass;
//                encPass = AES256Cipher.AES_Encode(mPass, Constants.AES_KEY);
            }else{
                encPass = mPass;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.ACCOUNT_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.RESET_PASSWORD);
            jsonObject.put("sEmail", mEmail);
            jsonObject.put("sRcvdAuthKey", mKey);
            jsonObject.put("sNewPasswd", encPass);
            jsonObject.put("nStep", mResetStep);
            new HttpAsyncTask(ResetPasswordActivity.this, HttpString.RESET_PASSWORD, jsonObject, mHandler).execute();
            mDialog.show();


//            client = new SoapClient(mContext, Constants.ACTION_RESET_PASSWORD_ACTIVITY);
//            client.ResetPassword(mEmail, mKey, encPass, mResetStep);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            mDialog.dismiss();
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
                    if(mNowConnectionAPI.equals(HttpString.RESET_PASSWORD))  // 패스워드 재설정
                    {
                        Log.e("kwon", "RESET_PASSWORD : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if(result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
                                mResetStep++;
                                ChangeControlMsg(mResetStep);
                            }else if(result_code.equals("fail")){ // 애러
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

    public class serverResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "serverResponseReceiver");

            String data = null;

            data = intent.getStringExtra("DATA");

            if (intent.getBooleanExtra("RESULT", true)) {

                String[] split = data.split(",");
                if ("0".equals(split[0])) {
                    rlAlertMsg.setVisibility(View.VISIBLE);
                    txtMsg.setText(split[1]);
                }else {
                    mResetStep++;
                    ChangeControlMsg(mResetStep);
                }

            }else {
                CommonUtils.ShowToast(mContext, "" + data);

            }
        }
    }



    public void btnResetPassword(View v) {
        if (mResetStep == 3) {
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_DATA_EMAIL, mEmail);
            setResult(RESULT_OK, intent);

            finish();
        }

        if (CheckInputData(mResetStep)) {
            ResetPassword();
        }
    }

    public void btnBack(View v) {
        if (mResetStep == 3) {
            Intent intent = new Intent();
            intent.putExtra(Constants.EXTRA_DATA_EMAIL, mEmail);
            setResult(RESULT_OK, intent);

        }
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
