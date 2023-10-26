package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;

import static zeobase.ZB_technical.challenges.type.ErrorCode.MEMBER_UID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_UID_NOT_FOUND));

        return member;
    }
}
