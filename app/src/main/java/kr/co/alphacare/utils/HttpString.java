package kr.co.alphacare.utils;

public class HttpString {
    //    public static final String BASE_URL = "http://api.alphadokorea.com/api/api.php";
//    public static final String BASE_URL = "http://api.alphadopet.com/api/api.php";
    public static final String BASE_URL = "https://alphadoapi.shop/api/api.php";
    public static final String BASE_URL_IMAGE_UPLOAD = "https://alphadoapi.shop/api/fileUploadAction.php";

    // API CATEGORI
    public static final String ACCOUNT_SERVICE = "AccountService";       // 계정 관련
    public static final String PET_INFO_SERVICE = "PetInfoService";      // 펫 정보 관련
    public static final String PET_URINE_SERVICE = "PetUrineService";    // 펫 검사결과 관련
    public static final String BOARD_SERVICE = "BoardService";           // 게시판 관련

    // API NAME
    public static final String CREATE_ACCOUNT = "CreateAccount";  // 회원가입
    public static final String VERIFY_USER = "VerifyUser";  // 로그인
    public static final String CHANGE_PASSWORD = "ChangePassword";  // 비밀번호 변경
    public static final String RESET_PASSWORD = "ResetPassword";  // 비밀번호 재설정

    public static final String GET_PET_INFO = "GetPetInfo";  // 펫정보 가져오기
    public static final String ADD_PET_INFO = "AddPetInfo";  // 펫등록
    public static final String CHG_PET_INFO = "ChgPetInfo";  // 펫수정
    public static final String DEL_PET_INFO = "DelPetInfo";  // 펫삭제
    public static final String GET_ACCOUNT_INFO = "GetAccountInfo";

    public static final String GET_PET_URINE_HISTORY = "GetPetUrineHistory";  // 검사결과 로드
    public static final String ADD_PET_URINE_RESULT = "AddPetUrineResult";  // 검사결과 등록

    public static final String WRITE = "Write";  // 글 작성
    public static final String LIST = "List";    // 리스트
    public static final String DEL = "Del";      // 삭제

    public static final String IMAGE_UPLOAD = "ImageUpload"; // 이미지 업로드


    public static final String PET_TOOTH_SERVICE = "PetTeethService";
    public static final String PET_SKIN_SERVICE = "PetSkinService";
    public static final String PET_EAR_SERVICE = "PetEarService";
    public static final String MODIFY = "Modify";



    // 공통
    public static final String SERVICE = "service";
    public static final String MODE = "mode";
}
