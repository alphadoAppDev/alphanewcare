package kr.co.alphacare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.alphacare.pets.PetKind;
import kr.co.alphacare.utils.CommonUtils;
import kr.co.alphacare.utils.Constants;
import kr.co.alphacare.utils.HttpAsyncTask;
import kr.co.alphacare.utils.HttpImageUpLoad;
import kr.co.alphacare.utils.HttpString;
import kr.co.alphacare.utils.PetInfo;
import kr.co.alphacare.utils.SoapClient;

import static kr.co.alphacare.utils.CommonUtils.rotateImage;

public class AddPet extends BaseActivity {
  private final String tag = "AddPet";
  Context mContext;
  CircleImageView civ;
  View lyManageState;
  TextView tvFocus;
  TextView txtPetType;
  TextView txtPetKind;
  TextView txtBirthDay;

  int nPetType, nPetKind;
  Uri imageUri;
  PetInfo mPetInfo;
  // ========= 신규 ==========
  private TextView mTvBtnFemale;
  private TextView mTvBtnMale;
  private View mViewBgFemale;
  private View mViewBgMale;
  private final String GENDER_FEMALE = "F";
  private final String GENDER_MALE = "M";
  private String SELETED_GENDER = "F";
  private RelativeLayout mRelativeNeutralization;
  private TextView mTvCheckNeutralization;
  private RelativeLayout mRelativeBreedEtc;

  // 반려동물 추가정보
  private View mBtn_11, mBtn_12, mBtn_13, mBtn_14;
  private View mBtn_21, mBtn_22, mBtn_23, mBtn_24;
  private View mBtn_31, mBtn_32, mBtn_33, mBtn_34;
  private View mBtn_41, mBtn_42, mBtn_43, mBtn_44;
  private LinearLayout mLinear_11, mLinear_12, mLinear_13, mLinear_14;
  private LinearLayout mLinear_21, mLinear_22, mLinear_23, mLinear_24;
  private LinearLayout mLinear_31, mLinear_32, mLinear_33, mLinear_34;
  private LinearLayout mLinear_41, mLinear_42, mLinear_43, mLinear_44;

  // default = 1
  private int ADD_INFO_1 = 1;
  private int ADD_INFO_2 = 1;
  private int ADD_INFO_3 = 1;
  private int ADD_INFO_4 = 1;
  private int ONE = 1;
  private int TWO = 2;
  private int THREE = 3;
  private int FOUR = 4;

