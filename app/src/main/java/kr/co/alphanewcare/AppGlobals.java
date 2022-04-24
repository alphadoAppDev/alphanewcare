package kr.co.alphanewcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import kr.co.alphanewcare.utils.PetInfo;

public enum AppGlobals {

    INSTANCE;
    private Context context = null;
    private SQLiteDatabase db;
    private String appVersion = null;

    private String email = null;
    private String name = null;
    private boolean autoLogin = false;

    private boolean networkConnected = false;

    private String imagePath = null;

    private int currentPage;
    private int nPetCount = 0;
    public static List<PetInfo> petInfoList;


    public void init(Context ctx, SQLiteDatabase db) {
        this.context = ctx;
        this.db = db;
    }

    public void init(Context ctx) {
        this.context = ctx;

        petInfoList = new ArrayList<>();
        currentPage = 1;
    }

    public Context getContext() { return this.context; }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    public String getAppVersion() {
        if (appVersion == null) {
            try {
                PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appVersion = i.versionName;
            } catch(NameNotFoundException e) {

            }
        }
        return this.appVersion;
    }

    public void printPetInfo() {
        PetInfo info;
        int count = petInfoList.size();
        for (int i = 0; i < count; i++) {
            info = petInfoList.get(i);
            Log.e("AAAAAA", "Number: " + info.getPetNumber() + ", Name: " + info.getPetName());
        }
    }

    public PetInfo getPetInfo(int index) {
        if (index + 1 > petInfoList.size()) {
            Log.e("AppGlobals", "no pet info");
            return null;
        }

        return petInfoList.get(index);
    }

    public void deletePetInfo(int index) {
        /*
        PetInfo info;
        int count = petInfoList.size();

        for (int i = 0; i < count; i++) {
            info = petInfoList.get(i);
            if (info.getPetNumber() == number) {
                petInfoList.remove(i);
                return true;
            }
        }
        return false;
        */
        petInfoList.remove(index);
    }

    public void addPetInfo(PetInfo info) {
        petInfoList.add(info);
    }

    public int getPetCount() {
        return petInfoList.size();
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public String getEmail() {
        if (email == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            email = preferences.getString("EMAIL", null);
        }

        return email;
    }
    public void setEmail(String email) {
        this.email = email;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("EMAIL", email);
        editor.apply();
    }

    public String getName() {
        if (name == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            name = preferences.getString("USER_NAME", null);
        }

        return name;
    }
    public void setName(String name) {
        this.name = name;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("USER_NAME", name);
        editor.apply();
    }

    public boolean getAutoLogin() {
        if (!autoLogin) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            autoLogin = preferences.getBoolean("AUTO_LOGIN", false);
        }

        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("AUTO_LOGIN", autoLogin);
        editor.apply();
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return this.imagePath;
    }







}