package kr.co.alphanewcare;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Tasks;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import kr.co.alphanewcare.utils.CommonUtils;
import kr.co.alphanewcare.utils.Constants;
import kr.co.alphanewcare.utils.HttpAsyncTask;
import kr.co.alphanewcare.utils.HttpString;
import kr.co.alphanewcare.utils.PetInfo;
import kr.co.alphanewcare.utils.SoapClient;
//import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import io.fabric.sdk.android.Fabric;


public class IntroActivity extends BaseActivity {
    private final String tag = "IntroActivity";
    Context mContext;
    Handler h;
    ImageView imageView;
    String email;

    private final BroadcastReceiver serverResponseReceiver = new serverResponseReceiver();
    private final IntentFilter serverResponseFilter = new IntentFilter(Constants.ACTION_INTRO_ACTIVITY);

    private final int MY_REQUEST_CODE = 99999;
    private AppUpdateManager appUpdateManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FCM Log", "알림 메시지: ");
        setContentView(R.layout.intro);
//        Fabric.with(this, new Crashlytics());
        mContext = IntroActivity.this;
        imageView = findViewById(R.id.imgView);

        SharedPreferences pref = getSharedPreferences("info", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();


        if (isChina) {

            if (pref.getBoolean("chinaTerms", false)) {
                setInit();
            } else {
                findViewById(R.id.popupView).setVisibility(View.VISIBLE);
                findViewById(R.id.tvTerms).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ServiceTermsActivity.class);
                        startActivity(intent);
                    }
                });
                findViewById(R.id.tvPrivacy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PrivacyActivity.class);
                        startActivity(intent);
                    }
                });
                findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editor.putBoolean("chinaTerms", true).apply();
                        findViewById(R.id.popupView).setVisibility(View.GONE);
                        setInit();
                    }
                });
            }


        } else {
            setInit();
        }

    }


    private void setInit() {

        InitAppGlobals();
        h = new Handler();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        Log.d("token : ", token);
                    }
                });
        h.postDelayed(irun, 2000);
    }

    Runnable irun = () -> {
        if(InfoPreference.Companion.getIsInitial() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(IntroActivity.this,
                            new String[]{
//                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            }, Constants.REQUEST_CODE_ACCESS_COARSE_LOCATION);
                } else {
                    CheckNextStep();
                }
            } else {
                CheckNextStep();
            }
        } else {
            CheckNextStep();
        }




    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length != 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        CheckNextStep();
                        InfoPreference.Companion.setIsInitial(false);
                    } else {
                        InfoPreference.Companion.setIsInitial(false);
                        CheckNextStep();
                    }
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_VERIFY_EMAIL) {
            email = intent.getStringExtra(Constants.EXTRA_DATA_EMAIL);
            if (intent.getBooleanExtra("RESULT", true)) {
                AppGlobals.INSTANCE.setEmail(email);
                GetPetInfo();
                //StartMainActivity();
            } else finish();
            /*
            if ("FAILED".equals(email)) finish();
            else {
                AppGlobals.INSTANCE.setEmail(email);
                StartMainActivity();
            }*/
        }else if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(IntroActivity.this, "어플 사용을 위해 최신 소프트웨어로 반짝앱을 업데이트 하셔야 합니다.", Toast.LENGTH_LONG).show();
                finishAffinity(); // 앱 종료
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }else {
                setInit();
            }
        }
    }

    private void CheckNextStep() {
        if (email == null) {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_VERIFY_EMAIL);
        } else {

            if (getIntent().hasExtra("logout")) {
                GetPetInfo();
            }else {
                if (AppGlobals.INSTANCE.getAutoLogin()) {
                    GetPetInfo();
                } else {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_CODE_VERIFY_EMAIL);
                }
            }

        }
        //StartMainActivity();
    }

    public void StartMainActivity() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HttpString.SERVICE, HttpString.ACCOUNT_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.GET_ACCOUNT_INFO);
            jsonObject.put("sEmail", AppGlobals.INSTANCE.getEmail());
            new HttpAsyncTask(IntroActivity.this, HttpString.GET_ACCOUNT_INFO, jsonObject, mHandler).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void InitAppGlobals() {
        AppGlobals.INSTANCE.init(mContext);
        email = AppGlobals.INSTANCE.getEmail();
        InfoPreference.Companion.init(this);
    }

    private void GetPetInfo() {
        SoapClient client;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(HttpString.SERVICE, HttpString.PET_INFO_SERVICE);
            jsonObject.put(HttpString.MODE, HttpString.GET_PET_INFO);
            jsonObject.put("sEmail", AppGlobals.INSTANCE.getEmail());
            jsonObject.put("sValues", "A");
            new HttpAsyncTask(IntroActivity.this, HttpString.GET_PET_INFO, jsonObject, mHandler).execute();

//            client = new SoapClient(mContext, Constants.ACTION_INTRO_ACTIVITY);
//            client.GetPetInfo(AppGlobals.INSTANCE.getEmail(), "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class serverResponseReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.e(tag, "serverResponseReceiver");
            String data = null;
            data = intent.getStringExtra("DATA");
            if (intent.getBooleanExtra("RESULT", true)) {
                String[] split = data.split(",");
                if ("0".equals(split[0])) {
                    CommonUtils.alertOK(IntroActivity.this, mContext.getResources().getString(R.string.notice), split[1]);
                    return;
                }
                String[] column;
                PetInfo info;

                int count = Integer.parseInt(split[1]);
                for (int i = 0; i < count; i++) {
                    column = split[i + 2].split("\\|");
                    info = new PetInfo();
                    info.setPetNumber(i);
                    info.setPetName(column[0]);
                    info.setPetType(Integer.parseInt(column[1]));
                    info.setPetID(column[2]);
                    info.setPetIndex(column[3]);
                    info.setPetImagePath(column[4]);
                    info.setBirthDay(column[5]);
                    info.setSex(column[6]);
                    info.setPetStatus(column[7]);
                    info.setWeight(column[8]);
                    info.setBreed(Integer.parseInt(column[9]));
                    info.setNeutralization(column[10]);
                    info.setInoculation(Integer.parseInt(column[11]));
                    info.setWalkCount(Integer.parseInt(column[12]));
                    info.setDeficationCount(Integer.parseInt(column[13]));
                    info.setUrinationCount(Integer.parseInt(column[14]));

                    AppGlobals.INSTANCE.addPetInfo(info);

                    //Log.e("AAAAA", info.GetPetInfo());
                }
                StartMainActivity();
                AppGlobals.INSTANCE.printPetInfo();

            } else {
                CommonUtils.ShowToast(mContext, "" + data);

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        h.removeCallbacks(irun);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        //각종 리스너 등록
        registerReceiver(serverResponseReceiver, serverResponseFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serverResponseReceiver);
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ERROR:
                    try {
                        mJsonResponseData = new JSONObject((String) msg.obj);
                    } catch (Exception e) {
                    }
                    break;
                case OK:
                    if (mNowConnectionAPI.equals(HttpString.GET_PET_INFO))  // 펫정보 가져오기
                    {
                        mJsonResponseData = (JSONObject) msg.obj;
                        JSONObject jsonObj = mJsonResponseData;
                        Log.e("Intro", "PET_INFO_SERVICE : " + jsonObj.toString());
                        try {
                            String result_code = jsonObj.getString("result");
                            String msgStr = jsonObj.getString("msg");
                            if (result_code.equals("ok"))  // 정상
                            {
                                Log.e("result_code", result_code);
                                int count = jsonObj.getInt("count");
                                JSONArray dataArr = new JSONArray();
                                if (count != 0) {
                                    dataArr = jsonObj.getJSONArray("data");
                                }
                                PetInfo info;
                                for (int i = 0; i < count; i++) {
                                    JSONObject data = dataArr.getJSONObject(i);
                                    info = new PetInfo();
                                    info.setPetNumber(i);
                                    info.setPetName(data.getString("PetName"));
                                    info.setPetType(Integer.parseInt(data.getString("PetType")));
                                    info.setPetID(data.getString("PetID"));
                                    info.setPetIndex(data.getString("PetIndex"));
                                    info.setPetImagePath(data.getString("PetImagePath"));
                                    info.setBirthDay(data.getString("PetBirthDay"));
                                    info.setSex(data.getString("Sex"));
                                    info.setPetStatus(data.getString("PetStatus"));
                                    info.setWeight(data.getString("Weight"));
                                    info.setBreed(Integer.parseInt(data.getString("Breed")));
                                    info.setNeutralization(data.getString("Neutralization"));
                                    info.setInoculation(Integer.parseInt(data.getString("Inoculation")));
                                    info.setWalkCount(Integer.parseInt(data.getString("WalkCount")));
                                    info.setDeficationCount(Integer.parseInt(data.getString("DeficationCount")));
                                    info.setUrinationCount(Integer.parseInt(data.getString("UrinationCount")));

                                    AppGlobals.INSTANCE.addPetInfo(info);
                                }
                                StartMainActivity();
                                AppGlobals.INSTANCE.printPetInfo();
                            } else if (result_code.equals("fail")) { // 애러

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mJsonResponseData = new JSONObject();
                    } else if (mNowConnectionAPI.equals(HttpString.GET_ACCOUNT_INFO)) {  // 계정정보가져오기
                        try {
                            mJsonResponseData = (JSONObject) msg.obj;
                            JSONObject jsonObj = mJsonResponseData;
                            JSONObject jsonObjData = (JSONObject) jsonObj.getJSONObject("data");
                            BaseActivity.mMemidx = jsonObjData.getInt("memidx");
                            Log.e("mMemidx", "mMemidx = " + mMemidx);

                            Intent i = new Intent(IntroActivity.this, MainActivity.class);
                            startActivity(i);
                            // fade in 으로 시작하여 fade out 으로 인트로 화면이 꺼지게 애니메이션 추가
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
}