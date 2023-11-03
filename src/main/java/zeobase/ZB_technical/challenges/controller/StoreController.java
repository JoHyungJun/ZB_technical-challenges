package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zeobase.ZB_technical.challenges.dto.store.StoreInfoDto;
import zeobase.ZB_technical.challenges.dto.store.StoreRegistrationDto;
import zeobase.ZB_technical.challenges.service.StoreService;
import zeobase.ZB_technical.challenges.type.StoreSortedType;

import java.util.List;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;


    @PostMapping("/registration")
    public ResponseEntity<StoreRegistrationDto.Response> registerStore(
            @RequestBody StoreRegistrationDto.Request request,
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(storeService.registerStore(request, authentication));
    }

    @GetMapping("/info")
    public ResponseEntity<StoreInfoDto.Response> getStoreInfo(
            @RequestParam Long storeId
    ) {

        return ResponseEntity.ok().body(storeService.getStoreInfo(storeId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StoreInfoDto.Response>> getSortedStoresInfo(
            @RequestParam(defaultValue = "DISTANCE") String sort,   // TODO : 상수처리 안 할 방법 찾기
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
            ) {

        return ResponseEntity.ok().body(storeService.getAllSortedStoresInfo(sort, latitude, longitude));
    }
}
