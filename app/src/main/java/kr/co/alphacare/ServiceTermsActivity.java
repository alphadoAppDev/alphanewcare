package kr.co.alphacare;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ServiceTermsActivity extends AppCompatActivity {

    private final String tag = "PrivacyActivity";
    private Context mContext;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebSettings webSettings;
        final String webViewUrl = "http://api.alphadopet.com/Policy/AlphaCare/";
//        final String webViewUrl = "http://121.152.44.12/Policy/AlphaCare/";
        final String webFile = "/terms_of_service.html";

        setContentView(R.layout.menu_terms_of_service);

        mContext = this;

        // 커스텀 툴바 타이틀
        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(mContext.getResources().getString(R.string.menu_service_terms));


        webView = (WebView) findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient()); // 클릭시 새창 안뜨게
        webSettings = webView.getSettings(); //세부 세팅 등록
        webSettings.setJavaScriptEnabled(false); // 웹페이지 자바스클비트 허용 여부
        webSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        webSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        webSettings.setUseWideViewPort(false); // 화면 사이즈 맞추기 허용 여부
        webSettings.setSupportZoom(true); // 화면 줌 허용 여부
        webSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        webSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

        //webSettings.setDefaultFontSize(5);

        String target;
        target = webViewUrl + mContext.getResources().getString(R.string.web_path) + webFile;
        webView.loadUrl(target); // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작

        ((ImageView) findViewById(R.id.iv_btn_top_move)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.scrollTo(0,0);
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
