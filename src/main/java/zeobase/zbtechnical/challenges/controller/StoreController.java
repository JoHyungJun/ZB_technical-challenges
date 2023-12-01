package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.zbtechnical.challenges.dto.store.request.StoreModifyRequest;
import zeobase.zbtechnical.challenges.dto.store.request.StoreRegistrationRequest;
import zeobase.zbtechnical.challenges.dto.store.request.StoreWithdrawRequest;
import zeobase.zbtechnical.challenges.dto.store.response.StoreInfoResponse;
import zeobase.zbtechnical.challenges.dto.store.response.StoreModifyResponse;
import zeobase.zbtechnical.challenges.dto.store.response.StoreRegistrationResponse;
import zeobase.zbtechnical.challenges.dto.store.response.StoreWithdrawResponse;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.service.StoreService;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;
import zeobase.zbtechnical.challenges.type.store.StoreSortedType;

import javax.validation.Valid;
import java.util.List;

/**
 * 매장 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final String DEFAULT_SORTED_OPTION = "ALPHABET";

    private final StoreService storeService;


    /**
     * 개별 매장의 정보를 전달하는 api
     *
     * @param storeId
     * @return
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreInfoResponse> getStoreInfo(
            @PathVariable Long storeId
    ) {

        return ResponseEntity.ok().body(storeService.getStoreInfo(storeId));
    }

    /**
     * 검색어가 포함된 상호명의 모든 매장 정보를 전달하는 api
     *
     * @param name - 검색할 상호명 (해당 단어가 포함된 상호 전체 검색)
     * @return
     */
    @GetMapping(value = "/search")
    public ResponseEntity<List<StoreInfoResponse>> searchStoresByName(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {

        return ResponseEntity.ok().body(storeService.getAllStoresInfoByName(name, pageable));
    }

    /**
     * 모든 매장 목록을 정렬에 따라 정보를 전달하는 api
     *
     * @param sortBy - "type/StoreSortedType" 거리(DISTANCE), 이름(ALPHABET), 별점(STAR_RATING)
     * @param latitude - 위도
     * @param longitude - 경도
     * @return
     */
    @GetMapping(value = "/search", params = "sortBy")
    public ResponseEntity<List<StoreInfoResponse>> searchAllStores(
            @RequestParam(defaultValue = DEFAULT_SORTED_OPTION) StoreSortedType sortBy,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            Pageable pageable
    ) {

        return ResponseEntity.ok().body(storeService.getAllSortedStoresInfo(sortBy, latitude, longitude, pageable));
    }

    /**
     * 이용자(점주)가 자신의 매장을 등록하는 api
     *
     * @param request - 매장 정보, 매장 위치, 매장 운영 시간, 매장 영업 상태
     * @param bindingResult
     * @param authentication - 토큰을 활용한 이용자(점주) 검증
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<StoreRegistrationResponse> registerStore(
            @Valid @RequestBody StoreRegistrationRequest request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new StoreException(ErrorCode.INVALID_STORE_SIGN_IN_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(storeService.registerStore(request, authentication));
    }

    /**
     * 매장 정보 수정을 진행하는 api
     *
     * @param request - store id, store 정보, store reservation info 정보
     * @param authentication
     * @return
     */
    @PatchMapping("")
    public ResponseEntity<StoreModifyResponse> modify(
        @RequestBody StoreModifyRequest request,
        Authentication authentication
    ) {

        return ResponseEntity.ok().body(storeService.modify(request, authentication));
    }

    /**
     * 이용자(점주)가 등록했던 매장을 해제하는 api
     *
     * @param request - store id
     * @param authentication
     * @return
     */
    @DeleteMapping("")
    public ResponseEntity<StoreWithdrawResponse> withdraw(
            @RequestBody StoreWithdrawRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(storeService.withdraw(request, authentication));
    }
}
