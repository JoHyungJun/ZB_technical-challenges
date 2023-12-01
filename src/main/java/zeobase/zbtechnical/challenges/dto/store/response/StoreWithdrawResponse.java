package zeobase.zbtechnical.challenges.dto.store.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreWithdrawResponse {

    private Long storeId;
}
