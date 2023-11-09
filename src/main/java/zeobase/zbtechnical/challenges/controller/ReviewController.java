package zeobase.zbtechnical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.zbtechnical.challenges.dto.review.ReviewInfoDto;
import zeobase.zbtechnical.challenges.dto.review.ReviewPostDto;
import zeobase.zbtechnical.challenges.exception.ReviewException;
import zeobase.zbtechnical.challenges.service.impl.ReviewServiceImpl;
import zeobase.zbtechnical.challenges.type.ErrorCode;

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
     * @param id - reviewId
     * @return
     */
    @GetMapping("")
    public ResponseEntity<ReviewInfoDto> reviewInfo(
            @RequestParam Long id
    ) {

        return ResponseEntity.ok().body(reviewService.getReviewById(id));
    }

    /**
     * 특정 이용자가 작성한 모든 리뷰를 전달하는 api
     *
     * @param id - memberId
     * @return
     */
    @GetMapping("/member")
    public ResponseEntity<List<ReviewInfoDto>> allReviewByMember(
            @RequestParam Long id
    ) {

        return ResponseEntity.ok().body(reviewService.getAllReviewsByMemberId(id));
    }

    /**
     * 특정 매장에 작성된 모든 리뷰를 전달하는 api
     *
     * @param id - storeId
     * @return
     */
    @GetMapping("/store")
    public ResponseEntity<List<ReviewInfoDto>> allReviewByStore(
            @RequestParam Long id
    ) {

        return ResponseEntity.ok().body(reviewService.getAllReviewsByStoreId(id));
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
    public ResponseEntity<ReviewPostDto.Response> postReview(
            @Valid @RequestBody ReviewPostDto.Request request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new ReviewException(ErrorCode.INVALID_REVIEW_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(reviewService.postReview(request, authentication));
    }
}
