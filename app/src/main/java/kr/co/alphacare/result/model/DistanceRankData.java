package kr.co.alphacare.result.model;

import java.io.Serializable;

public class DistanceRankData implements Serializable
{
    private int index;
    private int rank;
    private float distance;

    public DistanceRankData(int index, float distance)
    {
        this.index = index;
        this.distance = distance;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    public int getRank()
    {
        return rank;
    }

    public String getDataInfo() {
        String result;

        result = "index: " + index;
        result += ", rank: " + rank;
        result += ", dsitance: " + distance;

        return result;
    }
}