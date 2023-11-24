package zeobase.zbtechnical.challenges.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class CustomStringUtils extends StringUtils {

    public static boolean validateEmptyAndWhitespace(@Nullable String str) {

        if(!hasLength(str)) {
            return false;
        }

        for(char ch : str.toCharArray()) {
            if(Character.isWhitespace(ch)) {
                return false;
            }
        }

        return true;
    }
}
