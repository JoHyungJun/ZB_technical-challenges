package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.zbtechnical.challenges.dto.review.request.*;
import zeobase.zbtechnical.challenges.dto.review.response.*;
import zeobase.zbtechnical.challenges.exception.ReviewException;
import zeobase.zbtechnical.challenges.service.impl.ReviewServiceImpl;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;

import javax.validation.Valid;
import java.util.List;

/**
 * 리뷰 관련 api 를 담는 Controller 클래스
 */
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceImpl reviewService;


    /**
     * 개별 리뷰의 정보를 전달하는 api
     *
     * @param reviewId
     * @return
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewInfoResponse> reviewInfo(
            @PathVariable Long reviewId
    ) {

        return ResponseEntity.ok().body(reviewService.getReviewById(reviewId));
    }

    /**
     * 특정 이용자가 작성한 모든 리뷰를 전달하는 api
     *
     * @param memberId
     * @param pageable
     * @return
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<ReviewInfoResponse>> reviewsInfoByMember(
            @PathVariable Long memberId,
            Pageable pageable
    ) {

        return ResponseEntity.ok().body(reviewService.getAllReviewsByMemberId(memberId, pageable));
    }

    /**
     * 특정 매장에 작성된 모든 리뷰를 전달하는 api
     *
     * @param storeId
     * @param pageable
     * @return
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<ReviewInfoResponse>> reviewsInfoByStore(
            @PathVariable Long storeId,
            Pageable pageable
    ) {

        return ResponseEntity.ok().body(reviewService.getAllReviewsByStoreId(storeId, pageable));
    }

    /**
     * 리뷰를 작성하는 api
     *
     * @param request - 매장 정보, 별점, 리뷰 내용
     * @param bindingResult
     * @param authentication - 토큰을 활용한 이용자(리뷰 작성자) 검증
     * @return
     * @exception ReviewException
     */
    @PostMapping("")
    public ResponseEntity<ReviewPostResponse> write(
            @Valid @RequestBody ReviewPostRequest request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new ReviewException(ErrorCode.INVALID_REVIEW_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(reviewService.postReview(request, authentication));
    }

    /**
     * 개별 리뷰를 조회할 수 없도록 숨기는 api
     * 현재는 리뷰가 등록된 매장 점주가 요청 가능하며,
     * 추가 요구사항을 통해 관리자의 승인 하에 요청할 수 있도록 변경 가능
     *
     * @param reviewId
     * @param authentication - 리뷰가 등록된 가게의 점주 token
     * @return
     */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewHideResponse> hide(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {

        return ResponseEntity.ok().body(reviewService.hideReview(reviewId, authentication));
    }
}
