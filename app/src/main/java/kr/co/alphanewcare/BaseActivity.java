package kr.co.alphanewcare;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class BaseActivity extends AppCompatActivity {
    public static final int ERROR = -1;  // 통신에러 Code
    public static final int OK = 0;      // 통신성공 Code
    public static JSONObject mJsonResponseData;
    public static String mNowConnectionAPI = "";  // 현재 사용중인 통신 API (같은 Activity에서 여러개의 통신이 있을 경우, response 분기를 받기위함.)
    public static int mMemidx = 0; // 계정 번호(서버에 순차적으로 쌓임)

    public static boolean isChina = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }


}
