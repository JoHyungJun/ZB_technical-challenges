package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zeobase.zbtechnical.challenges.dto.store.StoreDistanceInfoDto;
import zeobase.zbtechnical.challenges.dto.store.StoreInfoDto;
import zeobase.zbtechnical.challenges.dto.store.StoreRegistrationDto;
import zeobase.zbtechnical.challenges.service.StoreService;

import java.util.List;

/**
 * 매장 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;


    /**
     * 개별 매장의 정보를 전달하는 api
     *
     * @param storeId
     * @return
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreInfoDto> getStoreInfo(
            @PathVariable Long storeId
    ) {

        return ResponseEntity.ok().body(storeService.getStoreInfo(storeId));
    }

    /**
     * 이용자(점주)가 자신의 매장을 등록하는 api
     *
     * @param request - 매장 정보, 매장 위치, 매장 운영 시간, 매장 영업 상태
     * @param authentication - 토큰을 활용한 이용자(점주) 검증
     * @return
     */
    @PostMapping("/registration")
    public ResponseEntity<StoreRegistrationDto.Response> registerStore(
            @RequestBody StoreRegistrationDto.Request request,
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(storeService.registerStore(request, authentication));
    }

    /**
     * 모든 매장 목록을 정렬에 따라 정보를 전달하는 api
     *
     * @param sort - "type/StoreSortedType" 거리(DISTANCE), 이름(ALPHABET), 별점(STAR_RATING)
     * @param latitude - 위도
     * @param longitude - 경도
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<List<StoreDistanceInfoDto>> getSortedStoresInfo(
            @RequestParam(defaultValue = "DISTANCE") String sort,   // TODO : 상수처리 안 할 방법 찾기
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude
    ) {

        return ResponseEntity.ok().body(storeService.getAllSortedStoresInfo(sort, latitude, longitude));
    }
}
