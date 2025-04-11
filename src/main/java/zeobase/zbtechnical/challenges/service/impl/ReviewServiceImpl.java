package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.review.request.ReviewModifyRequest;
import zeobase.zbtechnical.challenges.dto.review.request.ReviewWriteRequest;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewHideResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewInfoResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewModifyResponse;
import zeobase.zbtechnical.challenges.dto.review.response.ReviewWriteResponse;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Reservation;
import zeobase.zbtechnical.challenges.entity.Review;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.exception.ReservationException;
import zeobase.zbtechnical.challenges.exception.ReviewException;
import zeobase.zbtechnical.challenges.exception.StoreException;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.repository.ReservationRepository;
import zeobase.zbtechnical.challenges.repository.ReviewRepository;
import zeobase.zbtechnical.challenges.repository.ReviewStatisticsRepository;
import zeobase.zbtechnical.challenges.repository.StoreRepository;
import zeobase.zbtechnical.challenges.service.ReviewService;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;
import zeobase.zbtechnical.challenges.type.redis.value.CacheReviewStarRating;
import zeobase.zbtechnical.challenges.type.review.ReviewStatusType;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.ALREADY_REVIEW_WRITTEN;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.BLOCKED_REVIEW;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.HIDE_REVIEW;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_REVIEWING_DEADLINE;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_REVIEW_REQUEST;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.INVALID_STAR_RATING_VALUE;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.MISMATCH_ROLE;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_MEMBER_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_RESERVATION_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_REVIEW_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_STORE_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_STORE_VISITED_RECORD;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_OWNED_REVIEW_ID;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_OWNED_STORE_ID;
import static zeobase.zbtechnical.challenges.type.common.SQLType.DELETE;
import static zeobase.zbtechnical.challenges.type.common.SQLType.INSERT;
import static zeobase.zbtechnical.challenges.type.common.SQLType.UPDATE;
import static zeobase.zbtechnical.challenges.type.redis.key.RedisKeyType.REVIEW_STATISTICS_STAR_RATING_UPDATE;
import static zeobase.zbtechnical.challenges.type.reservation.ReservationVisitedType.VISITED;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MAX_AVAILABLE_REVIEWING_DAYS;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MAX_STAR_RATING;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.MIN_STAR_RATING;


