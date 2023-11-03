package zeobase.ZB_technical.challenges.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.ZB_technical.challenges.dto.member.MemberPublicInfoDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSigninDto;
import zeobase.ZB_technical.challenges.dto.member.MemberSignupDto;
import zeobase.ZB_technical.challenges.entity.Member;
import zeobase.ZB_technical.challenges.exception.MemberException;
import zeobase.ZB_technical.challenges.repository.MemberRepository;
import zeobase.ZB_technical.challenges.service.MemberService;
import zeobase.ZB_technical.challenges.type.MemberStatusType;
import zeobase.ZB_technical.challenges.utils.security.JwtUtils;

import static zeobase.ZB_technical.challenges.type.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;


    @Override
    @Transactional
    public MemberSignupDto.Response signup(MemberSignupDto.Request request) {

        if(memberRepository.existsByMemberId(request.getMemberId())) {
            throw new MemberException(ALREADY_EXISTS_MEMBER_ID);
        }

        if(memberRepository.existsByPhone(request.getPhone())) {
            throw new MemberException(ALREADY_EXISTS_PHONE);
        }

        Member savedMember = memberRepository.save(
                Member.builder()
                    .memberId(request.getMemberId())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getMemberRoleType())
                    .name(request.getName())
                    .phone(request.getPhone())
                    .status(MemberStatusType.ACTIVE)
                    .build());

        return MemberSignupDto.Response.builder()
                .id(savedMember.getId())
                .memberId(savedMember.getMemberId())
                .build();
    }

    @Override
    public MemberSigninDto.Response signin(MemberSigninDto.Request request) {

        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new MemberException(MEMBER_UID_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(MISMATCH_PASSWORD);
        }

        return MemberSigninDto.Response.builder()
                .token(jwtUtils.createToken(member.getMemberId(), member.getRole()))
                .build();
    }

    // TODO : signout

    @Override
    public MemberPublicInfoDto getMemberPublicInfoByMemberId(String memberId) {

        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_UID_NOT_FOUND));

        return MemberPublicInfoDto.builder()
                .memberId(member.getMemberId())
                .memberRoleType(member.getRole())
                .memberStatusType(member.getStatus())
                .build();
    }

    public Member getMemberByAuthentication(Authentication authentication) {

        if(authentication == null) {
            throw new MemberException(UNAUTHORIZED_RESPONSE);
        }

        return (Member) authentication.getPrincipal();
    }

    public void validateMemberStatus(Member member) {

        if(!member.isAccountNonExpired()) {
            throw new MemberException(WITHDRAWAL_MEMBER);
        }else if(!member.isAccountNonExpired()) {
            throw new MemberException(BLOCKED_MEMBER);
        }else if(!member.isCredentialsNonExpired()) {
            throw new MemberException();    // TODO
        }else if(!member.isEnabled()) {
            throw new MemberException(INACTIVE_MEMBER);
        }
    }
}
