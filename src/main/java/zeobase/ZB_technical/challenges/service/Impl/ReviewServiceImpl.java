package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.review.ReviewInfoDto;
import zeobase.ZB_technical.challenges.dto.review.ReviewPostDto;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.entity.Reservation;
import zeobase.ZB_technical.challenges.entity.Review;
import zeobase.ZB_technical.challenges.entity.Store;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.exception.ReviewException;
import zeobase.ZB_technical.challenges.exception.StoreException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;
import zeobase.ZB_technical.challenges.repository.ReservationRepository;
import zeobase.ZB_technical.challenges.repository.ReviewRepository;
import zeobase.ZB_technical.challenges.repository.StoreRepository;
import zeobase.ZB_technical.challenges.service.ReviewService;
import zeobase.ZB_technical.challenges.type.ReservationVisitedType;

import java.util.List;
import java.util.stream.Collectors;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

/**
 * 리뷰 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final MemberServiceImpl memberService;

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;


    /**
     * 개별 리뷰의 정보를 전달하는 메서드
     * 전달된 reviewId 검증
     *
     * @param reviewId
     * @return "dto/review/ReviewInfoDto"
     * @exception ReviewException
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewInfoDto getReviewById(Long reviewId) {
        
        // review id 검증
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND_REVIEW_ID));

        return ReviewInfoDto.fromEntity(review);
    }

    /**
     * 특정 이용자가 작성한 모든 리뷰를 전달하는 메서드
     * 전달된 memberId 검증
     *
     * @param memberId
     * @return List "dto/review/ReviewInfoDto"
     * @exception MemberException
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewInfoDto> getAllReviewsByMemberId(Long memberId) {

        // member id 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        return member.getReviews()
                .stream()
                .map(review -> ReviewInfoDto.fromEntity(review))
                .collect(Collectors.toList());
    }

    /**
     * 특정 매장에 작성된 모든 리뷰를 전달하는 메서드
     * 전달된 storeId 검증
     *
     * @param storeId
     * @return List "dto/Review/ReviewInfoDto"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewInfoDto> getAllReviewsByStoreId(Long storeId) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        return store.getReviews()
                .stream()
                .map(review -> ReviewInfoDto.fromEntity(review))
                .collect(Collectors.toList());
    }

    /**
     * 리뷰를 작성하는 메서드
     * store, member 관련 검증 후
     * 해당 이용자의 예약 기록을 추출하여, 해당 매장을 방문하지 않았다면 리뷰 작성 금지 처리
     *
     * @param request - 매장 정보, 별점, 리뷰 내용
     * @param authentication - 토큰을 활용한 이용자(리뷰 작성자) 검증
     * @return "dto/review/ReviewPostDto.Response"
     * @exception ReviewException
     */
    @Override
    @Transactional
    public ReviewPostDto.Response postReview(ReviewPostDto.Request request, Authentication authentication) {

        // store id 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member 의 예약(이용) 기록 검증 및 기록 추출
        Reservation reservation = reservationRepository.findByMemberIdAndStoreId(member.getId(), store.getId())
                .orElseThrow(() -> new ReviewException(NOT_FOUND_STORE_RESERVED_RECORD));

        // 매장을 이용하지 않은 member 는 리뷰 작성 불가능.
        // 단, 추가적인 요구사항에 따라 바뀔 수 있음. (예약만 한 손님도 리뷰 작성 가능)
        if(reservation.getVisitedStatus() == ReservationVisitedType.UNVISITED) {
            throw new ReviewException(NOT_FOUND_STORE_VISITED_RECORD);
        }

        Review review = Review.builder()
                .startRating(request.getStarRating())
                .reviewMessage(request.getReviewMessage())
                .member(member)
                .store(store)
                .build();

        return ReviewPostDto.Response.fromEntity(reviewRepository.save(review));
    }
}
