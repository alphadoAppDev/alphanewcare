package kr.co.alphanewcare.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUpload {

    public static Handler mHandler;
    public static RequestBody mRequestBody;
    public static Exception mException;

    public static void send2Server(File file) {
//        mHandler = handler;
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("files", file.getName(),RequestBody.create(MultipartBody.FORM, file)).build();
        Request request = new Request.Builder()
                .url(HttpString.BASE_URL_IMAGE_UPLOAD)
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mException = e;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("FileUpload", response.body().toString());
                InputStreamReader tmp = new InputStreamReader(response.body().byteStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null)
                {
                    builder.append(str + "\n");
                }
//                if(mException != null)
//                {
//                    handlerSetting(ERROR);
//                    return;
//                }
//                else
//                {
//                    handlerSetting(OK);
//                }
            }
        });
    }

    public static void handlerSetting(int what)
    {
        Log.e("handlerResponse", "what = " + what);
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        msg.obj = mRequestBody;
        mHandler.sendMessage(msg);
    }
}