  private ProgressDialog mDialog;
  // ========= 신규 ==========
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pet_add);

    mContext = getApplicationContext();

    TextView textView = findViewById(R.id.txtTitle);
    textView.setText(mContext.getResources().getString(R.string.reg_pet));

    civ = findViewById(R.id.circleImageView);

    lyManageState = findViewById(R.id.lyManageState);

    tvFocus = findViewById(R.id.noFocus);
    tvFocus.setFocusableInTouchMode(true);
    tvFocus.requestFocus();

    txtPetType = findViewById(R.id.txtPetType);
    txtPetKind = findViewById(R.id.txtPetKind);
    txtBirthDay = findViewById(R.id.txtBirthDay);

    nPetType = -1;
    nPetKind = -1;

    mPetInfo = new PetInfo();

    // 신규
    mTvBtnFemale = findViewById(R.id.tv_btn_female);
    mTvBtnFemale.setOnClickListener(mOnClickListener);
    mTvBtnMale = findViewById(R.id.tv_btn_male);
    mTvBtnMale.setOnClickListener(mOnClickListener);
    mViewBgFemale = findViewById(R.id.view_bg_female);
    mViewBgMale = findViewById(R.id.view_bg_male);

    mRelativeNeutralization = findViewById(R.id.relative_btn_neutralization);
    mRelativeNeutralization.setOnClickListener(mOnClickListener);
    mTvCheckNeutralization = findViewById(R.id.tv_check_neutralization);
    mRelativeBreedEtc = findViewById(R.id.relative_breed_etc);
    mRelativeBreedEtc.setVisibility(View.GONE);

    // 반려동물 추가정보 버튼들
    mBtn_11 = findViewById(R.id.btn_11);mBtn_12 = findViewById(R.id.btn_12);mBtn_13 = findViewById(R.id.btn_13);mBtn_14 = findViewById(R.id.btn_14);
    mBtn_21 = findViewById(R.id.btn_21);mBtn_22 = findViewById(R.id.btn_22);mBtn_23 = findViewById(R.id.btn_23);mBtn_24 = findViewById(R.id.btn_24);
    mBtn_31 = findViewById(R.id.btn_31);mBtn_32 = findViewById(R.id.btn_32);mBtn_33 = findViewById(R.id.btn_33);mBtn_34 = findViewById(R.id.btn_34);
    mBtn_41 = findViewById(R.id.btn_41);mBtn_42 = findViewById(R.id.btn_42);mBtn_43 = findViewById(R.id.btn_43);mBtn_44 = findViewById(R.id.btn_44);
    mBtn_11.setOnClickListener(mOnClickListener);mBtn_12.setOnClickListener(mOnClickListener);mBtn_13.setOnClickListener(mOnClickListener);mBtn_14.setOnClickListener(mOnClickListener);
    mBtn_21.setOnClickListener(mOnClickListener);mBtn_22.setOnClickListener(mOnClickListener);mBtn_23.setOnClickListener(mOnClickListener);mBtn_24.setOnClickListener(mOnClickListener);
    mBtn_31.setOnClickListener(mOnClickListener);mBtn_32.setOnClickListener(mOnClickListener);mBtn_33.setOnClickListener(mOnClickListener);mBtn_34.setOnClickListener(mOnClickListener);
    mBtn_41.setOnClickListener(mOnClickListener);mBtn_42.setOnClickListener(mOnClickListener);mBtn_43.setOnClickListener(mOnClickListener);mBtn_44.setOnClickListener(mOnClickListener);
    mLinear_11 = findViewById(R.id.linear_1_1);mLinear_12 = findViewById(R.id.linear_1_2);mLinear_13 = findViewById(R.id.linear_1_3);mLinear_14 = findViewById(R.id.linear_1_4);
    mLinear_21 = findViewById(R.id.linear_2_1);mLinear_22 = findViewById(R.id.linear_2_2);mLinear_23 = findViewById(R.id.linear_2_3);mLinear_24 = findViewById(R.id.linear_2_4);
    mLinear_31 = findViewById(R.id.linear_3_1);mLinear_32 = findViewById(R.id.linear_3_2);mLinear_33 = findViewById(R.id.linear_3_3);mLinear_34 = findViewById(R.id.linear_3_4);
    mLinear_41 = findViewById(R.id.linear_4_1);mLinear_42 = findViewById(R.id.linear_4_2);mLinear_43 = findViewById(R.id.linear_4_3);mLinear_44 = findViewById(R.id.linear_4_4);
    // 신규 end

    mDialog = new ProgressDialog(AddPet.this);
    mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    mDialog.setMessage("Loading...");
  }

  private View.OnClickListener mOnClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      switch (v.getId())
      {
        case R.id.tv_btn_female:
          SELETED_GENDER = GENDER_FEMALE;
          selectGender(SELETED_GENDER);
          break;

        case R.id.tv_btn_male:
          SELETED_GENDER = GENDER_MALE;
          selectGender(SELETED_GENDER);
          break;

        case R.id.relative_btn_neutralization:
          if(mTvCheckNeutralization.getVisibility() == View.INVISIBLE)
          {
            mTvCheckNeutralization.setVisibility(View.VISIBLE);
          }else{
            mTvCheckNeutralization.setVisibility(View.INVISIBLE);
          }
          break;

        case R.id.btn_11:
          addInfoLine1(ONE);
          mLinear_11.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_12:
          addInfoLine1(TWO);
          mLinear_12.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_13:
          addInfoLine1(THREE);
          mLinear_13.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_14:
          addInfoLine1(FOUR);
          mLinear_14.setVisibility(View.VISIBLE);
          break;

        case R.id.btn_21:
          addInfoLine2(ONE);
          mLinear_21.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_22:
          addInfoLine2(TWO);
          mLinear_22.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_23:
          addInfoLine2(THREE);
          mLinear_23.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_24:
          addInfoLine2(FOUR);
          mLinear_24.setVisibility(View.VISIBLE);
          break;

        case R.id.btn_31:
          addInfoLine3(ONE);
          mLinear_31.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_32:
          addInfoLine3(TWO);
          mLinear_32.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_33:
          addInfoLine3(THREE);
          mLinear_33.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_34:
          addInfoLine3(FOUR);
          mLinear_34.setVisibility(View.VISIBLE);
          break;

        case R.id.btn_41:
          addInfoLine4(ONE);
          mLinear_41.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_42:
          addInfoLine4(TWO);
          mLinear_42.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_43:
          addInfoLine4(THREE);
          mLinear_43.setVisibility(View.VISIBLE);
          break;
        case R.id.btn_44:
          addInfoLine4(FOUR);
          mLinear_44.setVisibility(View.VISIBLE);
          break;

      }
    }
  };

  private void addInfoLine1(int selectedBtn){
    mLinear_11.setVisibility(View.INVISIBLE);
    mLinear_12.setVisibility(View.INVISIBLE);
    mLinear_13.setVisibility(View.INVISIBLE);
    mLinear_14.setVisibility(View.INVISIBLE);
    ADD_INFO_1 = selectedBtn;
  }
  private void addInfoLine2(int selectedBtn){
    mLinear_21.setVisibility(View.INVISIBLE);
    mLinear_22.setVisibility(View.INVISIBLE);
    mLinear_23.setVisibility(View.INVISIBLE);
    mLinear_24.setVisibility(View.INVISIBLE);
    ADD_INFO_2 = selectedBtn;
  }
  private void addInfoLine3(int selectedBtn){
    mLinear_31.setVisibility(View.INVISIBLE);
    mLinear_32.setVisibility(View.INVISIBLE);
    mLinear_33.setVisibility(View.INVISIBLE);
    mLinear_34.setVisibility(View.INVISIBLE);
    ADD_INFO_3 = selectedBtn;
  }
  private void addInfoLine4(int selectedBtn){
    mLinear_41.setVisibility(View.INVISIBLE);
    mLinear_42.setVisibility(View.INVISIBLE);
    mLinear_43.setVisibility(View.INVISIBLE);
    mLinear_44.setVisibility(View.INVISIBLE);
    ADD_INFO_4 = selectedBtn;
  }

  private void selectGender(String gender)
  {
    if(gender.equals("F"))
    {
      mTvBtnFemale.setTextColor(getColor(R.color.colorWhite));
      mTvBtnMale.setTextColor(getColor(R.color.new_gray_1));
      mViewBgFemale.setVisibility(View.VISIBLE);
      mViewBgMale.setVisibility(View.INVISIBLE);
    }
    else{
      mTvBtnFemale.setTextColor(getColor(R.color.new_gray_1));
      mTvBtnMale.setTextColor(getColor(R.color.colorWhite));
      mViewBgFemale.setVisibility(View.INVISIBLE);
      mViewBgMale.setVisibility(View.VISIBLE);
    }
  }
  private String mPetName;
  private boolean CheckInputValues() {
    String szData = "";
    String szValue;

    // 이름
    szValue = ((EditText)findViewById(R.id.editName)).getText().toString();
    mPetName = ((EditText)findViewById(R.id.editName)).getText().toString();
    if (szValue.length() == 0) {
      CommonUtils.alertOK(AddPet.this, mContext.getResources().getString(R.string.reg_pet), mContext.getResources().getString(R.string.input_name));
      return false;
    }
    mPetInfo.setPetName(szValue);

    // 생년월일
    szValue = ((TextView)findViewById(R.id.txtBirthDay)).getText().toString();
    if (szValue.length() == 0 || mContext.getResources().getString(R.string.pet_birth_date).equals(szValue)) {
      CommonUtils.alertOK(AddPet.this, mContext.getResources().getString(R.string.reg_pet), mContext.getResources().getString(R.string.input_birth_date));
      return false;
    }
    mPetInfo.setBirthDay(szValue);

    // 종류
    if (nPetType < 0) {
      CommonUtils.alertOK(AddPet.this, mContext.getResources().getString(R.string.reg_pet), mContext.getResources().getString(R.string.select_kind));
      return false;
    }
    mPetInfo.setPetType(nPetType);


    // 품종
    if (nPetKind < 0) {
      CommonUtils.alertOK(AddPet.this, mContext.getResources().getString(R.string.reg_pet), mContext.getResources().getString(R.string.select_breed));
      return false;
    }
    mPetInfo.setBreed(nPetKind);

    // 등록번호
    szValue = ((EditText)findViewById(R.id.editRegisterNumber)).getText().toString();
    mPetInfo.setPetID(szValue);

    // 인덱스(등록시 모름)
    mPetInfo.setPetIndex("");

    // 사진 경로
    if (imageUri != null) szData += CommonUtils.getRealPathFromURI(mContext, imageUri);
    else szData = "";
//        mPetInfo.setPetImagePath(szData);


    // 성별

//        szValue = GetSelectedRadioValue(R.id.groupSex);
    if(SELETED_GENDER.equals(GENDER_FEMALE)) szValue = GENDER_FEMALE;
    else szValue = GENDER_MALE;
    mPetInfo.setSex(szValue);

    // 반려동물 상태(1:성장기, 2:성년기, 3:노년기, 4:임신기)
    mPetInfo.setPetStatus("");

    //몸무게
    szValue = ((EditText)findViewById(R.id.editWeight)).getText().toString();
    if (szValue.length() == 0) {
      CommonUtils.alertOK(AddPet.this, mContext.getResources().getString(R.string.reg_pet), mContext.getResources().getString(R.string.input_weight));
      return false;
    }
    mPetInfo.setWeight(szValue);


    // 중성화수술 여부
//        CheckBox checkBox = findViewById(R.id.chkOperation);
    if (mTvCheckNeutralization.getVisibility() == View.VISIBLE) szValue = "Y";
    else szValue = "N";
    mPetInfo.setNeutralization(szValue);

    //반려동물 추가 정보
    // 기초 예방 접종
    mPetInfo.setInoculation(ADD_INFO_1);
    // 산책횟수
    mPetInfo.setWalkCount(ADD_INFO_2);
    // 배변횟수
    mPetInfo.setDeficationCount(ADD_INFO_3);
    // 소변횟수
    mPetInfo.setUrinationCount(ADD_INFO_4);
    Log.e(tag, "pet info: " + mPetInfo.GetPetInfo());

    return true;
  }

  private void ShowPetType()
  {
    List<Map<String, Object>> dialogItemList;
    dialogItemList = CommonUtils.GetPetTypes(mContext);
    AlertDialog.Builder builder = new AlertDialog.Builder(AddPet.this);
    LayoutInflater inflater = getLayoutInflater();
    View view = inflater.inflate(R.layout.show_pet_type_1, null);
    builder.setView(view);

    TextView txtTitle = view.findViewById(R.id.txtTitle);
    txtTitle.setText(mContext.getResources().getString(R.string.dialog_pet_type));

    final ListView listview = view.findViewById(R.id.lvChoosePet);
    final AlertDialog dialog = builder.create();

    ((TextView) view.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    SimpleAdapter simpleAdapter = new SimpleAdapter(AddPet.this, dialogItemList,
            R.layout.image_text_item,
            new String[]{"image", "text"},
            new int[]{R.id.imgView, R.id.txtName});

    listview.setAdapter(simpleAdapter);
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView txtName = view.findViewById(R.id.txtName);
        txtPetType.setText(txtName.getText());
        if (nPetType != (position + 1))  {
          txtPetKind.setText(mContext.getResources().getString(R.string.kind));
          nPetKind = -1;

        }
        nPetType = position + 1;

        dialog.dismiss();
      }
    });

    dialog.setCancelable(false);
    if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.show();
  }

  private void ShowPetBreed(int type)
  {
    List<Map<String, Object>> dialogItemList;
    dialogItemList = PetKind.GetPetKindList(mContext, type);
    AlertDialog.Builder builder = new AlertDialog.Builder(AddPet.this);
    LayoutInflater inflater = getLayoutInflater();
    View view = inflater.inflate(R.layout.show_pet_type_2, null);
    builder.setView(view);

    String title = "";
    TextView txtTitle = view.findViewById(R.id.txtTitle);
    if (type == 1) title = mContext.getResources().getString(R.string.dialog_dog_breed);
    else if (type == 2) title = mContext.getResources().getString(R.string.dialog_cat_breed);

    txtTitle.setText(title);

    final ListView listview = view.findViewById(R.id.lvChoosePet);
    final AlertDialog dialog = builder.create();

    ((TextView) view.findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });

    SimpleAdapter simpleAdapter = new SimpleAdapter(AddPet.this, dialogItemList,
            R.layout.text_item,
            new String[]{"kind", "name"},
            new int[]{R.id.txtKind, R.id.txtName});

    listview.setAdapter(simpleAdapter);
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtKind = view.findViewById(R.id.txtKind);
        txtPetKind.setText(txtName.getText());
        nPetKind = Integer.parseInt(txtKind.getText().toString());

        if(nPetKind == 0)
        {
          mRelativeBreedEtc.setVisibility(View.VISIBLE);
        }else{
          mRelativeBreedEtc.setVisibility(View.GONE);
        }

        dialog.dismiss();
      }
    });
    if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    dialog.show();
  }

  private String getRealPathFromURI(Uri contentUri) {
    String[] proj = {MediaStore.Images.Media.DATA};
    Cursor cursor = getContentResolver().query(Uri.parse(contentUri.toString()), proj, null, null, null);
    cursor.moveToFirst();
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    return cursor.getString(column_index);
  }

  private Bitmap bmp;
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1000 && resultCode == RESULT_OK && data != null && data.getData() != null) {
      imageUri = data.getData();
      mFile = new File(getRealPathFromURI(imageUri));
      if (imageUri != null) {
        bmp = CommonUtils.ResizePicture(mContext, imageUri, 100);

        if (bmp != null) {
          try {
            ExifInterface ei = new ExifInterface(CommonUtils.getRealPathFromURI(AddPet.this, imageUri));
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
              case ExifInterface.ORIENTATION_ROTATE_90:
                bmp = rotateImage(bmp, 90);
                break;

              case ExifInterface.ORIENTATION_ROTATE_180:
                bmp = rotateImage(bmp, 180);
                break;

              case ExifInterface.ORIENTATION_ROTATE_270:
                bmp = rotateImage(bmp, 270);
                break;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }

        }
        civ.setImageBitmap(bmp);

        //AppGlobals.INSTANCE.setImagePath(CommonUtils.getRealPathFromURI(mContext, imageUri));
//                mPetInfo.setPetImagePath(CommonUtils.getRealPathFromURI(mContext, imageUri));

        Log.e(tag, "uri: " + imageUri.toString() + ", path: " + CommonUtils.getRealPathFromURI(mContext, imageUri));
      }
    }

  }

  public void btnGetPicture(View v) {
    Intent intent = new Intent(Intent.ACTION_PICK);
    imageUri = null;
    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
    startActivityForResult(intent, 1000);
  }

  public void btnInputBirthDay(View v) {
    TextView textView_Date;
    DatePickerDialog.OnDateSetListener callbackMethod;

    callbackMethod = new DatePickerDialog.OnDateSetListener()
    {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
      {
        txtBirthDay.setText((year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
      }
    };

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    String year = yearFormat.format(currentTime);
    String month = monthFormat.format(currentTime);
    String day = dayFormat.format(currentTime);

    // DatePickerDialog
    DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
    dialog.show();
  }

  public void btnShowManageState(View v) {
    if (lyManageState.getVisibility() == View.GONE) lyManageState.setVisibility(View.VISIBLE);
    else lyManageState.setVisibility(View.GONE);
  }

  public void btnSelectPetType(View v) {
    ShowPetType();
  }
  public void btnSelectPetBreed(View v) {
    ShowPetBreed(nPetType);
  }
  public void btnAddPet(View v) {
    if (!CheckInputValues()){
      return;
    }else{
      if(mFile != null)
      {
        new HttpImageUpLoad(AddPet.this, HttpString.IMAGE_UPLOAD, mFile, BaseActivity.mMemidx, mHandler).execute();
        mDialog.show();
      }else{
        AddPet();
      }
    }
  }
  public void btnBack(View v) {
    finish();
  }

  private File mFile;
  private  void AddPet() {
    if (!CheckInputValues()) return;
    //szPetInfo = "사랑2,1,123456789012345,,/storage/emulated/0/URSDetector/20190815051909018.jpg,2019-8-13,F,,7.9,2,Y,1,1,1,1";
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(HttpString.SERVICE, HttpString.PET_INFO_SERVICE);
      jsonObject.put(HttpString.MODE, HttpString.ADD_PET_INFO);
      jsonObject.put("sEmail", AppGlobals.INSTANCE.getEmail());

      JSONObject jsonObjPetInfo = new JSONObject();
      jsonObjPetInfo.put("petName", mPetName);
      jsonObjPetInfo.put("petType", mPetInfo.getPetType());
      jsonObjPetInfo.put("petID", mPetInfo.getPetID());
      jsonObjPetInfo.put("petIndex", mPetInfo.getPetIndex());
      if(mFile != null)
      {
        jsonObjPetInfo.put("PetImagePath", mPetInfo.getPetImagePath());
      }else{
        jsonObjPetInfo.put("PetImagePath", "");
      }
      Log.e("getPetImagePath()","mPetInfo.getPetImagePath() = " + mPetInfo.getPetImagePath());
      jsonObjPetInfo.put("PetBirthDay", mPetInfo.getBirthDay());
      jsonObjPetInfo.put("sex", mPetInfo.getSex());
      jsonObjPetInfo.put("PetStatus", mPetInfo.getPetStatus());
      jsonObjPetInfo.put("Weight", mPetInfo.getWeight());
      jsonObjPetInfo.put("Breed", mPetInfo.getBreed());
      jsonObjPetInfo.put("Neutralization", mPetInfo.getNeutralization());
      jsonObjPetInfo.put("Inoculation", mPetInfo.getInoculation());
      jsonObjPetInfo.put("WalkCount", mPetInfo.getWalkCount());
      jsonObjPetInfo.put("DeficationCount", mPetInfo.getDeficationCount());
      jsonObjPetInfo.put("UrinationCount", mPetInfo.getUrinationCount());

      jsonObject.put("petInfo", jsonObjPetInfo);
//            jsonObject.put("sValues", mPetInfo.GetPetInfo());
      new HttpAsyncTask(AddPet.this, HttpString.ADD_PET_INFO, jsonObject, mHandler).execute();
      mDialog.show();
    }catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void Finish() {
    Intent intent = new Intent();
    setResult(RESULT_OK, intent);
    finish();
  }

  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message msg)
    {
      switch (msg.what)
      {
        case ERROR:
          try{
            mJsonResponseData = new JSONObject((String) msg.obj);
          }catch (Exception e){}
          break;
        case OK:
          mJsonResponseData = (JSONObject)msg.obj;
          if(mNowConnectionAPI.equals(HttpString.IMAGE_UPLOAD))
          {
            mDialog.dismiss();
            JSONObject jsonObj = mJsonResponseData;
            JSONArray jsonArrData;
            String result = "";
            try {
              result = jsonObj.getString("result");
              jsonArrData = jsonObj.getJSONArray("data");
              JSONObject jsonObjectData = jsonArrData.getJSONObject(0);
              String orgVar = jsonObjectData.getString("orgVar");
              String uploadFile = jsonObjectData.getString("uploadFile");
              mPetInfo.setPetImagePath(uploadFile);
//                            MainActivity.circleImageViews[AppGlobals.INSTANCE.getPetCount()].setImageBitmap(bmp);
              Log.e("orgVar", "orgVar = " + orgVar);
              Log.e("uploadFile", "uploadFile = " + uploadFile);
            } catch (JSONException e) {
              e.printStackTrace();
            }
            AddPet();
          }
          else if(mNowConnectionAPI.equals(HttpString.ADD_PET_INFO))  // 펫정보 가져오기
          {
            mDialog.dismiss();
            mJsonResponseData = (JSONObject)msg.obj;
            JSONObject jsonObj = mJsonResponseData;
            Log.e("AddPet", "PET_INFO_SERVICE : " + jsonObj.toString());
            try {
              String result_code = jsonObj.getString("result");
              String msgStr = jsonObj.getString("msg");
              if(result_code.equals("ok"))  // 정상
              {
                Log.e("result_code", result_code);
                JSONObject data = jsonObj.getJSONObject("data");
                String petIndex = data.getString("PetIndex");
                mPetInfo.setPetIndex(petIndex);
                PetInfo info = (PetInfo)mPetInfo.clone();
                AppGlobals.INSTANCE.addPetInfo(info);

                Log.e(tag, "pet info: " + mPetInfo.GetPetInfo());

                AlertDialog.Builder ab = new AlertDialog.Builder(AddPet.this);
                ab.setMessage(getResources().getString(R.string.pet_add));
                ab.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Finish();
                  }
                });
                ab.show();
              }else if(result_code.equals("fail")){ // 애러

              }
            } catch (JSONException e) {
              e.printStackTrace();
            }
            mJsonResponseData = new JSONObject();
          }
          break;
      }
    }
  };
}