/**
 * 리뷰 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final MemberServiceImpl memberService;
    private final StoreServiceImpl storeService;

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewStatisticsRepository reviewStatisticsRepository;

    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * 개별 리뷰의 정보를 전달하는 메서드
     * reviewId (Review 의 PK) 검증
     *
     * @param reviewId
     * @return "dto/review/response/ReviewInfoResponse"
     * @exception ReviewException
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewInfoResponse getReviewInfoById(Long reviewId) {
        
        // review id 검증
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND_REVIEW_ID));

        // review status 검증
        validateReviewStatus(review);

        return ReviewInfoResponse.fromEntity(review);
    }

    /**
     * 특정 이용자가 작성한 모든 리뷰를 전달하는 메서드
     * 전달된 memberId 검증
     *
     * @param memberId
     * @return List "dto/review/response/ReviewInfoResponse"
     * @exception MemberException
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewInfoResponse> getReviewsInfoByMember(Long memberId, Pageable pageable) {

        // member id 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        // member status 검증
        memberService.validateMemberSignedStatus(member);

        return reviewRepository.findAllByMemberIdAndStatusOrderByCreatedAtDesc(memberId, ReviewStatusType.SHOW, pageable)
                .map(review -> ReviewInfoResponse.fromEntity(review));
    }

    /**
     * 특정 매장에 작성된 모든 리뷰를 전달하는 메서드
     * 전달된 storeId 검증
     *
     * @param storeId
     * @return List "dto/review/response/ReviewInfoResponse"
     * @exception StoreException
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewInfoResponse> getReviewsInfoByStore(Long storeId, Pageable pageable) {

        // store id 검증
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        return reviewRepository.findAllByStoreIdAndStatusOrderByCreatedAtDesc(storeId, ReviewStatusType.SHOW, pageable)
                .map(review -> ReviewInfoResponse.fromEntity(review));
    }

    /**
        TODO : 리뷰 작성 시 redis, DB 동시 저장. (최신순 리뷰 조회를 위해)
        - 개별 리뷰 조회 시 redis 먼저 살펴보고 DB 접근.
        - 수정 시 redis에 없다면 거절.
        - 최대 수정 일자가 지나면 redis에서 expired event로 DB 개별 리뷰에 반영 + 통계 테이블에 저장
            -> batch + redis + 통계 테이블
     */

    /**
     * 리뷰를 작성하는 메서드
     * store, member 관련 검증 후
     * 해당 이용자의 예약 기록을 추출하여, 해당 매장을 방문하지 않았다면 리뷰 작성 금지 처리
     * 정책 상 방문 기록 일주일 내에 리뷰를 작성할 수 있음 (다음 주 동일 방문 요일까지)
     *
     * @param request - 매장 정보, 별점, 리뷰 내용
     * @param authentication - 토큰을 활용한 이용자(리뷰 작성자) 검증
     * @return "dto/review/response/ReviewWriteResponse"
     * @exception ReviewException
     * @exception StoreException
     * @exception ReservationException
     */
    @Override
    @Transactional
    public ReviewWriteResponse writeReview(ReviewWriteRequest request, Authentication authentication) {

        // TODO : redis 에 이미 저장 된 key 인지, 그러니까 review 작성 요청이 2 번 이상 들어왔을 떄 처리

        // store id 검증
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_STORE_ID));

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);

        // reservation id 검증
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ReservationException(NOT_FOUND_RESERVATION_ID));

        // 해당 reservation 으로 방문했는지 검증
        if(reservation.getVisitedStatus() != VISITED) {
            throw new ReviewException(NOT_FOUND_STORE_VISITED_RECORD);
        }

        // 해당 reservation 이 현재 시각 기준 일주일 안에 일어난 예약인지 검증
        validateAvailableReviewingDeadlineByReservation(reservation);

        // member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member status 검증
        memberService.validateMemberSignedStatus(member);

        // reservation 내에 저장된 member, store 의 정보가 일치하는지 검증
        if(reservation.getMemberIdByValidate() != member.getId()
            || reservation.getStoreIdByValidate() != store.getId()) {
            throw new ReservationException(INVALID_REVIEW_REQUEST);
        }

        // 같은 기록으로 또 다른 리뷰를 추가로 남기려 했을 때 에러 발생
        Review ownedReview = reservation.getReviewByValidate();
        if(ownedReview != null) {
            throw new ReviewException(ALREADY_REVIEW_WRITTEN);
        }

        Review review = Review.builder()
                .starRating(request.getStarRating())
                .reviewMessage(request.getReviewMessage())
                .status(ReviewStatusType.SHOW)
                .member(member)
                .store(store)
                .reservation(reservation)
                .build();

        // 리뷰를 작성했다면 reservation 의 review 값을 세팅하여 같은 기록으로 여러 리뷰를 남길 수 없도록 제한
        reservation.setReview(review);

        // 별점 캐싱 및 통계 테이블 반영을 위해 redis 에 저장
        cacheReviewStarRating(CacheReviewStarRating.builder()
                .sqlType(INSERT)
                .reviewId(review.getId())
                .storeId(store.getId())
                .starRating(review.getStarRating())
                .previousStarRatingForUpdate(null)
                .build());


        return ReviewWriteResponse.fromEntity(reviewRepository.save(review));
    }

    /**
     * 리뷰를 수정하는 메서드
     * 정책 상 방문 기록 일주일 내에 리뷰를 수정할 수 있음 (다음 주 동일 방문 요일까지)
     *
     * @param reviewId
     * @param request - star rating, review message
     * @return
     * @exception MemberException
     * @exception ReviewException
     */
    @Override
    @Transactional
    public ReviewModifyResponse modifyReview(Long reviewId, ReviewModifyRequest request, Authentication authentication) {

        // review id 검증
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND_REVIEW_ID));

        // review status 검증
        validateReviewStatus(review);

        // member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member status 검증
        memberService.validateMemberSignedStatus(member);

        // store 추출
        Store store = review.getStoreByValidate();

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);

        // 본인이 작성한 리뷰가 맞는지 검증
        validateReviewOwner(review, member);

        // reservation 추출
        Reservation reservation = review.getValidatedReservation();

        // 방문 후 일주일 이내의 수정 요청인지 검증
        validateAvailableReviewingDeadlineByReservation(reservation);

        // star rating 수정 요청 시 검증 및 수정
        if(request.getStarRating() != null) {

            // 0.0 이상, 5.0 이하의 값인지 검증
            validateStarRating(request.getStarRating());

            // 별점 캐싱 및 통계 테이블 반영을 위해 redis 에 저장
            cacheReviewStarRating(CacheReviewStarRating.builder()
                    .sqlType(UPDATE)
                    .reviewId(review.getId())
                    .storeId(store.getId())
                    .starRating(request.getStarRating())
                    .previousStarRatingForUpdate(review.getStarRating())
                    .build());

            review.modifyStarRating(request.getStarRating());
        }

        // review message 수정 요청 시 검증 및 수정
        if(request.getReviewMessage() != null) {

            review.modifyReviewMessage(review.getReviewMessage());
        }

        
        return ReviewModifyResponse.builder()
                .reviewId(review.getId())
                .build();
    }

    /**
     * 점주가 리뷰를 삭제하는 메서드
     * 현재는 리뷰가 등록된 매장 점주가 요청 가능
     *
     * @param reviewId
     * @param authentication
     * @return "dto/review/response/ReviewHideResponse"
     * @exception MemberException
     * @exception StoreException
     * @exception ReviewException
     */
    @Override
    @Transactional
    public ReviewHideResponse hideReviewByStoreOwner(Long reviewId, Authentication authentication) {

        // review id 검증
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(NOT_FOUND_REVIEW_ID));

        // review status 검증
        validateReviewStatus(review);

        // member 추출
        Member member = memberService.getMemberByAuthentication(authentication);

        // member status 검증
        memberService.validateMemberSignedStatus(member);

        // member role (가게 점장이 맞는지 여부) 검증
        if(member.getRole() != MemberRoleType.STORE_OWNER) {
            throw new MemberException(MISMATCH_ROLE);
        }

        // 해당 review 가 등록된 store 추출
        Store store = review.getStoreByValidate();

        // store status 검증
        storeService.validateStoreStatus(store);

        // store signed status 검증
        storeService.validateStoreSignedStatus(store);

        // 전달된 store id가 추출한 이용자(점주) 소유 매장인지 여부 검증
        if(!member.getStores()
                .stream()
                .map(ownStore -> ownStore.getId())
                .collect(Collectors.toList())
                .contains(store.getId())) {
            throw new StoreException(NOT_OWNED_STORE_ID);
        }

        // review 의 status 를 HIDE 로 변경 (soft delete)
        review.modifyStatus(ReviewStatusType.HIDE);

        // 별점 캐싱 및 통계 테이블 반영을 위해 redis 에 저장
        cacheReviewStarRating(CacheReviewStarRating.builder()
                .sqlType(DELETE)
                .reviewId(review.getId())
                .storeId(store.getId())
                .starRating(review.getStarRating())
                .previousStarRatingForUpdate(null)
                .build());

        return ReviewHideResponse.builder()
                .reviewId(review.getId())
                .build();
    }

    // TODO : 이용자가 리뷰 삭제하는 거 만들어야 함
    
    /**
     * 요청 받은 별점 값이 0 이상, 5 이하의 값이 맞는지 검증하는 메서드
     *
     * @param starRating
     * @return
     * @exception ReviewException
     */
    private void validateStarRating(Double starRating) {

        if(starRating < MIN_STAR_RATING || starRating > MAX_STAR_RATING) {
            throw new ReviewException(INVALID_STAR_RATING_VALUE);
        }
    }

    /**
     * 해당 리뷰가 요청 받은 이용자가 작성한 리뷰가 맞는지 검증하는 메서드
     *
     * @param review - id, status 등이 검증된 인자이어야 함
     * @param member - id, status 등이 검증된 인자이어야 함
     * @return
     * @exception ReviewException
     */
    public void validateReviewOwner(Review review, Member member) {

        if(review.getMemberIdByValidate() != member.getId()) {
            throw new ReviewException(NOT_OWNED_REVIEW_ID);
        }
    }

    /**
     * 리뷰의 조회 가능 여부를 검증하는 메서드
     * 
     * @param review - review 의 ReviewStatusType 검증
     * @return
     * @exception ReviewException
     */
    private void validateReviewStatus(Review review) {

        switch(review.getStatus()) {
            case HIDE :
                throw new ReviewException(HIDE_REVIEW);
            case BLOCKED:
                throw new ReviewException(BLOCKED_REVIEW);
        }
    }

    /**
     * 예약별 작성된 리뷰가 작성/수정 가능한 시간인지 검증
     *
     * @param reservation - reservation 의 예약 날짜와 현재 날짜 비교
     * @return
     * @exception ReviewException
     */
    private void validateAvailableReviewingDeadlineByReservation(Reservation reservation) {

        if(!reservation.getReservationDateTime().plusDays(MAX_AVAILABLE_REVIEWING_DAYS).isBefore(LocalDateTime.now())) {
            throw new ReviewException(INVALID_REVIEWING_DEADLINE);
        }
    }

    /**
     * 별점 등록/수정/삭제 시 redis 에 반영
     *
     * @param cacheReviewStarRating - redis 에 캐싱할 별점 및 관련 정보
     * @return
     * @exception
     */
    private void cacheReviewStarRating(CacheReviewStarRating cacheReviewStarRating) {

        var setOperations = redisTemplate.opsForSet();
        setOperations.add(REVIEW_STATISTICS_STAR_RATING_UPDATE.getStringKey(), cacheReviewStarRating);
    }
}
