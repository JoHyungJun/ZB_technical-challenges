package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zeobase.zbtechnical.challenges.exception.StoreException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalTime;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NOT_FOUND_STORE_OWNED_STORE_RESERVATION_INFO;
import static zeobase.zbtechnical.challenges.type.common.ErrorCode.NULL_POINT_PRIMARY_KEY;

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

    /**
     * 해당 store reservation info 의 연관관계의 주인이 되는 store 의 id 를 추출하는 메서드
     * store id 가 null 이라면 예외 처리
     * 내부적으로 getStoreByValidate() 메서드를 통해 검증
     *
     * @return store id
     * @exception StoreException
     */
    public Long getStoreId() {

        Long validatedId = getStoreByValidate().getId();

        if(validatedId == null) {
            throw new StoreException(NULL_POINT_PRIMARY_KEY);
        }

        return validatedId;
    }

    /**
     * 해당 store reservation info 의 연관관계의 주인이 되는 store 을 추출하는 메서드
     * store 가 null 이라면 예외 처리
     *
     * @return "entity/Store"
     * @exception StoreException
     */
    public Store getStoreByValidate() {

        if(this.store == null) {
            throw new StoreException(NOT_FOUND_STORE_OWNED_STORE_RESERVATION_INFO);
        }

        return this.store;
    }
}
