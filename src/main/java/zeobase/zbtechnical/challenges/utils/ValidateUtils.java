package zeobase.zbtechnical.challenges.utils;

public class ValidateUtils {

    public static final int MIN_PHONE_LENGTH = 8;
    public static final int MAX_PHONE_LENGTH = 16;

    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?!.*\\s).+$";
    public static final String PHONE_REGEX = "^01([0|1|6|7|8|9])([0-9]{3,4})([0-9]{4})";
}
