package kr.co.alphanewcare.utils;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PetInfo implements Cloneable{
  int petNumber;
  private String petName;
  private int petType;
  private String petID;
  private String petIndex;
  private String petImagePath;
  private String birthDay;
  private String sex;
  private String petStatus;
  private String weight;
  private int breed;
  private String neutralization;
  private int inoculation;

  private int walkCount;
  private int deficationCount;
  private int urinationCount;
  private List<TestHistory> testHistoryList;

  public PetInfo() {
    testHistoryList = new ArrayList<>();
  }


  public String GetPetInfo() {
    String result = "";

    result += petName + ",";
    result += petType + ",";
    result += petID + ",";
    result += petIndex + ",";
    result += petImagePath + ",";
    result += birthDay + ",";
    result += sex + ",";
    result += petStatus + ",";
    result += weight + ",";
    result += breed + ",";
    result += neutralization + ",";
    result += inoculation + ",";
    result += walkCount + ",";
    result += deficationCount + ",";
    result += urinationCount;

    return result;
  }

  public void printTestHistory() {
    TestHistory history;
    int count = testHistoryList.size();
    for (int i = 0; i < count; i++) {
      history = testHistoryList.get(i);
      Log.e("AAAAAA", "Numer: " + history.getNumber() + ", data: " + history.getDisplayFormat());
    }
  }

  public void sortTestHistory() {
    //Collections.sort(testHistoryList);
    Collections.sort(testHistoryList, new Comparator<TestHistory>() {
      @Override
      public int compare(TestHistory history1, TestHistory history2) {
        if (history1.getNumber() > history2.getNumber()) return 1;
        else if (history1.getNumber() < history2.getNumber()) return -1;
        else return 0;
      }
    });
  }

  public TestHistory getTestHistory(int index) {
    if (index + 1 > testHistoryList.size()) {
      Log.e("AppGlobals", "no pet info");
      return null;
    }
    return testHistoryList.get(index);
  }

  public void addTestHistory(TestHistory history) {
    testHistoryList.add(history);
  }

  public int getTestHistorySize() {
    return testHistoryList.size();
  }

  public void clearTestHistory() {
    testHistoryList.clear();
  }


  public void setPetNumber(int petNumber) {
    this.petNumber = petNumber;
  }

  public int getPetNumber() {
    return petNumber;
  }

  public void setPetName(String petName) {
    this.petName = petName;
  }
  public String getPetName() { return this.petName; }

  public void setPetType(int petType) {
    this.petType = petType;
  }
  public int getPetType() { return this.petType; }

  public void setPetID(String petID) {
    this.petID = petID;
  }
  public String getPetID() { return this.petID; }

  public void setPetIndex(String petIndex) {
    this.petIndex = petIndex;
  }
  public String getPetIndex() { return this.petIndex; }

  public void setPetImagePath(String petImagePath) {
    this.petImagePath = petImagePath;
  }
  public String  getPetImagePath() { return this.petImagePath; }

  public void setBirthDay(String birthDay) {
    this.birthDay = birthDay;
  }
  public String getBirthDay() { return this.birthDay; }

  public void setSex(String sex) {
    this.sex = sex;
  }
  public String getSex() { return  this.sex; }

  public void setPetStatus(String petStatus) {
    this.petStatus = petStatus;
  }

  public String getPetStatus() {
    return petStatus;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }
  public String getWeight() { return this.weight; }

  public void setBreed(int breed) {
    this.breed = breed;
  }

  public int getBreed() {
    return breed;
  }

  public void setNeutralization(String neutralization) {
    this.neutralization = neutralization;
  }
  public String getNeutralization() { return this.neutralization; }

  public void setInoculation(int inoculation) {
    this.inoculation = inoculation;
  }

  public int getInoculation() {
    return inoculation;
  }

  public void setWalkCount(int walkCount) {
    this.walkCount = walkCount;
  }

  public int getWalkCount() {
    return walkCount;
  }

  public void setUrinationCount(int urinationCount) {
    this.urinationCount = urinationCount;
  }

  public int getUrinationCount() { return urinationCount; }

  public void setDeficationCount(int deficationCount) {
    this.deficationCount = deficationCount;
  }

  public int getDeficationCount() {
    return deficationCount;
  }

  public void UpdatePetInfo(PetInfo info) {
    petName = info.getPetName();
    petType = info.getPetType();
    petID = info.getPetID();
    petIndex = info.getPetIndex();
    petImagePath = info.getPetImagePath();
    birthDay = info.getBirthDay();
    sex = info.getSex();
    petStatus = info.getPetStatus();
    weight = info.getWeight();
    breed = info.getBreed();
    neutralization = info.getNeutralization();
    inoculation = info.getInoculation();
    walkCount = info.getWalkCount();
    deficationCount = info.getDeficationCount();
    urinationCount = info.getUrinationCount();

  }

  public ArrayList<Integer> getColorList(int type, int showPeriod) {
    ArrayList<Integer> color = new ArrayList<Integer>();
    int nTestCount;

    nTestCount = testHistoryList.size();
    for (int i = 0; i < nTestCount; i++) {
      if (testHistoryList.get(nTestCount - (i + 1)).getItemValue(type, showPeriod) != 0) {
        if (testHistoryList.get(nTestCount - (i + 1)).isAlertValue(type))
          color.add(Color.parseColor("#ff0000"));
        else color.add(Color.parseColor("#ffd2d2"));
      }
    }

    return color;
  }

  public int[] getItemValue(int type, int showPeriod) {
    int[] result;
    int[] tmp;
    int value;
    String report;
    int nTestCount;
    int index = 0;

    // showPeriod 1개월,3개월,6개월,1년

    nTestCount = testHistoryList.size();
    tmp = new int[nTestCount];
    for (int i = 0; i < nTestCount; i++) {
      value = testHistoryList.get(nTestCount - (i + 1)).getItemValue(type, showPeriod);
      if (value != 0) tmp[index++] = value;
            /*
            if (value == 0) break;
            else result[i] = value;*/
    }

//        if (index == 0) return null;

    result = new int[index];

    report = "";
    for (int i = 0; i < index; i++) {
      result[i] = tmp[i];
      report += tmp[i] + ", ";
    }
    //Log.e("getItemValue", report);

    return result;
  }

  public String[] getDataList(int showPeriod) {
    String[] result;
    String[] tmp;
    String data;
    String report;
    int nTestCount;
    int index = 0;


    nTestCount = testHistoryList.size();
    tmp = new String[nTestCount];
    for (int i = 0; i < nTestCount; i++) {
      data = testHistoryList.get(nTestCount - (i + 1)).getDateTime(showPeriod);
      if (data != null) tmp[index++] = data;
            /*
            if (data == null) break;
            else result[i] = data;
            */


      //result[i] = testHistoryList.get(nTestCount - (i + 1)).getDate();
      //result[i] = "08/0" + (i + 1) ;
    }

    if (tmp.length == 0) return null;


    result = new String[index];

    report = "";
    for (int i = 0; i < index; i++) {
      result[i] = tmp[i];
      report += tmp[i] + ", ";
    }
    //Log.e("getDataList", report);

    return result;
  }

  public String[] getItemRange(int type) {
    String[] result;

    result = null;

    switch (type) {
      case 1:
        String[] one = {"","음성","","양성(1+)","","양성(2+)","","양성(3+)"};
        result = one;
        break;
      case 2:
        String[] two = {"","음성","","양성(1+)","","양성(2+)","","양성(3+)"};
        result = two;
        break;
      case 3:
        String[] three = {"","0.1","", "1","", "2","", "4","", "8","", "12"};
        result = three;
        break;
      case 4:
        String[] four = {"","음성","","미량","", "양성(1+)","","양성(2+)","","양성(3+)"};
        result = four;
        break;
      case 5: // 단백질
        String[] five = {"","음성","","미량","", "양성(1+)","","양성(2+)","","양성(3+)","", "양성(4+)"};
        result = five;
        break;
      case 6: // 아질산염
        String[] six = {"","음성","","양성"};
        result = six;
        break;
      case 7: // 포도당
        String[] seven = {"","음성","","미량","", "양성(1+)","","양성(2+)","","양성(3+)"};
        result = seven;
        break;
      case 8: // PH
        String[] eight = {"","5.0","", "6.0","", "6.5","", "7.0","", "8.0","", "9.0"};
        result = eight;
        break;
      case 9: // 비중
        String[] nine = {"","1.000","", "1.010","", "1.020","", "1.030","", "1.040","", "1.050","", "1.060","", "1.070"};
        result = nine;
        break;
      case 10:
        String[] ten = {"","음성","","미량","", "양성(1+)","","양성(2+)","","양성(3+)"};
        result = ten;
        break;
      default:
    }

    if (result != null) {
      String report;
      int count = result.length;
      report = "";
      for (int i = 0; i < count; i++) {
        report += result[i] + ", ";
      }
      //Log.e("getItemRange " + type, report);
    }

    return result;
  }


  public Object clone() {
    Object obj = null;
    try {
      obj = super.clone();
    }catch(Exception e){
      e.printStackTrace();
    }
    return obj;
  }




}
