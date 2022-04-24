package kr.co.alphanewcare.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kr.co.alphanewcare.CareActivity;
import kr.co.alphanewcare.R;

public class CareFragment_1 extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private WebView mWebView;
    private String mCareWebPageUrl = CareActivity.mSubUrl_1;
    /*
    *   강아지 고양이 치아세정방법
        https://blog.naver.com/alphado0313/221978918437

        강아지 고양이 피부세정방법
        https://blog.naver.com/alphado0313/221978925083

        강아지 고양이 귀세정방법
        https://blog.naver.com/alphado0313/221978933390

        강아지 고양이 치아질병 및 치료방법
        https://blog.naver.com/alphado0313/221978922018

        강아지 고양이 피부질병 및 치료방법
        https://blog.naver.com/alphado0313/221978929019

        강아지 고양이 귀질병 및 치료방법
        https://blog.naver.com/alphado0313/221978941318 */
    private ProgressDialog mDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tooth_care_1, null);
        mWebView = view.findViewById(R.id.webView);
        mDialog = new ProgressDialog(getActivity());
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Loading...");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mDialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mDialog.show();
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        mWebView.loadUrl(mCareWebPageUrl);
        return view;
    }
}
