package com.cafe.mobile.shcafe.member.service;

import com.cafe.mobile.shcafe.common.exception.AlreadyExistsException;
import com.cafe.mobile.shcafe.common.exception.BizException;
import com.cafe.mobile.shcafe.common.jwt.JwtUtil;
import com.cafe.mobile.shcafe.common.type.MemberStsCdConst;
import com.cafe.mobile.shcafe.common.type.ResponseType;
import com.cafe.mobile.shcafe.member.dto.request.MemberCancelWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberLoginRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberSignUpRequest;
import com.cafe.mobile.shcafe.member.dto.request.MemberWithdrawRequest;
import com.cafe.mobile.shcafe.member.dto.response.MemberSignUpResponse;
import com.cafe.mobile.shcafe.member.dto.response.MemberWithdrawResponse;
import com.cafe.mobile.shcafe.member.entity.Member;
import com.cafe.mobile.shcafe.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceImplTest {
    @Mock // 가짜 객체
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks // 테스트할 객체
    private MemberServiceImpl memberService;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest(
                "testuser123",
                "password123!",
                "test@example.com",
                "홍길동",
                "01012345678",
                "M",
                LocalDate.of(1990, 12, 25)
        );

        String encodedPassword = "encoded_password_123";
        Member savedMember = request.toEntity(encodedPassword);

        // 가짜 객체들이 동작 설정
        when(memberRepository.findMemberNotResigned(any(), any(), any(), any()))
                .thenReturn(Optional.empty()); // 중복 없음
        when(bCryptPasswordEncoder.encode(any()))
                .thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class)))
                .thenReturn(savedMember);

        // When: 실제 테스트할 메서드 실행
        MemberSignUpResponse response = memberService.signUp(request);

        // Then
        assertThat(response.getMemberId()).isEqualTo("testuser123"); // 이 값이 맞는지 확인
        assertThat(response.getRegDt()).isNotNull();

        // 메소드가 실제 호출되었는지 확인
        verify(memberRepository).findMemberNotResigned(
                eq("testuser123"), eq("test@example.com"), eq("01012345678"), eq(MemberStsCdConst.WITHDRAWN)
        );
        verify(bCryptPasswordEncoder).encode("password123!");
        verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("중복 ID 예외")
        void duplicateId_throwsException() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest(
                "duplicateuser",
                "password123!",
                "test@example.com",
                "홍길동",
                "01012345678",
                "M",
                LocalDate.of(1990, 12, 25)
        );

        Member duplicateMember = Member.builder()
                .memberId("duplicateuser")  // 중복된 ID
                .password("password")
                .email("different@example.com")  // 다른 이메일
                .name("홍길동")
                .telNo("01099999999")  // 다른 전화번호
                .gender("M")
                .birthDt(LocalDate.of(1990, 12, 25))
                .memberStsCd(MemberStsCdConst.ACTIVE)
                .regDt(LocalDate.now())
                .build();

        when(memberRepository.findMemberNotResigned(any(), any(), any(), any()))
                .thenReturn(Optional.of(duplicateMember)); // 중복 있음

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request)) // 예외를 던지는지 확인
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("이미 사용 중인 ID입니다");

        verify(memberRepository).findMemberNotResigned(any(), any(), any(), any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복 이메일 예외")
        void duplicateEmail_throwsException() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest(
                "newuser",
                "password123!",
                "duplicate@example.com",
                "홍길동",
                "01012345678",
                "M",
                LocalDate.of(1990, 12, 25)
        );

        Member duplicateMember = Member.builder()
                .memberId("differentuser")  // 다른 ID
                .password("password")
                .email("duplicate@example.com")  // 중복된 이메일
                .name("홍길동")
                .telNo("01099999999")  // 다른 전화번호
                .gender("M")
                .birthDt(LocalDate.of(1990, 12, 25))
                .memberStsCd(MemberStsCdConst.ACTIVE)
                .regDt(LocalDate.now())
                .build();

        when(memberRepository.findMemberNotResigned(any(), any(), any(), any()))
                .thenReturn(Optional.of(duplicateMember)); // 중복 있음

        // When & Then
        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage("이미 사용 중인 이메일입니다");

        verify(memberRepository).findMemberNotResigned(any(), any(), any(), any());
        verify(bCryptPasswordEncoder, never()).encode(any());
        verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복 전화번호 예외")
        void duplicateTelNo_throwsException() {
            // Given
            MemberSignUpRequest request = new MemberSignUpRequest(
                    "newuser",
                    "password123!",
                    "new@example.com",
                    "홍길동",
                    "01012345678",  // 중복된 전화번호
                    "M",
                    LocalDate.of(1990, 12, 25)
            );

            Member duplicateMember = Member.builder()
                    .memberId("differentuser")
                    .password("password")
                    .email("different@example.com")
                    .name("홍길동")
                    .telNo("01012345678")  // 중복된 전화번호
                    .gender("M")
                    .birthDt(LocalDate.of(1990, 12, 25))
                    .memberStsCd(MemberStsCdConst.ACTIVE)
                    .regDt(LocalDate.now())
                    .build();

            when(memberRepository.findMemberNotResigned(any(), any(), any(), any()))
                    .thenReturn(Optional.of(duplicateMember));

            // When & Then
            assertThatThrownBy(() -> memberService.signUp(request))
                    .isInstanceOf(AlreadyExistsException.class)
                    .hasMessage("이미 사용 중인 전화번호입니다");

            verify(memberRepository).findMemberNotResigned(any(), any(), any(), any());
            verify(bCryptPasswordEncoder, never()).encode(any());
            verify(memberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
        // Given
        MemberLoginRequest request = new MemberLoginRequest("testuser123", "password123!");
        
        Member member = Member.builder()
                .memberId("testuser123")
                .password("encoded_password")
                .memberStsCd(MemberStsCdConst.ACTIVE)
                .build();

        String expectedJwt = "jwt_token_123";

        when(memberRepository.findById("testuser123"))
                .thenReturn(Optional.of(member));
        when(bCryptPasswordEncoder.matches("password123!", "encoded_password"))
                .thenReturn(true);
        when(jwtUtil.createJwt("testuser123", "USER"))
                .thenReturn(expectedJwt);

        // When
        String result = memberService.login(request);

        // Then
        assertThat(result).isEqualTo(expectedJwt);

        verify(memberRepository).findById("testuser123");
        verify(bCryptPasswordEncoder).matches("password123!", "encoded_password");
        verify(jwtUtil).createJwt("testuser123", "USER");
        }

        @Test
        @DisplayName("존재하지 않는 ID")
        void nonexistentId_throwsException() {
        // Given
        MemberLoginRequest request = new MemberLoginRequest("nonexistent", "password123!");

        when(memberRepository.findById("nonexistent"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(BizException.class)
                .hasMessage("ID가 일치하지 않습니다.");

        verify(memberRepository).findById("nonexistent");
        verify(bCryptPasswordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).createJwt(any(), any());
        }

        @Test
        @DisplayName("잘못된 비밀번호")
        void wrongPassword_throwsException() {
        // Given
        MemberLoginRequest request = new MemberLoginRequest("testuser123", "wrongpassword");
        
        Member member = Member.builder()
                .memberId("testuser123")
                .password("encoded_password")
                .memberStsCd(MemberStsCdConst.ACTIVE)
                .build();

        when(memberRepository.findById("testuser123"))
                .thenReturn(Optional.of(member));
        when(bCryptPasswordEncoder.matches("wrongpassword", "encoded_password"))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(BizException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");

        verify(memberRepository).findById("testuser123");
        verify(bCryptPasswordEncoder).matches("wrongpassword", "encoded_password");
        verify(jwtUtil, never()).createJwt(any(), any());
        }

        @Test
        @DisplayName("탈퇴한 회원")
        void withdrawnMember_throwsException() {
        // Given
        MemberLoginRequest request = new MemberLoginRequest("testuser123", "password123!");
        
        Member member = Member.builder()
                .memberId("testuser123")
                .password("encoded_password")
                .memberStsCd(MemberStsCdConst.WITHDRAWN)
                .build();

        when(memberRepository.findById("testuser123"))
                .thenReturn(Optional.of(member));
        when(bCryptPasswordEncoder.matches("password123!", "encoded_password"))
                .thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(BizException.class)
                .hasMessage(ResponseType.WITHDRAWN_MEMBER.message());

        verify(memberRepository).findById("testuser123");
        verify(bCryptPasswordEncoder).matches("password123!", "encoded_password");
        verify(jwtUtil, never()).createJwt(any(), any());
        }

        @Test
        @DisplayName("탈퇴 진행중인 회원")
        void withdrawingMember_throwsException() {
            // Given
            MemberLoginRequest request = new MemberLoginRequest("testuser123", "password123!");
            
            Member member = Member.builder()
                    .memberId("testuser123")
                    .password("encoded_password")
                    .memberStsCd(MemberStsCdConst.WITHDRAWING)  // 탈퇴 진행중
                    .build();

            when(memberRepository.findById("testuser123"))
                    .thenReturn(Optional.of(member));
            when(bCryptPasswordEncoder.matches("password123!", "encoded_password"))
                    .thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> memberService.login(request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.WITHDRAWING_PROGRESS.message());

            verify(memberRepository).findById("testuser123");
            verify(bCryptPasswordEncoder).matches("password123!", "encoded_password");
            verify(jwtUtil, never()).createJwt(any(), any());
        }
    }

    @Nested
    @DisplayName("회원 검증 테스트")
    class ValidateActiveMemberTest {

        @Test
        @DisplayName("정상 회원")
        void success() {
        // Given
        String memberId = "testuser123";
        Member member = Member.builder()
                .memberId(memberId)
                .memberStsCd(MemberStsCdConst.ACTIVE)
                .build();

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        // When
        Member result = memberService.validateActiveMemberByMemberId(memberId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getMemberStsCd()).isEqualTo(MemberStsCdConst.ACTIVE);

        verify(memberRepository).findById(memberId);
        }

        @Test
        @DisplayName("존재하지 않는 회원")
        void nonexistentMember_throwsException() {
        // Given
        String memberId = "nonexistent";

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> memberService.validateActiveMemberByMemberId(memberId))
                .isInstanceOf(BizException.class)
                .hasMessage(ResponseType.NOT_EXIST_MEMBER.message());

        verify(memberRepository).findById(memberId);
        }

        @Test
        @DisplayName("탈퇴 진행중인 회원")
        void withdrawingMember_throwsException() {
            // Given
            String memberId = "testuser123";
            Member member = Member.builder()
                    .memberId(memberId)
                    .memberStsCd(MemberStsCdConst.WITHDRAWING)  // 탈퇴 진행중
                    .build();

            when(memberRepository.findById(memberId))
                    .thenReturn(Optional.of(member));

            // When & Then
            assertThatThrownBy(() -> memberService.validateActiveMemberByMemberId(memberId))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.WITHDRAWING_PROGRESS.message());

            verify(memberRepository).findById(memberId);
        }
    }

    @Nested
    @DisplayName("탈퇴신청 테스트")
    class WithdrawTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            // Given
            String memberId = "testuser123";
            MemberWithdrawRequest request = new MemberWithdrawRequest("password123!");
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .password("encoded_password")
                    .memberStsCd(MemberStsCdConst.ACTIVE)
                    .regDt(LocalDate.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(bCryptPasswordEncoder.matches("password123!", "encoded_password")).thenReturn(true);
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // When
            MemberWithdrawResponse response = memberService.withdraw(memberId, request);

            // Then
            assertThat(response.getMemberId()).isEqualTo(memberId);
            assertThat(response.getClsDt()).isNotNull();

            verify(jwtUtil).getCurrentMemberId();
            verify(memberRepository).findById(memberId);
            verify(bCryptPasswordEncoder).matches("password123!", "encoded_password");
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("본인이 아닌 경우")
        void notOwnMember_throwsException() {
            // Given
            String memberId = "testuser123";
            String currentMemberId = "otheruser";
            MemberWithdrawRequest request = new MemberWithdrawRequest("password123!");

            when(jwtUtil.getCurrentMemberId()).thenReturn(currentMemberId);

            // When & Then
            assertThatThrownBy(() -> memberService.withdraw(memberId, request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.NOT_ALLOWED.message());

            verify(jwtUtil).getCurrentMemberId();
            verify(memberRepository, never()).findById(any());
        }

        @Test
        @DisplayName("잘못된 비밀번호")
        void wrongPassword_throwsException() {
            // Given
            String memberId = "testuser123";
            MemberWithdrawRequest request = new MemberWithdrawRequest("wrongpassword");
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .password("encoded_password")
                    .memberStsCd(MemberStsCdConst.ACTIVE)
                    .regDt(LocalDate.now())
                    .build();

            when(jwtUtil.getCurrentMemberId()).thenReturn(memberId);
            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(bCryptPasswordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> memberService.withdraw(memberId, request))
                    .isInstanceOf(BizException.class)
                    .hasMessage("비밀번호가 일치하지 않습니다.");

            verify(jwtUtil).getCurrentMemberId();
            verify(memberRepository).findById(memberId);
            verify(bCryptPasswordEncoder).matches("wrongpassword", "encoded_password");
            verify(memberRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("탈퇴취소 테스트")
    class CancelWithdrawTest {

        @Test
        @DisplayName("정상 케이스")
        void success() {
            // Given
            String memberId = "testuser123";
            MemberCancelWithdrawRequest request = new MemberCancelWithdrawRequest("password123!");
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .password("encoded_password")
                    .memberStsCd(MemberStsCdConst.WITHDRAWING)
                    .clsDt(LocalDate.now())
                    .build();

            when(memberRepository.findByMemberIdAndMemberStsCd(memberId, MemberStsCdConst.WITHDRAWING))
                    .thenReturn(Optional.of(member));
            when(bCryptPasswordEncoder.matches("password123!", "encoded_password")).thenReturn(true);
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // When
            memberService.cancelWithdraw(memberId, request);

            // Then
            verify(memberRepository).findByMemberIdAndMemberStsCd(memberId, MemberStsCdConst.WITHDRAWING);
            verify(bCryptPasswordEncoder).matches("password123!", "encoded_password");
            verify(memberRepository).save(any(Member.class));
        }

        @Test
        @DisplayName("탈퇴 진행중이 아닌 회원")
        void notWithdrawingMember_throwsException() {
            // Given
            String memberId = "testuser123";
            MemberCancelWithdrawRequest request = new MemberCancelWithdrawRequest("password123!");

            when(memberRepository.findByMemberIdAndMemberStsCd(memberId, MemberStsCdConst.WITHDRAWING))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> memberService.cancelWithdraw(memberId, request))
                    .isInstanceOf(BizException.class)
                    .hasMessage(ResponseType.BAD_REQUEST.message());

            verify(memberRepository).findByMemberIdAndMemberStsCd(memberId, MemberStsCdConst.WITHDRAWING);
            verify(bCryptPasswordEncoder, never()).matches(any(), any());
            verify(memberRepository, never()).save(any());
        }

        @Test
        @DisplayName("잘못된 비밀번호")
        void wrongPassword_throwsException() {
            // Given
            String memberId = "testuser123";
            MemberCancelWithdrawRequest request = new MemberCancelWithdrawRequest("wrongpassword");
            
            Member member = Member.builder()
                    .memberId(memberId)
                    .password("encoded_password")
                    .memberStsCd(MemberStsCdConst.WITHDRAWING)
                    .clsDt(LocalDate.now())
                    .build();

            when(memberRepository.findByMemberIdAndMemberStsCd(memberId, MemberStsCdConst.WITHDRAWING))
                    .thenReturn(Optional.of(member));
            when(bCryptPasswordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> memberService.cancelWithdraw(memberId, request))
                    .isInstanceOf(BizException.class)
                    .hasMessage("비밀번호가 일치하지 않습니다.");

            verify(memberRepository).findByMemberIdAndMemberStsCd(memberId, MemberStsCdConst.WITHDRAWING);
            verify(bCryptPasswordEncoder).matches("wrongpassword", "encoded_password");
            verify(memberRepository, never()).save(any());
        }
    }
}
