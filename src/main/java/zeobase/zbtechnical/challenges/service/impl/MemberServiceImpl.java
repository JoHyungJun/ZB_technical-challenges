package zeobase.zbtechnical.challenges.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zeobase.zbtechnical.challenges.dto.member.request.MemberModifyRequest;
import zeobase.zbtechnical.challenges.dto.member.request.MemberSigninRequest;
import zeobase.zbtechnical.challenges.dto.member.request.MemberSignupRequest;
import zeobase.zbtechnical.challenges.dto.member.request.RefreshTokenReissueRequest;
import zeobase.zbtechnical.challenges.dto.member.response.*;
import zeobase.zbtechnical.challenges.entity.Member;
import zeobase.zbtechnical.challenges.entity.RefreshToken;
import zeobase.zbtechnical.challenges.exception.JwtException;
import zeobase.zbtechnical.challenges.exception.MemberException;
import zeobase.zbtechnical.challenges.repository.MemberRepository;
import zeobase.zbtechnical.challenges.repository.RefreshTokenRepository;
import zeobase.zbtechnical.challenges.service.MemberService;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;
import zeobase.zbtechnical.challenges.type.member.MemberSignedStatusType;
import zeobase.zbtechnical.challenges.utils.CustomStringUtils;
import zeobase.zbtechnical.challenges.utils.security.JwtUtils;

