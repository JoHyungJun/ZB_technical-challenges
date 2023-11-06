package zeobase.ZB_technical.challenges.utils;

import lombok.experimental.UtilityClass;

/**
 * 거리순 계산 관련 Util 클래스
 */
@UtilityClass
public class Distance {

    private static final Double PI = Math.PI;
    private static final Double DEGREE = 180.0;
    private static final Double RADIUS = 6371.0;


    /**
     * 하버사인 공식을 이용하여 두 위치(위도, 경도) 간 거리를 구하는 메서드
     * 
     * @param positionX - 현재 위치의 위도
     * @param positionY - 현재 위치의 경도
     * @param targetX - 타겟 위치의 위도
     * @param targetY - 타겟 위치의 경도
     * @return 두 지점의 거리를 Double 로 반환
     */
    public static Double getDistanceInKilometerByHarversine(
            Double positionX, Double positionY,
            Double targetX, Double targetY
    ) {

        Double toRadian = PI / DEGREE;
        Double deltaLatitude = Math.abs(positionX - targetX) * toRadian;
        Double deltaLongitude = Math.abs(positionY - targetY) * toRadian;

        Double sinDeltaLat = Math.sin(deltaLatitude / 2);
        Double sinDeltaLng = Math.sin(deltaLongitude / 2);
        Double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                Math.cos(positionX * toRadian) * Math.cos(targetX * toRadian) * sinDeltaLng * sinDeltaLng);

        return 2 * RADIUS * Math.asin(squareRoot);
    }
}
