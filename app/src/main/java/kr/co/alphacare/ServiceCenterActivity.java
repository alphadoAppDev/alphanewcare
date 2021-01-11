package kr.co.alphacare;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.HttpAsyncTask;
import kr.co.alphacare.utils.HttpString;
import kr.co.alphacare.utils.PetInfo;
import kr.co.alphacare.utils.TestHistory;

public class ServiceCenterActivity extends BaseActivity {
    private final String tag = "ServiceCenterActivity";
    private Context mContext;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_service_center);
        mContext = this;
        // 커스텀 툴바 타이틀
        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(mContext.getResources().getString(R.string.menu_customer_center));
        ((TextView) findViewById(R.id.tv_email)).setText(AppGlobals.INSTANCE.getEmail());

        mDialog = new ProgressDialog(ServiceCenterActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
    }

    public void btnSendMessage(View v) {
        TextView textView = findViewById(R.id.txtContent);
        if (textView.getText().length() == 0) {
            CommonUtils.alertOK(mContext, mContext.getResources().getString(R.string.menu_customer_center), mContext.getResources().getString(R.string.enter_inquery));
            return;
        }
       /*
        msg = "문의사항이 접수되었습니다. 빠른 시일내에 (";
        msg += AppGlobals.INSTANCE.getEmail();
        msg += ")로 답변 드리겠습니다.";
        */

        mDialog.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.BOARD_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.WRITE);
            jsonObject.put("memidx", BaseActivity.mMemidx);
            jsonObject.put("boardName", "board_qna");
            jsonObject.put("title", AppGlobals.INSTANCE.getEmail());
            jsonObject.put("content", textView.getText().toString());
            new HttpAsyncTask(ServiceCenterActivity.this, HttpString.WRITE, jsonObject, mHandler).execute();
        } catch (Exception e) {
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
                    if(mNowConnectionAPI.equals(HttpString.WRITE))  // 검사결과 저장
                    {
                        Log.e("kwon", "ServiceCenterActivity : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if(result_code.equals("ok"))  // 정상
                            {
                                String msg_;
                                msg_ = mContext.getResources().getString(R.string.received_inquery);
                                alertOK(mContext, mContext.getResources().getString(R.string.menu_customer_center), msg_);
                            }else if(result_code.equals("fail")){ // 애러
                                CommonUtils.alertOK(ServiceCenterActivity.this, mContext.getResources().getString(R.string.data_receive), msgStr);
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
    private void alertOK(Context context, String title, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cardview_alert_ok, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        Button btnOk;
        TextView txtTitle, txtMsg;

        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText(title);
        txtMsg = view.findViewById(R.id.txtMsg);
        txtMsg.setText(msg);

        btnOk = view.findViewById(R.id.btnOK);
        btnOk.setText("OK");
        btnOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        }) ;

        dialog.setCancelable(false);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void btnBack(View v) {
        finish();
    }
}
