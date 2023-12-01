package zeobase.zbtechnical.challenges.type.common;

/**
 * ErrorResponse 의 description 에 해당하는 메세지
 * 임시로 에러 코드의 에러 메세지가 여러 곳에서 공유되는 부분만 작성
 */
public class ErrorMessage {

    // Member
    public static final String INVALID_PASSWORD_REGEX_MSG = "비밀번호는 영어와 숫자를 혼용해야 하며 공백은 사용할 수 없습니다.";
    public static final String INVALID_PASSWORD_LENGTH_MSG = "비밀번호는 최소 8글자 이상 최대 16글자 이하로 작성해야 합니다.";
    public static final String INVALID_PHONE_REGEX_MSG = "사용자의 핸드폰 번호 형식이 부적합합니다.";
}
