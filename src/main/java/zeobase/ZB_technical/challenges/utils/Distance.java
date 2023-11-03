package zeobase.ZB_technical.challenges.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Distance {

    private static final Double PI = Math.PI;
    private static final Double DEGREE = 180.0;
    private static final Double RADIUS = 6371.0;


    // 하버사인으로 두 위치 간 직선 거리 구하기
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
