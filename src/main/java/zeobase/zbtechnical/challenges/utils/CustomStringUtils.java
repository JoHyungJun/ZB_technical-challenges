package zeobase.zbtechnical.challenges.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import zeobase.zbtechnical.challenges.exception.CommonException;

import java.net.URLDecoder;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_URL_STRING;

public class CustomStringUtils extends StringUtils {

    /**
     * 해당 문자열이 null 이거나, 값이 없거나, 공백이 있는지 검증하는 메서드
     * 
     * @param targetString - 타겟 문자열 (null 허용)
     * @return 
     */
    public static boolean validateEmptyAndWhitespace(@Nullable String targetString) {

        return StringUtils.hasLength(targetString) && !StringUtils.containsWhitespace(targetString);
    }

    /**
     * URL 로 전달된 문자를 UTF-8 형식으로 디코딩 해주는 메서드
     * GET 형식으로 한글이 전달될 때 올바른 디코딩을 도와줌
     *
     * @param
     * @return
     */
    public static String decodeUtf8Url(String urlString) {

        try {
            return URLDecoder.decode(urlString, "UTF-8");
        }catch (Exception e) {
            throw new CommonException(INVALID_URL_STRING);
        }
    }
}
