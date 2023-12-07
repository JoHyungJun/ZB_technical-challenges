package zeobase.zbtechnical.challenges.utils;

/**
 * 정책 관련 Constants 클래스
 */
public class ValidateConstants {

    // Member
    public static final int MIN_PHONE_LENGTH = 8;       // good
    public static final int MAX_PHONE_LENGTH = 16;  // error 발생!

    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?!.*\\s).+$";
    public static final String PHONE_REGEX = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})";


    // Review
    public static final long MAX_STAR_RATING = 5l;
    public static final long MIN_STAR_RATING = 0l;
}
