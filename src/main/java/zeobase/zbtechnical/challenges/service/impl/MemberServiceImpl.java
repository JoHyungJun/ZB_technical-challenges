package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.member.*;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.RefreshToken;
import zeobase.zbtechnical.challenges.exception.JwtException;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;


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
    public MemberInfoResponse getMemberPublicInfoByMemberId(Long memberId) {

        // member id 존재 여부 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        return MemberInfoResponse.builder()
                .UID(member.getUID())
                .role(member.getRole())
                .status(member.getStatus())
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
    public MemberSignup.Response signup(MemberSignup.Request request) {

        // UID 존재 검증 여부
        if(memberRepository.existsByUID(request.getUID())) {
            throw new MemberException(ALREADY_EXISTS_MEMBER_UID);
        }

        // 핸드폰 존재 검증 여부
        if(memberRepository.existsByPhone(request.getPhone())) {
            throw new MemberException(ALREADY_EXISTS_PHONE);
        }

        Member savedMember = memberRepository.save(
                Member.builder()
                    .UID(request.getUID())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(request.getRole())
                    .name(request.getName())
                    .phone(request.getPhone())
                    .status(MemberStatusType.ACTIVE)
                    .build());

        return MemberSignup.Response.builder()
                .memberId(savedMember.getId())
                .UID(savedMember.getUID())
                .build();
    }

    /**
     * 로그인을 진행하는 메서드
     * id, password 검증 후 토큰 발행
     *
     * @param request - id, password
     * @return "dto/member/TokenResponse"
     * @exception MemberException
     */
    @Override
    @Transactional
    public TokenResponse signin(MemberSigninRequest request) {

        // UID 로 멤버 추출
        Member member = memberRepository.findByUID(request.getUID())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_UID));

        // 비밀번호 검증
        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(MISMATCH_PASSWORD);
        }

        // 멤버 status 검증
        validateMemberStatus(member);

        // token response 객체 생성
        TokenResponse tokenResponse = jwtUtils.createToken(member.getUID(), member.getRole());
        
        // 생성된 refresh token 은 repository 에 저장
        RefreshToken refreshTokenInDB = refreshTokenRepository.findByMemberId(member.getId())
                .orElse(null);

        if(refreshTokenInDB == null) {

            refreshTokenRepository.save(RefreshToken.builder()
                    .refreshToken(tokenResponse.getRefreshToken())
                    .memberId(member.getId())
                    .build());
        }else {

            refreshTokenRepository.save(refreshTokenInDB.updateRefreshToken(tokenResponse.getRefreshToken()));
        }

        return tokenResponse;
    }

    @Override
    @Transactional
    public MemberSignOutResponse signout(Authentication authentication) {

        // authentication 에서 멤버 추출
        Member member = getMemberByAuthentication(authentication);

        // 멤버 status 검증
        validateMemberStatus(member);

        // DB 에 저장된 refresh token 삭제
        refreshTokenRepository.deleteByMemberId(member.getId());

        return MemberSignOutResponse.builder()
                .signoutSuccess(true)
                .build();
    }

    /**
     * refresh token 을 검증하고
     * 새로운 access token 및 refresh token 을 발급
     *
     * @param request - refresh token
     * @return "dto/member/TokenResponse"
     * @exception MemberException
     * @exception JwtException
     */
    @Override
    @Transactional
    public TokenResponse reissue(RefreshTokenReissueRequest request) {
        
        // refresh token 에서 member uid 추출 및 토큰 검증
        String memberUID = jwtUtils.parseClaimsByToken(request.getRefreshToken())
                .getSubject();

        // member uid 검증
        Member member = memberRepository.findByUID(memberUID)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_UID));
        
        // member status 검증
        validateMemberStatus(member);

        // DB 에서 refresh token 추출
        RefreshToken refreshTokenInDB = refreshTokenRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new JwtException(UNSUPPORTED_JWT_EXCEPTION));

        // DB 의 refresh token 과 request 로 전달 된 refresh token 비교
        if(!refreshTokenInDB.getRefreshToken().equals(request.getRefreshToken())) {
            throw new JwtException(SIGNATURE_EXCEPTION);
        }

        // refresh token 검증
        if(!jwtUtils.validateToken(refreshTokenInDB.getRefreshToken())) {
            throw new JwtException(EXPIRED_JWT_EXCEPTION);
        }

        // 새로운 token response 생성
        TokenResponse newTokenResponse = jwtUtils.createToken(memberUID, member.getRole());

        // 기존 DB 에 저장된 refresh token 업데이트
        refreshTokenRepository.save(refreshTokenInDB.updateRefreshToken(newTokenResponse.getRefreshToken()));

        return newTokenResponse;
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
            throw new MemberException(EXPIRED_CREDENTIAL);
        }else if(!member.isEnabled()) {
            throw new MemberException(INACTIVE_MEMBER);
        }
    }
}
