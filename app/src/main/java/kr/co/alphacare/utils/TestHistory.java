package kr.co.alphacare.utils;

import android.util.Log;

public class TestHistory implements Comparable<TestHistory> {
    private int number;
    private int blood;
    private int bilirubin;
    private int urobilinogen;
    private int ketones;
    private int protein;
    private int nitrite;
    private int glucose;
    private int ph;
    private int sg;
    private int leukocytes;
    private String dateTime;
    private String date;
    private String time;

    private int normal;
    private int alert;
    private boolean analysis;

    public TestHistory() {}

    /** * Comparable 인터페이스에 정의된 compareTo 메소드를 오버라이드 합니다. */
    @Override
    public int compareTo(TestHistory board) {
        return this.dateTime.compareTo(board.dateTime);
    }

    public TestHistory(int number, String blood, String bilirubin, String urobilinogen, String ketones, String protein, String nitrite, String glucose, String ph, String sg, String leukocytes, String dateTime) {
        this.number = number;
        this.blood = Integer.parseInt(blood);
        this.bilirubin = Integer.parseInt(bilirubin);
        this.urobilinogen = Integer.parseInt(urobilinogen);
        this.ketones = Integer.parseInt(ketones);
        this.protein = Integer.parseInt(protein);
        this.nitrite = Integer.parseInt(nitrite);
        this.glucose = Integer.parseInt(glucose);
        this.ph = Integer.parseInt(ph);
        this.sg = Integer.parseInt(sg);
        this.leukocytes = Integer.parseInt(leukocytes);
        this.dateTime = dateTime;
        String[] tmp = dateTime.split(" ");
        this.date = tmp[0];
        this.time = tmp[1];

        normal = 0;
        alert = 0;
        analysis = false;
    }

    private double GetPhLevel(int num) {
        double phLevel;

        switch (num) {
            case 1:
                phLevel = 5.0;
                break;
            case 2:
                phLevel = 6.0;
                break;
            case 3:
                phLevel = 6.5;
                break;
            case 4:
                phLevel = 7.0;
                break;
            case 5:
                phLevel = 7.5;
                break;
            case 6:
                phLevel = 8.0;
                break;
            case 7:
                phLevel = 8.5;
                break;
            default:
                phLevel = 6.0;
                break;
        }

        return  phLevel;
    }

    private void Analysis() {
        double phLevel;

        if (blood < 2) normal++;
        else alert++;

        if (bilirubin < 2) normal++;
        else alert++;

        if (urobilinogen < 3) normal++;
        else alert++;

        if (ketones < 3) normal++;
        else alert++;

        if (protein < 3) normal++;
        else alert++;

        if (nitrite < 2) normal++;
        else alert++;

        if (glucose < 3) normal++;
        else alert++;

        phLevel = GetPhLevel(ph);
        if (phLevel >= 6.0 && phLevel <= 7.0) normal++;
        else alert++;

        if (sg >= 2 && sg <= 3) normal++;
        else alert++;

        if (leukocytes < 3) normal++;
        else alert++;

        analysis = true;

    }

    public boolean isAlertValue(int type) {
        boolean alert;

        alert = true;
        switch (type) {
            case 1:
                if (blood < 3) alert = false;
                break;
            case 2:
                if (bilirubin < 3) alert = false;
                break;
            case 3:
                if (urobilinogen < 3) alert = false;
                break;
            case 4:
                if (ketones < 3) alert = false;
                break;
            case 5:
                if (protein < 3) alert = false;
                break;
            case 6:
                if (nitrite < 2) alert = false;
                break;
            case 7:
                if (glucose < 3) alert = false;
                break;
            case 8:
                double phLevel;
                phLevel = GetPhLevel(ph);
                if (phLevel >= 6.0 && phLevel <= 7.0) alert = false;
                break;
            case 9:
                if (sg >= 2 && sg <= 3) alert = false;
                break;
            case 10:
                if (leukocytes < 3) alert = false;
                break;
            default:
                alert = false;
        }

        return alert;
    }

    public int getNumber() { return number; }

    public int getBlood() { return blood; }

    public int getBilirubin() { return bilirubin; }

    public int getUrobilinogen() { return urobilinogen; }

    public int getKetones() { return ketones; }

    public int getProtein() { return protein; }

    public int getNitrite() { return nitrite; }

    public int getGlucose() { return glucose; }

    public int getPh() { return ph; }

    public int getSg() {
        return sg;
    }

    public int getLeukocytes() {
        return leukocytes;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDate() { return this.date; }

    public String getTime() { return this.time; }

    public int getNormalCount() {
        if (!analysis) Analysis();

        return normal;
    }

    public int getAlertCount() {
        if (!analysis) Analysis();

        return alert;
    }

    public String getDisplayFormat() {
        String result;

        result = number + "차 ";
        result += date;
        result += " 의심" + getAlertCount() + " 정상" + getNormalCount();

        return result;
    }

    public String getTestDate() {
        String result;

        result = number + "차 ";
        result += date;

        return result;
    }

    public String getTestDateTime() {
        String result;

        result = getTestDate();
        result += " " + time;


        return result;
    }

    public String getTestResult() {
        String result;

        result = " 의심" + getAlertCount() + " 정상" + getNormalCount();

        return result;
    }

    public String getDateTime(int period) {
        String result;
        boolean bOk;

        switch (period) {
            case 1:
                bOk = isValid(date, 0, -1, 0);
                break;
            case 2:
                bOk = isValid(date, 0, -3, 0);
                break;
            case 3:
                bOk = isValid(date, 0, -6, 0);
                break;
            case 4:
                bOk = isValid(date, -1, 0, 0);
                break;
            case 5:
                bOk = true;
                break;
            default:
                bOk = true;
        }

        if (bOk) result = date;
        else result = null;

        return result;
    }

    private boolean isValid(String date, int year, int month, int day) {
        boolean result = false;

        try {
            String formater = "yyyy-MM-dd";
            String curDate = CommonUtils.getDate(formater);
            String diffDate = CommonUtils.addDate(formater, curDate, year, month, day);
            int compare = CommonUtils.dateCompare(formater, diffDate, date);
            if (compare <= 0) result = true;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getItemValue(int type, int period) {
        int result;

        if (getDateTime(period) == null) return 0;

        switch (type) {
            case 1:
                result = blood;
                break;
            case 2:
                result = bilirubin;
                break;
            case 3:
                result = urobilinogen;
                break;
            case 4:
                result = ketones;
                break;
            case 5:
                result = protein;
                break;
            case 6:
                result = nitrite;
                break;
            case 7:
                result = glucose;
                break;
            case 8:
                result = ph;
                break;
            case 9:
                result = sg;
                break;
            case 10:
                result = leukocytes;
                break;
            default:
                result = 0;
        }
        return result;
    }


}
