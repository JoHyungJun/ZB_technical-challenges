package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.store.StoreDistanceInfoDto;
import zeobase.zbtechnical.challenges.dto.store.StoreInfoDto;
import zeobase.zbtechnical.challenges.dto.store.StoreRegistrationDto;

import java.util.List;

/**
 * 매장 관련 Service 의 부모 인터페이스
 */
public interface StoreService {

    StoreRegistrationDto.Response registerStore(StoreRegistrationDto.Request request, Authentication authentication);
    StoreInfoDto getStoreInfo(Long storeId);
    List<StoreDistanceInfoDto> getAllSortedStoresInfo(String sort, Double latitude, Double longitude);
}
