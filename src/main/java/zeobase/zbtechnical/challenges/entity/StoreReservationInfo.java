package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class StoreReservationInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime reservationTerm;

    private Integer tableCount;

    private Integer seatingCapacityPerTable;

    @OneToOne(mappedBy = "storeReservationInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Store store;


    public StoreReservationInfo modifyReservationTerm(LocalTime reservationTerm) {

        this.reservationTerm = reservationTerm;

        return this;
    }

    public StoreReservationInfo modifyTableCount(Integer tableCount) {

        this.tableCount = tableCount;

        return this;
    }

    public StoreReservationInfo modifySeatingCapacityPerTable(Integer seatingCapacityPerTable) {

        this.seatingCapacityPerTable = seatingCapacityPerTable;

        return this;
    }
}
