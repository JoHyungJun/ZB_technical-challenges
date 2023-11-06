package zeobase.ZB_technical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.ZB_technical.challenges.type.StoreStatusType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    private String name;

    private Double latitude;

    private Double longitude;

    private String explanation;

    private StoreStatusType status;

    private LocalTime openHours;

    private LocalTime closedHours;

    private LocalTime reservationTerm;


    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "store")
    private List<Review> reviews;

    @OneToMany(mappedBy = "store")
    private List<Reservation> reservations;


    public void modifyStatus(StoreStatusType status) {
        this.status = status;
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
