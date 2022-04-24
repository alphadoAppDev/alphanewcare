package kr.co.alphanewcare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import org.opencv.core.Size;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtils
{
    private static final int MAX_WIDTH      = 4096;
    private static final int MAX_HEIGHT     = 4096;

    public static Bitmap create(String fileName)
    {
        File f = new File(fileName);
        if(!f.exists())
        {
            return null;
        }

        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeFile(fileName);

        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            return bitmap;
        }

        if(bitmap == null)
        {
            return null;
        }

        Bitmap fixedOrientationBitmap = fixedOrientation(bitmap, fileName);
        bitmap = replace(bitmap, fixedOrientationBitmap);

        Bitmap fixedSizeBitmap = fixedMaxSize(bitmap);
        bitmap = replace(bitmap, fixedSizeBitmap);

        return bitmap;
    }

    public static Bitmap create(String file, Bitmap.Config config)
    {
        File f = new File(file);
        if(!f.exists())
        {
            return null;
        }

        Bitmap bitmap = null;
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = config;

            bitmap = BitmapFactory.decodeFile(file, options);
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            return bitmap;
        }

        String ext = FileUtils.getExtention(file);
        if( ext.equals("png") || ext.equals("bmp") )
        {
            return bitmap;
        }

        if(bitmap == null)
        {
            return null;
        }

        Bitmap fixedOrientationBitmap = fixedOrientation(bitmap, file);
        bitmap = replace(bitmap, fixedOrientationBitmap);

        Bitmap fixedSizeBitmap = fixedMaxSize(bitmap);
        bitmap = replace(bitmap, fixedSizeBitmap);

        return bitmap;
    }

    public static Bitmap create(String fileName, int inSampleSize)
    {
        File f = new File(fileName);
        if(!f.exists())
        {
            return null;
        }

        Bitmap bitmap = null;
        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;

            bitmap = BitmapFactory.decodeFile(fileName, options);

        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            return bitmap;
        }

        if(bitmap == null)
        {
            return null;
        }

        Bitmap fixedOrientationBitmap = fixedOrientation(bitmap, fileName);
        bitmap = replace(bitmap, fixedOrientationBitmap);

        Bitmap fixedSizeBitmap = fixedMaxSize(bitmap);
        bitmap = replace(bitmap, fixedSizeBitmap);

        return bitmap;
    }

    public static Bitmap create(byte[] data)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, getOptions());

        if(bitmap == null)
        {
            return null;
        }

        Bitmap fixedSizeBitmap = fixedMaxSize(bitmap);
        bitmap = replace(bitmap, fixedSizeBitmap);

        return bitmap;
    }

    public static Bitmap create(byte[] data, int degree)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, getOptions());

        if(bitmap == null)
        {
            return null;
        }

        Bitmap rotatedBitmap = rotate(bitmap, degree);
        bitmap = replace(bitmap, rotatedBitmap);

        Bitmap fixedSizeBitmap = fixedMaxSize(bitmap);
        bitmap = replace(bitmap, fixedSizeBitmap);

        return bitmap;
    }

    public static Bitmap fixedOrientation(Bitmap bitmap, String fileName)
    {
        ExifInterface exif = null;
        try
        {
            exif = new ExifInterface(fileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return bitmap;
        }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegreee = exifOrientationToDegrees(exifOrientation);

        return rotate(bitmap, exifDegreee);
    }

    private static int exifOrientationToDegrees(int exifOrientation)
    {
        switch(exifOrientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:           return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:          return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:          return 270;
            default:                                            return 0;
        }
    }

    private static Bitmap fixedMaxSize(Bitmap bitmap)
    {
        if(bitmap.getWidth() < MAX_WIDTH && bitmap.getHeight() < MAX_HEIGHT)
        {
            return bitmap;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        if(w > h)
        {
            float ratio = (float) MAX_WIDTH / w;
            h = Math.round(h * ratio);
            w = MAX_WIDTH;
        }
        else
        {
            float ratio = (float) MAX_HEIGHT / h;
            w = Math.round(w * ratio);
            h = MAX_HEIGHT;
        }

        return resize(bitmap, w, h);
    }

    public static Bitmap resize(Bitmap b, int newWidth, int newHeight)
    {
        return Bitmap.createScaledBitmap(b, newWidth, newHeight, false);
    }

    public static Bitmap resize(Drawable d, int size)
    {
        return resize(d, size, size);
    }

    public static Bitmap resize(Drawable d, int newWidth, int newHeight)
    {
        return resize(getBitmap(d), newWidth, newHeight);
    }

    public static Bitmap resize(Bitmap b, int size)
    {
        return resize(b, size, size);
    }

    public static Bitmap replace(Bitmap src, Bitmap dst)
    {
        if(src == dst)
        {
            return src;
        }

        src.recycle();

        return dst;
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if( degrees == 0 )
        {
            return bitmap;
        }

        if( bitmap ==  null )
        {
            return bitmap;
        }

        Matrix m = new Matrix();
        m.setRotate(degrees, (float)bitmap.getWidth()/2, (float)bitmap.getHeight()/2);

        try
        {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m , true);
            if( bitmap != converted )
            {
                bitmap = converted;
            }
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap crop(Bitmap bitmap, Path path)
    {
        if(bitmap == null)
        {
            return null;
        }

        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawColor(Color.TRANSPARENT);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        RectF cropRect = new RectF();
        path.computeBounds(cropRect, false);

        Bitmap croppedBitmap = crop(canvasBitmap, cropRect);
        canvasBitmap = replace(canvasBitmap, croppedBitmap);

        return canvasBitmap;
    }

    public static Bitmap crop(Bitmap bitmap, int left, int top, int cropWidth, int cropHeight)
    {
        Rect cropRect = new Rect(
                left,
                top,
                left + cropWidth,
                top + cropHeight
        );

        return crop(bitmap, cropRect);
    }

    public static Bitmap crop(Bitmap bitmap, RectF cropRectF)
    {
        if (cropRectF == null)
        {
            return null;
        }

        Rect cropRect = new Rect();
        cropRectF.roundOut(cropRect);

        return crop(bitmap, cropRect);
    }

    public static Bitmap crop(Bitmap bitmap, Rect cropRect)
    {
        if(bitmap == null)
        {
            return null;
        }

        if (cropRect == null)
        {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Rect imageRect = new Rect(0, 0, width, height);

        if (cropRect.left < imageRect.left)
        {
            cropRect.left = imageRect.left;
        }

        if (cropRect.top < imageRect.top)
        {
            cropRect.top = imageRect.top;
        }

        if (cropRect.right > imageRect.right)
        {
            cropRect.right = imageRect.right;
        }

        if (cropRect.bottom > imageRect.bottom)
        {
            cropRect.bottom = imageRect.bottom;
        }

        if (cropRect.left > imageRect.right ||
                cropRect.top > cropRect.bottom ||
                cropRect.width() <= 0 ||
                cropRect.height() <= 0)
        {
            return null;
        }

        return Bitmap.createBitmap(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
    }

    public static String saveImageFile(Bitmap bitmap, String dirPath, String fileName)
    {
        if(!FileUtils.createDirectory(dirPath))
        {
            return null;
        }

        String filePath = dirPath+fileName;
        if(saveImageFile(bitmap, filePath))
        {
            return filePath;
        }

        return null;
    }

    public static boolean saveImageFile(Bitmap bitmap, String filePath)
    {
        return saveImageFile(bitmap, filePath, 100);
    }

    public static boolean saveImageFile(Bitmap bitmap, String filePath, int compress)
    {
        try
        {
            FileOutputStream out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, compress, out);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String saveImageFile(byte[] data, String dirPath, String fileName)
    {
        if(!FileUtils.createDirectory(dirPath))
        {
            return null;
        }

        String filePath = dirPath+fileName;
        if(saveImageFile(data, filePath))
        {
            return filePath;
        }

        return null;
    }

    public static boolean saveImageFile(byte[] data, String filePath)
    {
        try
        {
            FileOutputStream outStream = new FileOutputStream(filePath);
            outStream.write(data);
            outStream.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Size getOriginImageSize(String filePath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(filePath).getAbsolutePath(), options);

        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        return new Size(bitmapWidth, bitmapHeight);
    }

    private static BitmapFactory.Options getOptions()
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inMutable = true;
        options.inDither = false;
        options.inScaled = false;

        return options;
    }

    public static Bitmap getBitmap(Drawable d)
    {
        return ((BitmapDrawable)d).getBitmap();
    }
}
