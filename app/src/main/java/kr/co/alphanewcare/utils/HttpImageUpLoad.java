package kr.co.alphanewcare.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import kr.co.alphanewcare.BaseActivity;

import static kr.co.alphanewcare.BaseActivity.ERROR;
import static kr.co.alphanewcare.BaseActivity.OK;

public class HttpImageUpLoad extends AsyncTask<String, String, String> {

  private String mApiStr = "";
  private JSONObject mJSONObj;
  private Handler mHandler;
  private StringBuffer mSbf;
  private int mMemberIdx;
  private Context mContext;
  private JSONObject mJoinJsonResponse;
  private Exception mException;
  private Uri mImageUri;
  private File mFile;
  public static HttpURLConnection http = null;

  public HttpImageUpLoad(Context conttext, String api, File file, int memberIdx, Handler handler)
  {
    this.mContext = conttext;
    this.mApiStr = api;
//        this.mJSONObj = jsonObject;
    this.mHandler = handler;
    this.mFile = file;
    this.mMemberIdx = memberIdx;
  }

  String boundary = "^-----^";
  String LINE_FEED = "\r\n";
  String charset = "UTF-8";
  OutputStream outputStream;
  PrintWriter writer;

  JSONObject result = null;
  @Override
  protected String doInBackground(String... strings) {
    BaseActivity.mNowConnectionAPI = mApiStr;
    try{
      Log.e("sssssssssss","1111111111111");
      URL url = new URL(HttpString.BASE_URL_IMAGE_UPLOAD);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      connection.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);
      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setConnectTimeout(15000);

      Log.e("sssssssssss","222222222");
      outputStream = connection.getOutputStream();
      writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

      /** Body에 데이터를 넣어줘야 할경우 없으면 Pass **/
      writer.append("--" + boundary).append(LINE_FEED);
      writer.append("Content-Disposition: form-data; name=\"memidx\"").append(LINE_FEED);
      writer.append("Content-Type: text/plain; charset=" + charset).append(LINE_FEED);
      writer.append(LINE_FEED);
      writer.append(String.valueOf(mMemberIdx)).append(LINE_FEED);
      writer.flush();
      Log.e("sssssssssss","33333333333");
      /** 파일 데이터를 넣는 부분**/
      writer.append("--" + boundary).append(LINE_FEED);
      writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + mFile.getName() + "\"").append(LINE_FEED);
      writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(mFile.getName())).append(LINE_FEED);
      writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
      writer.append(LINE_FEED);
      writer.flush();

      Log.e("sssssssssss","44444444444");
      FileInputStream inputStream = new FileInputStream(mFile);
      byte[] buffer = new byte[(int)mFile.length()];
      int bytesRead = -1;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      Log.e("sssssssssss","555555555");
      outputStream.flush();
      inputStream.close();
      writer.append(LINE_FEED);
      writer.flush();
      Log.e("sssssssssss","6666666666");
      writer.append("--" + boundary + "--").append(LINE_FEED);
      writer.close();

      Log.e("sssssssssss","777777777");
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();

        try {
          mJoinJsonResponse = new JSONObject(response.toString());
          Log.e("result_json", result.toString());
        } catch (Exception e)
        {

        }
        Log.e("sssssssssss","888888888888");
      } else {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }
        in.close();
        mJoinJsonResponse = new JSONObject(response.toString());
        Log.e("result_json", mJoinJsonResponse.toString());
      }
      Log.e("sssssssssss","999999999999");

    } catch (ConnectException e) {
      e.printStackTrace();
      this.mException = e;
      Log.e("sssssssssss","Exception11111");

    } catch (Exception e){
      e.printStackTrace();
      this.mException = e;
      Log.e("sssssssssss","Exception222222");
    }
    return "ok";
  }
  @Override
  protected void onPostExecute(String s) {
    Log.e("sssssssss", s);
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
