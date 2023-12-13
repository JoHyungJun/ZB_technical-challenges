package zeobase.zbtechnical.challenges.dto.store;

import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

import java.time.LocalTime;

public interface StoreInfoWithDistanceDiffDto {

    Long getStoreId();
    String getName();
    Double getLatitude();
    Double getLongitude();
    String getExplanation();
    StoreStatusType getStatus();
    LocalTime getOpenHours();
    LocalTime getClosedHours();
    Double getStarRating();
    Long getReviewParticipantCount();
    Double getDistanceDiff();
}