import static zeobase.zbtechnical.challenges.type.common.ErrorCode.*;
import static zeobase.zbtechnical.challenges.utils.ValidateConstants.*;

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
     * member id 검증
     *
     * @param memberId
     * @return "dto/member/response/MemberInfoResponse"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberInfoResponse getMemberPublicInfoByMemberId(Long memberId) {

        // member id 존재 여부 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER_ID));

        return MemberInfoResponse.fromEntity(member);
    }

    /**
     * 회원 가입을 진행하는 메서드
     * id와 핸드폰 번호에 대해 중복 검사 진행
     *
     * @param request - 가입에 필요한 회원 정보
     * @return "dto/member/response/MemberSignupResponse"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest request) {

        // UID 존재 여부 검증
        if(memberRepository.existsByUID(request.getUID())) {
            throw new MemberException(ALREADY_EXISTS_MEMBER_UID);
        }

        // 핸드폰 존재 여부 검증
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
                    .status(MemberSignedStatusType.ACTIVE)
                    .build());

        return MemberSignupResponse.builder()
                .memberId(savedMember.getId())
                .UID(savedMember.getUID())
                .build();
    }

    /**
     * 로그인을 진행하는 메서드
     * id, password 검증 후 토큰 발행
     *
     * @param request - id, password
     * @return "dto/member/response/TokenResponse"
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
        validateMemberSignedStatus(member);

        // token response 객체 생성
        TokenResponse tokenResponse = jwtUtils.createToken(member.getUID(), member.getRole());
        
        // 생성된 refresh token 은 repository 에 저장
        RefreshToken refreshTokenInDB = refreshTokenRepository.findByMemberId(member.getId())
                .orElse(null);

        // 기존에 해당 멤버의 refresh token 이 DB에 존재한다면 토큰 업데이트, 아니라면 새로 등록
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

    /**
     * 로그 아웃을 진행하는 메서드
     * 토큰 검증 후 해당 member 의 refresh token 삭제
     * 
     * @param authentication
     * @return "dto/member/response/MemberSignOutResponse"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberSignOutResponse signout(Authentication authentication) {

        // authentication (토큰) 에서 멤버 추출
        Member member = getMemberByAuthentication(authentication);

        // 멤버 status 검증
        validateMemberSignedStatus(member);

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
     * @return "dto/member/response/TokenResponse"
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
        validateMemberSignedStatus(member);

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
     * 이용자 정보 수정을 진행하는 메서드
     * request 중 수정을 원하지 않는 정보(필드)는 null 로 전달
     * (프론트 쪽에서 변하지 않은 정보를 null 이 아닌 원래 값으로 넣어줄 수도 있기에, 해당 검증 추가)
     *
     * @param request - UID, password, role, name, phone
     * @param authentication
     * @return "dto/member/response/MemberModifyResponse"
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberModifyResponse modify(MemberModifyRequest request, Authentication authentication) {

        // authentication (토큰) 에서 멤버 추출
        Member member = getMemberByAuthentication(authentication);
        
        // 멤버 status 검증
        validateMemberSignedStatus(member);

        // UID 수정 요청 시 검증 및 수정
        if(request.getUID() != null) {
            
            // 아이디 형식 (null 및 공백문자) 검증
            if(!CustomStringUtils.validateEmptyAndWhitespace(request.getUID())) {
                throw new MemberException(INVALID_UID_REGEX);
            }
            
            // UID 존재 여부 검증
            // 자신의 아이디(Unique)가 그대로 들어갔을 경우 발생하는 논리적 오류 방지
            if(!member.getUID().equals(request.getUID()) && memberRepository.existsByUID(request.getUID())) {
                throw new MemberException(ALREADY_EXISTS_MEMBER_UID);
            }

            member.modifyUID(request.getUID());
        }

        // 비밀번호 수정 요청 시 검증 및 수정
        if(request.getPassword() != null) {

            // 비밀번호 길이 검증
            if(request.getPassword().length() < MIN_PHONE_LENGTH ||
               request.getPassword().length() > MAX_PHONE_LENGTH) {
                throw new MemberException(INVALID_PASSWORD_LENGTH);
            }

            // 비밀번호 형식 (format) 검증
            if(!request.getPassword().matches(PASSWORD_REGEX)) {
                throw new MemberException(INVALID_PASSWORD_REGEX);
            }

            member.modifyPasswordByEncodedPassword(passwordEncoder.encode(request.getPassword()));
        }

        // 이용자 권한 수정 요청 시 검증 및 수정
        if(request.getRole() != null) {

            // 일반 이용자로 권한을 변경하고 싶을 시, 자기 소유의 매장이 존재한다면 거절
            if(request.getRole() == MemberRoleType.MEMBER &&
               member.getStores().size() > 0) {
                throw new MemberException(INVALID_MEMBER_MODIFY_ROLE);
            }

            member.modifyRole(request.getRole());
        }

        // 이용자 이름 수정 요청 시 검증 및 수정
        if(request.getName() != null) {

            // 이름 형식 (null 및 공백문자) 검증
            if(!CustomStringUtils.validateEmptyAndWhitespace(request.getName())) {
                throw new MemberException(INVALID_NAME_REGEX);
            }

            member.modifyName(request.getName());
        }

        // 이용자 핸드폰 번호 수정 요청 시 검증 및 수정
        if(request.getPhone() != null) {

            // 핸드폰 번호 형식 (regex) 검증
            if(!request.getPhone().matches(PHONE_REGEX)) {
                throw new MemberException(INVALID_PHONE_REGEX);
            }

            // 핸드폰 번호 존재 여부 검증
            // 자신의 핸드폰 번호(Unique)가 그대로 들어갔을 경우 발생하는 논리적 오류 방지
            if(!member.getPhone().equals(request.getPhone()) && memberRepository.existsByPhone(request.getPhone())) {
                throw new MemberException(ALREADY_EXISTS_PHONE);
            }

            member.modifyPhone(request.getPhone());
        }

        memberRepository.save(member);

        return MemberModifyResponse.builder()
                .memberId(member.getId())
                .build();
    }

    /**
     * 이용자 회원 탈퇴를 진행하는 메서드
     * member signed status 를 WITHDRAW 로 수정 (soft delete)
     *
     * @param authentication
     * @return "dto/member/response/MemberWithdrawResponse" - member id
     * @exception MemberException
     */
    @Override
    @Transactional
    public MemberWithdrawResponse withdraw(Authentication authentication) {

        // authentication (토큰) 에서 멤버 추출
        Member member = getMemberByAuthentication(authentication);

        // member status 검증
        validateMemberSignedStatus(member);

        memberRepository.save(member.modifyStatus(MemberSignedStatusType.WITHDRAW));

        return MemberWithdrawResponse.builder()
                .memberId(member.getId())
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

        // 필터에 의해 토큰이 전달되지 않는 요청에는 authentication 이 null 로 들어감
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
    public void validateMemberSignedStatus(Member member) {

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
