package kr.co.alphanewcare.utils;

import java.io.File;

public class FileUtils
{
    public static boolean createDirectory(String dirPath)
    {
        File f = new File(dirPath);
        if(!f.exists())
        {
            if(!f.mkdirs())
            {
                return false;
            }
        }

        return true;
    }

    public static String getExtention(String filePath)
    {
        int index = filePath.lastIndexOf('.');
        return filePath.substring(index+1);
    }

    public static boolean removeFile(String filePath)
    {
        File file = new File(filePath);
        return file.delete();
    }

    public static boolean removeDirectory(String dirPath)
    {
        File file = new File(dirPath);
        if(!file.exists())
        {
            return false;
        }

        File[] childFiles = file.listFiles();
        if(childFiles == null || childFiles.length == 0)
        {
            file.delete();
            return true;
        }

        for(File childFile : childFiles)
        {
            if(childFile.isDirectory())
            {
                removeDirectory(childFile.getAbsolutePath());
            }
            else
            {
                removeFile(childFile.getAbsolutePath());
            }
        }

        return true;
    }
}
