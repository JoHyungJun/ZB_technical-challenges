package zeobase.zbtechnical.challenges.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import zeobase.zbtechnical.challenges.entity.Store;

@Getter
public class StoreSavedEvent extends ApplicationEvent {

    private final Store store;


    public StoreSavedEvent(Store store) {
        super(store);
        this.store = store;
    }
}
