package zeobase.zbtechnical.challenges.event.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import zeobase.zbtechnical.challenges.entity.ReviewStatistics;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.event.StoreSavedEvent;
import zeobase.zbtechnical.challenges.repository.ReviewStatisticsRepository;

@Component
@RequiredArgsConstructor
public class StoreSavedEventListener {

    private final ReviewStatisticsRepository reviewStatisticsRepository;


    @EventListener
    public void handleStoreSaved(StoreSavedEvent storeSavedEvent) {

        Store savedStore = storeSavedEvent.getStore();

        if(reviewStatisticsRepository.existsByStoreId((savedStore.getId()))) {
            return;
        }

        ReviewStatistics newReviewStatistics = ReviewStatistics.initializeForNewStore(savedStore.getId());

        reviewStatisticsRepository.save(newReviewStatistics);
    }
}
