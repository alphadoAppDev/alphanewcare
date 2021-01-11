package kr.co.alphacare.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.co.alphacare.BaseActivity;

import static kr.co.alphacare.BaseActivity.ERROR;
import static kr.co.alphacare.BaseActivity.OK;

public class HttpAsyncTask extends AsyncTask<String, String, String> {

  private String mApiStr = "";
  private JSONObject mJSONObj;
  private Handler mHandler;
  private StringBuffer mSbf;
  private Context mContext;
  private JSONObject mJoinJsonResponse;

  private Exception mException;
  public static HttpURLConnection http = null;

  public HttpAsyncTask(Context conttext, String api, JSONObject jsonObject, Handler handler)
  {
    this.mContext = conttext;
    this.mApiStr = api;
    this.mJSONObj = jsonObject;
    this.mHandler = handler;
  }

  @Override
  protected String doInBackground(String... strings) {
    BaseActivity.mNowConnectionAPI = mApiStr;
    try{
      String baseUrl = HttpString.BASE_URL;
      URL url = new URL(baseUrl);
      http = (HttpURLConnection) url.openConnection();
      http.setDoOutput(true);
      http.setDoInput(true);
      http.setDefaultUseCaches(false);
      http.setRequestMethod("POST");
      http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
      http.setRequestProperty("Cipher-Enable", "True");

      // request
      OutputStream os = null;
      os = http.getOutputStream();
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      bw.write(mJSONObj.toString());
      bw.flush();
      bw.close();
      os.close();

      if(http.getResponseCode() == http.HTTP_OK)
      {
        InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
        BufferedReader reader = new BufferedReader(tmp);
        StringBuilder builder = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null)
        {
          builder.append(str + "\n");
        }
        mJoinJsonResponse = new JSONObject(builder.toString());
      }
    }catch (Exception e)
    {
      mException = e;
      Log.e("mException", "mException = " + mException.toString());
    }
    return "ok";
  }

  @Override
  protected void onPostExecute(String s) {
    if(mException != null)
    {
      handlerSetting(ERROR);
      return;
    }
    else
    {
      handlerSetting(OK);
    }
  }

  private void handlerSetting(int what)
  {
    Log.e("handlerResponse", "what = " + what);
    Message msg = mHandler.obtainMessage();
    msg.what = what;
    msg.obj = mJoinJsonResponse;
    mHandler.sendMessage(msg);
  }
}
