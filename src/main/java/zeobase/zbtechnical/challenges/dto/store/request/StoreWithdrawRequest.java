package zeobase.zbtechnical.challenges.dto.store.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreWithdrawRequest {

    private Long storeId;
}
