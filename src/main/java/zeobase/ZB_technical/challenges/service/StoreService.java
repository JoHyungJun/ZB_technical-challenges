package zeobase.ZB_technical.challenges.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import zeobase.ZB_technical.challenges.dto.store.StoreInfoDto;
import zeobase.ZB_technical.challenges.dto.store.StoreRegistrationDto;
import zeobase.ZB_technical.challenges.type.StoreSortedType;

import java.util.List;

/**
 * 매장 관련 Service 의 부모 인터페이스
 */
public interface StoreService {

    StoreRegistrationDto.Response registerStore(StoreRegistrationDto.Request request, Authentication authentication);
    StoreInfoDto.Response getStoreInfo(Long storeId);
    List<StoreInfoDto.Response> getAllSortedStoresInfo(String sort, Double latitude, Double longitude);
}
