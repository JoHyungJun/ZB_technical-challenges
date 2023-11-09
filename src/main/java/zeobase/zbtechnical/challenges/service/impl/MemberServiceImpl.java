package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.member.MemberInfoDto;
import zeobase.zbtechnical.challenges.dto.member.MemberSigninDto;
import zeobase.zbtechnical.challenges.dto.member.MemberSignupDto;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.service.MemberService;
import zeobase.zbtechnical.challenges.type.MemberStatusType;
import zeobase.zbtechnical.challenges.utils.security.JwtUtils;

import static zeobase.zbtechnical.challenges.type.ErrorCode.*;

/**
 * 이용자 관련 로직을 담는 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;


    /**
     * 개별 이용자의 공개 가능한 정보를 전달하는 메서드
     * memberId (Member 의 PK) 검증
     *
     * @param memberId
     * @return "dto/member/MemberInfoDto"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberInfoDto getMemberPublicInfoByMemberId(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        return MemberInfoDto.builder()
                .memberId(member.getMemberId())
                .memberRoleType(member.getRole())
                .memberStatusType(member.getStatus())
                .build();
    }

    /**
     * 회원 가입을 진행하는 메서드
     * id와 핸드폰 번호에 대해 중복 검사 진행
     *
     * @param request - 가입에 필요한 회원 정보
     * @return "dto/member/MemberSignupDto.Response"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberSignupDto.Response signup(MemberSignupDto.Request request) {

        if(memberRepository.existsByMemberId(request.getMemberId())) {
            throw new MemberException(ALREADY_EXISTS_MEMBER_UID);
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

    /**
     * 로그인을 진행하는 메서드
     * id, password 검증 후 토큰 발행
     *
     * @param request - id, password
     * @return "dto/member/MemberSigninDto.Response"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberSigninDto.Response signin(MemberSigninDto.Request request) {

        Member member = memberRepository.findByMemberId(request.getMemberId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_UID));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(MISMATCH_PASSWORD);
        }

        return MemberSigninDto.Response.builder()
                .token(jwtUtils.createToken(member.getMemberId(), member.getRole()))
                .build();
    }

    /**
     * Authentication 을 통해 이용자 Entity 클래스를 추출하는 메서드
     * 
     * @param authentication
     * @return Member Entity 클래스
     * @exception MemberException
     */
    public Member getMemberByAuthentication(Authentication authentication) {

        if(authentication == null) {
            throw new MemberException(UNAUTHORIZED_RESPONSE);
        }

        return (Member) authentication.getPrincipal();
    }

    /**
     * 이용자의 아이디 상태를 검증하는 메서드
     *
     * @param member - 이용자의 Entity 객체
     * @return
     * @exception MemberException
     */
    public void validateMemberStatus(Member member) {

        if(!member.isAccountNonExpired()) {
            throw new MemberException(WITHDRAWAL_MEMBER);
        }else if(!member.isAccountNonExpired()) {
            throw new MemberException(BLOCKED_MEMBER);
        }else if(!member.isCredentialsNonExpired()) {
            throw new MemberException(EXPIRED_CREDENTIAL);    // TODO
        }else if(!member.isEnabled()) {
            throw new MemberException(INACTIVE_MEMBER);
        }
    }
}
