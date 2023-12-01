package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.type.common.ErrorCode;

/**
 * 시큐리티 기능 제공 관련 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    /**
     * UserDetails 를 이용하기 위한 메서드
     *
     * @param memberId - Member 의 PK가 아닌 이용자 UID
     * @return Member Entity 클래스
     */
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        Member member = memberRepository.findByUID(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER_UID));

        return member;
    }
}
