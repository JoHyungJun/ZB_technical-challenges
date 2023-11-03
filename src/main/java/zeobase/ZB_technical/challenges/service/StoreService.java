package zeobase.ZB_technical.challenges.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import zeobase.ZB_technical.challenges.dto.store.StoreInfoDto;
import zeobase.ZB_technical.challenges.dto.store.StoreRegistrationDto;
import zeobase.ZB_technical.challenges.type.StoreSortedType;

import java.util.List;

public interface StoreService {

    StoreRegistrationDto.Response registerStore(StoreRegistrationDto.Request request, Authentication authentication);
    StoreInfoDto.Response getStoreInfo(Long storeId);
    List<StoreInfoDto.Response> getAllSortedStoresInfo(String sort, Double latitude, Double longitude);
}
