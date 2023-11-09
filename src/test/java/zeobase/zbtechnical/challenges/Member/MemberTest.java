package zeobase.zbtechnical.challenges.Member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.Store;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.repository.StoreRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @Transactional
    void 연관관계() {
        //given
        Store store1 = Store.builder().name("store1").build();
        Store store2 = Store.builder().name("store2").build();

        //when
        storeRepository.save(store1);
        storeRepository.save(store2);

        List<Store> stores = new ArrayList<>();
        stores.add(store1);
        stores.add(store2);
        Member member = Member.builder()
                .stores(stores)
                .build();

        member = memberRepository.save(member);

        //then
        Member testM = memberRepository.findById(member.getId()).get();
        stores.removeAll(member.getStores());

        assertEquals(0, stores.size());
    }
}