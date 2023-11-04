package zeobase.ZB_technical.challenges.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import zeobase.ZB_technical.challenges.dto.common.ErrorResponse;
import zeobase.ZB_technical.challenges.dto.review.ReviewInfoDto;
import zeobase.ZB_technical.challenges.dto.review.ReviewPostDto;
import zeobase.ZB_technical.challenges.exception.ReviewException;
import zeobase.ZB_technical.challenges.service.Impl.ReviewServiceImpl;

import javax.validation.Valid;
import java.util.List;

import static zeobase.ZB_technical.challenges.type.ErrorCode.INVALID_REVIEW_REQUEST;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewServiceImpl reviewService;


    @GetMapping("")
    public ResponseEntity<ReviewInfoDto> reviewInfo(
            @RequestParam Long id
    ) {

        return ResponseEntity.ok().body(reviewService.getReviewById(id));
    }

    @GetMapping("/member")
    public ResponseEntity<List<ReviewInfoDto>> allReviewByMember(
            @RequestParam Long id
    ) {

        return ResponseEntity.ok().body(reviewService.getAllReviewsByMemberId(id));
    }

    @GetMapping("/store")
    public ResponseEntity<List<ReviewInfoDto>> allReviewByStore(
            @RequestParam Long id
    ) {

        return ResponseEntity.ok().body(reviewService.getAllReviewsByStoreId(id));
    }

    @PostMapping("")
    public ResponseEntity<?> postReview(
            @Valid @RequestBody ReviewPostDto.Request request,
            BindingResult bindingResult,
            Authentication authentication
    ) {

        if(bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();

            throw new ReviewException(INVALID_REVIEW_REQUEST.modifyDescription(errors.get(0).getDefaultMessage()));
        }

        return ResponseEntity.ok().body(reviewService.postReview(request, authentication));
    }
}
