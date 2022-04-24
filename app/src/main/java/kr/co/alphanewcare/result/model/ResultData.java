package kr.co.alphanewcare.result.model;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ResultData implements Parcelable
{
    private int type;
    private ArrayList<Rect> litmusRects = new ArrayList<>();
    private ArrayList<Rect> comparisionRects = new ArrayList<>();
    private ArrayList<ArrayList<DistanceRankData>> distanceList = new ArrayList<>();
    private String imagePath;

    public ResultData()
    {

    }

    protected ResultData(Parcel in)
    {
        type = in.readInt();
        litmusRects = in.createTypedArrayList(Rect.CREATOR);
        comparisionRects = in.createTypedArrayList(Rect.CREATOR);
        imagePath = in.readString();
        distanceList = (ArrayList<ArrayList<DistanceRankData>>)in.readSerializable();
    }

    public static final Creator<ResultData> CREATOR = new Creator<ResultData>()
    {
        @Override
        public ResultData createFromParcel(Parcel in)
        {
            return new ResultData(in);
        }

        @Override
        public ResultData[] newArray(int size)
        {
            return new ResultData[size];
        }
    };

    public void setType(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public void setLitmusRects(ArrayList<Rect> litmusRects)
    {
        this.litmusRects = litmusRects;
    }

    public ArrayList<Rect> getLitmusRects()
    {
        return litmusRects;
    }

    public void setComparisionRects(ArrayList<Rect> comparisionRects)
    {
        this.comparisionRects = comparisionRects;
    }

    public ArrayList<Rect> getComparisionRects()
    {
        return comparisionRects;
    }

    public void setDistanceList(ArrayList<ArrayList<DistanceRankData>> distanceList)
    {
        this.distanceList = distanceList;
    }

    public ArrayList<ArrayList<DistanceRankData>> getDistanceList()
    {
        return distanceList;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getImagePath()
    {
        return imagePath;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(type);
        dest.writeTypedList(litmusRects);
        dest.writeTypedList(comparisionRects);
        dest.writeString(imagePath);
        dest.writeSerializable(distanceList);
    }
}
