package kr.co.alphacare;

import kr.co.alphacare.result.model.ResultData;

public class ImageProcessing
{
    static
    {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    public native static int detectURS(int[] imageData, int width, int height, int mode);

    public native static ResultData findURSResult(String imagePath, int cropX, int cropY, int cropW, int cropH, int mode);

    public native static String getVersion();
}
