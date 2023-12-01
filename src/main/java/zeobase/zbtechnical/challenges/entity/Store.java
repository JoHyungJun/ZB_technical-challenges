package zeobase.zbtechnical.challenges.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.type.store.StoreSignedStatusType;
import zeobase.zbtechnical.challenges.type.store.StoreStatusType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

/**
 * 매장 관련 Entity 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String explanation;

    @Enumerated(value = EnumType.STRING)
    private StoreStatusType status;

    @Enumerated(value = EnumType.STRING)
    private StoreSignedStatusType signedStatus;

    private LocalTime openHours;

    private LocalTime closedHours;

    private Double starRating;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "store")
    private List<Review> reviews;

    @OneToMany(mappedBy = "store")
    private List<Reservation> reservations;

    @OneToOne
    @JoinColumn(name = "reservation_info_id")
    private StoreReservationInfo storeReservationInfo;


    public Store modifyName(String name) {

        this.name = name;

        return this;
    }

    public Store modifyPosition(Double latitude, Double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;

        return this;
    }

    public Store modifyExplanation(String explanation) {

        this.explanation = explanation;

        return this;
    }

    public Store modifyStatus(StoreStatusType status) {

        this.status = status;

        return this;
    }

    public Store modifySignedStatus(StoreSignedStatusType signedStatus) {

        this.signedStatus = signedStatus;

        return this;
    }

    public Store modifyOpenHours(LocalTime openHours) {

        this.openHours = openHours;

        return this;
    }

    public Store modifyClosedHours(LocalTime closedHours) {

        this.closedHours = closedHours;

        return this;
    }

    public Double getAverageStarRating() {

        if(this.reviews.size() == 0) {
            return 0.0;
        }

        Double totalRating = this.reviews
                .stream()
                .mapToDouble(review -> review.getStartRating())
                .sum();

        return totalRating / (double) this.reviews.size();
    }
}
