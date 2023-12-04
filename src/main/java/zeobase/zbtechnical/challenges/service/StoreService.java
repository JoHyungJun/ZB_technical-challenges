package zeobase.zbtechnical.challenges.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import zeobase.zbtechnical.challenges.dto.store.request.StoreModifyRequest;
import zeobase.zbtechnical.challenges.dto.store.request.StoreRegistrationRequest;
import zeobase.zbtechnical.challenges.dto.store.request.StoreWithdrawRequest;
import zeobase.zbtechnical.challenges.dto.store.response.StoreInfoResponse;
import zeobase.zbtechnical.challenges.dto.store.response.StoreModifyResponse;
import zeobase.zbtechnical.challenges.dto.store.response.StoreRegistrationResponse;
import zeobase.zbtechnical.challenges.dto.store.response.StoreWithdrawResponse;
import zeobase.zbtechnical.challenges.type.store.StoreSortedType;

/**
 * 매장 관련 Service 의 부모 인터페이스
 */
public interface StoreService {

    // GET
    StoreInfoResponse getStoreInfo(Long storeId);
    Page<StoreInfoResponse> getAllStoresInfoByName(String name, Pageable pageable);
    Page<StoreInfoResponse> getAllSortedStoresInfo(StoreSortedType sort, Double latitude, Double longitude, Pageable pageable);

    // POST
    StoreRegistrationResponse registerStore(StoreRegistrationRequest request, Authentication authentication);

    // UPDATE (PATCH)
    StoreModifyResponse modify(StoreModifyRequest request, Authentication authentication);

    // DELETE
    StoreWithdrawResponse withdraw(StoreWithdrawRequest request, Authentication authentication);
}
