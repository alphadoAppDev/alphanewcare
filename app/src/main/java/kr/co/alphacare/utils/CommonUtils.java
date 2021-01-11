
package kr.co.alphacare.utils;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.alphacare.AddPet;
import kr.co.alphacare.MainActivity;
import kr.co.alphacare.R;


public final class CommonUtils {

    final String TAG = "CommonUtils";
    static final String APP_LANG = null;
    static final String BASE_URL = "http://alphado.co.kr";

    // 이메일 정규식
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    // 이메일 검사
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    // 비밀번호 정규식
    private static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$"); // 4자리 ~ 16자리까지 가능
    // 비밀번호 검사
    public static boolean validatePassword(String pwStr) {
        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
        return matcher.matches();
    }

    public static Drawable getImageByType(Context context, int type) {
        Drawable dr = null;

        return dr;
    }

    public static void alertOK(Context context, String title, String msg)
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
            }
        }) ;

        dialog.setCancelable(false);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static void alertOK(final Context context, final MainActivity activity, String title, String msg)
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
                if (activity != null) {
                    activity.AddPet();
                }
            }
        }) ;

        dialog.setCancelable(false);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public static Uri getUriFromPath(Context context, String filePath) {
        Uri uri;

        uri = null;
        try {
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, "_data = '" + filePath + "'", null, null);

            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return uri;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        Log.d("TTTT", "getRealPathFromURI(), path : " + uri.toString());

        cursor.close();
        return path;
    }

    public static Bitmap ResizePicture(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;


            while (true) {
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
    }


    public static List<Map<String, Object>> GetPetTypes(Context context) {
        List<Map<String, Object>> dialogItemList;
        int[] image = {R.drawable.pet, R.drawable.cat};
        String[] text = {context.getResources().getString(R.string.pet_type_dog), context.getResources().getString(R.string.pet_type_cat)};

        dialogItemList = new ArrayList<>();

        for(int i=0;i<image.length;i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("image", image[i]);
            itemMap.put("text", text[i]);

            dialogItemList.add(itemMap);
        }

        return dialogItemList;
    }

    public static List<Map<String, Object>> GetDogKind(Context context) {
        List<Map<String, Object>> dialogItemList;
        int[] image = {R.drawable.pet, R.drawable.pet};
        String[] text = {context.getResources().getString(R.string.dog_golden_retriever), context.getResources().getString(R.string.dog_greyhound)};

        dialogItemList = new ArrayList<>();

        for(int i=0;i<image.length;i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("image", image[i]);
            itemMap.put("text", text[i]);

            dialogItemList.add(itemMap);
        }

        return dialogItemList;
    }

    public static String getPetTypeToString(int type) {
        String result = "";

        switch (type) {
            case 1:
                result = "강아지";
                break;
            case 2:
                result = "고양이";
                break;

        }
        return result;
    }

    public static String getPetKindToString(int type) {
        String result = "";

        switch (type) {
            case 1:
                result = "골든 리트리버";
                break;
            case 2:
                result = "그레이 하운드";
                break;

        }
        return result;
    }





    public static String GetAppVersion(Context context) {
        String ver = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            ver = i.versionName;
        } catch(NameNotFoundException e) {
            ver =  "";
        }

        return ver;
    }


    public static void ShowToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    static String GetCode(String ph)
    {
        if (ph.indexOf("+82") != -1) return "+82";
        else if (ph.indexOf("+1") != -1) return "+1";
        else if (ph.indexOf("+84") != -1) return "+84";
        else if (ph.indexOf("+62") != -1) return "+62";
        else if (ph.indexOf("+81") != -1) return "+81";
        else if (ph.indexOf("+61") != -1) return "+61";
        else if (ph.indexOf("+99") != -1) return "+99"; /* for testing */
        else return null;

    }

    static String GetAppLang(String lang) {
        if (lang.equals("ko_kr") || lang.equals("ko_KR")) lang = "Korean : ko_KR";
        else if (lang.equals("vi_vn") || lang.equals("vi_VN")) lang = "Vietnamese : vi_VN";
        else if (lang.equals("in_id") || lang.equals("in_ID")) lang = "Indonesian : in_ID";
        else if (lang.equals("ja_jp") || lang.equals("in_JP")) lang = "Japanese : ja_JP";
        else lang = "English : en_US";

        return lang;
    }

    static String GetLanguage(String lang) {
        String szLang = "한국어";
        if ("English".equals(lang)) szLang = "영어";

        return szLang;
    }


    static void setUseableEditText(EditText et, boolean useable) {
        et.setClickable(useable);
        et.setEnabled(useable);
        et.setFocusable(useable);
        et.setFocusableInTouchMode(useable);
    }


    public static String phoneNumberHyphenAdd(String ph, boolean mask) {

        String formatNum = "";
        String num;
        String code;

        num = ph;
        code = GetCode(num);
        if (code != null) {
            num = num.replace(code, "0");
        }


        if (num.length() == 11) {
            if (mask) {
                formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
            }else{
                formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
            }
        }else if(num.length()==8){
            formatNum = num.replaceAll("(\\d{4})(\\d{4})", "$1-$2");
        }else{
            if(num.indexOf("02")==0){
                if(mask){
                    formatNum = num.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-****-$3");
                }else{
                    formatNum = num.replaceAll("(\\d{2})(\\d{3,4})(\\d{4})", "$1-$2-$3");
                }
            }else{
                if(mask){
                    formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
                }else{
                    formatNum = num.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
                }
            }
        }

        return formatNum;
    }

    /**
     * 년 월 일 날짜 더하기
     *
     * @param dt(날짜) , y(년) , m(월), d(일)
     * @Exam  addDate("20180910",1,12,1) -->20200911
     * @return String
     */
    public static String addDate(String formater, String dt, int y, int m, int d) throws Exception  {
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format = new SimpleDateFormat(formater);

        Calendar cal = Calendar.getInstance();
        Date date = format.parse(dt);
        cal.setTime(date);
        cal.add(Calendar.YEAR, y);      //년 더하기
        cal.add(Calendar.MONTH, m);     //년 더하기
        cal.add(Calendar.DATE, d);      //년 더하기

        return format.format(cal.getTime());

    }

    public static String getDate(String formater) {
        long mNow;
        Date mDate;

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        SimpleDateFormat mFormat = new SimpleDateFormat(formater, Locale.KOREA);

        return mFormat.format(mDate);
    }

    public static int dateCompare(String formater, String day1, String day2) {
        Date dt1, dt2;
        int result = 0;
        SimpleDateFormat format = new SimpleDateFormat(formater);
        try {
            dt1 = format.parse(day1);
            dt2 = format.parse(day2);
            result = dt1.compareTo(dt2);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void setBackgroundColorAndRetainShape(final int color, final Drawable background) {

        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background.mutate()).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background.mutate()).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background.mutate()).setColor(color);
        }else{
            Log.w("AAAA","Not a valid background type");
        }

    }


    public static String readString(Context context, String fname) {

        String str = null;

        try {
            FileInputStream fin = context.openFileInput(fname);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));

            str = br.readLine();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("CommonUtils", "" + e.getMessage());
        }

        return str;
    }

    public static void writeString(Context context, String fname, int type, String str) {
        try {
            FileOutputStream fout = context.openFileOutput(fname, type);
            PrintWriter pw = new PrintWriter(fout);
            pw.println(str);
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
