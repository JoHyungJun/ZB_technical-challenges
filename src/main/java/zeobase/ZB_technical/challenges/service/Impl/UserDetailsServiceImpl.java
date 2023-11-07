package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;

import static zeobase.ZB_technical.challenges.type.ErrorCode.NOT_FOUND_MEMBER_UID;

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

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_UID));

        return member;
    }
}
