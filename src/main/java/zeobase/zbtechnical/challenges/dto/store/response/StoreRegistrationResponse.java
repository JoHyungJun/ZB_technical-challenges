package zeobase.zbtechnical.challenges.dto.store.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이용자(점주)의 매장 등록 관련 response DTO 클래스
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreRegistrationResponse {

    private Long storeId;
}
