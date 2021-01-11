package kr.co.alphacare;

import android.os.Environment;

import java.io.File;

public class URSConfig
{
    public static String APP_NAME = "URSDetector";
    public static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    public static String APP_PATH = ROOT_PATH + APP_NAME + File.separator;
}
