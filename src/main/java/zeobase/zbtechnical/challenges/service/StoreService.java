package zeobase.zbtechnical.challenges.service;

import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.store.response.*;
import zeobase.zbtechnical.challenges.dto.store.request.*;

import java.util.List;

/**
 * 매장 관련 Service 의 부모 인터페이스
 */
public interface StoreService {

    StoreRegistrationResponse registerStore(StoreRegistrationRequest request, Authentication authentication);
    StoreInfoResponse getStoreInfo(Long storeId);
    List<StoreDistanceInfoResponse> getAllSortedStoresInfo(String sort, Double latitude, Double longitude);
}
