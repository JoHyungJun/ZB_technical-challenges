package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.type.review.availability.ReviewWrittenStatusType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 방문 이후 7일간 리뷰 작성이 가능한 것을 검증하기 위해 7일간 예약 정보를 저장하는 Entity 클래스
 * 정확한 방문 날짜 (LocalTimeDate) 는 BaseEntity 의 createdAt 이 된다
 * 한 예약 정보로 한 번의 방문만 가능하기 때문에 reservationId 는 자연스럽게 unique 하게 됨
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class ReservationStillAvailableReviewing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long storeId;

    @NotNull
    @Column(unique = true)
    private Long reservationId;

    @NotNull
    private LocalDate visitedDate;

    @NotNull
    @Builder.Default
    private ReviewWrittenStatusType status = ReviewWrittenStatusType.NOT_WRITTEN;


    public ReservationStillAvailableReviewing modifyStatus(ReviewWrittenStatusType status) {

        this.status = status;

        return this;
    }
}
