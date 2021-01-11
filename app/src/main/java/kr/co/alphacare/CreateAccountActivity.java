package kr.co.alphacare;


import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.alphacare.utils.AES256Cipher;
import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.Constants;
import kr.co.alphacare.utils.HttpAsyncTask;
import kr.co.alphacare.utils.HttpString;
import kr.co.alphacare.utils.SoapClient;


public class CreateAccountActivity extends BaseActivity {
    private final String tag = "CreateAccountActivity";
    private Context mContext;
    EditText editName, editEmail, editPassword, editPasswordConfirm;
    private TextView txtMsg;
    private RelativeLayout rlAlertMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_create_account);
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

        editName = findViewById(R.id.editName);
        editName.addTextChangedListener(textWatcher);
        editEmail = findViewById(R.id.editEmail);
        editEmail.addTextChangedListener(textWatcher);
        editPassword = findViewById(R.id.editPassword);
        editPassword.addTextChangedListener(textWatcher);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
        editPasswordConfirm.addTextChangedListener(textWatcher);
        txtMsg = findViewById(R.id.txtMsg);
        rlAlertMsg = findViewById(R.id.rlAlertMsg);
    }

    private boolean CheckInputData() {
        boolean valid = false;

        if (editName.length() == 0) {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_name));
            editName.requestFocus();
        }
        else if (editEmail.length() == 0)  {
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
        }
        else if (!CommonUtils.validatePassword(editPassword.getText().toString())) {
            txtMsg.setText(mContext.getResources().getString(R.string.password_format_invalid));
            editPassword.requestFocus();
        }
        else if (editPasswordConfirm.length() == 0)  {
            txtMsg.setText(mContext.getResources().getString(R.string.enter_password_confirm));
            editPasswordConfirm.requestFocus();
        }
        else if (!CommonUtils.validatePassword(editPasswordConfirm.getText().toString())) {
            txtMsg.setText(mContext.getResources().getString(R.string.password_format_invalid));
            editPasswordConfirm.requestFocus();
        }
        else if (!editPassword.getText().toString().equals(editPasswordConfirm.getText().toString()))
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

    public void btnCreateAccount(View v) {
        if (CheckInputData()) {
            CreateAccount();
        }
    }

    private  void CreateAccount() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.ACCOUNT_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.CREATE_ACCOUNT);
            jsonObject.put("sEmail", editEmail.getText().toString());
            jsonObject.put("sPasswd", editPassword.getText().toString());
            new HttpAsyncTask(CreateAccountActivity.this, HttpString.CREATE_ACCOUNT, jsonObject, mHandler).execute();
//            client = new SoapClient(mContext, Constants.ACTION_CREATE_ACCOUNT_ACTIVITY);
//            client.CreateAccount(editEmail.getText().toString(), AES256Cipher.AES_Encode(editPassword.getText().toString(), Constants.AES_KEY), editName.getText().toString());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CreateAccountOK() {
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_DATA_EMAIL, editEmail.getText().toString());
        setResult(RESULT_OK, intent);
        Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.join_ok), Toast.LENGTH_SHORT).show();
        finish();
    }


    public class serverResponseReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {

            Log.e(tag, "serverResponseReceiver");

            String data;

            data = intent.getStringExtra("DATA");

            if (intent.getBooleanExtra("RESULT", true)) {

                String[] split = data.split(",");
                if ("0".equals(split[0])) {
                    rlAlertMsg.setVisibility(View.VISIBLE);
                    txtMsg.setText(split[1]);
                }else {
                    CreateAccountOK();
                }

            }else {
                CommonUtils.ShowToast(mContext, "" + data);

            }
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
                    if(mNowConnectionAPI.equals(HttpString.CREATE_ACCOUNT))  // 회원가입하기
                    {
                        JSONObject jsonObj = mJsonResponseData;
                        Log.e("CreateAccount", "PET_INFO_SERVICE : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if(result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
                                CreateAccountOK();
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
}
