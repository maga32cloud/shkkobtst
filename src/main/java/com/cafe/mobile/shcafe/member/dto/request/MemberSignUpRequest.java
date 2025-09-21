package com.cafe.mobile.shcafe.member.dto.request;

import com.cafe.mobile.shcafe.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class MemberSignUpRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    @Schema(description = "회원 아이디", example = "testuser123", required = true)
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", example = "password123!", required = true)
    private String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Schema(description = "이메일 주소", example = "test@example.com", required = true)
    private String email;

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "회원 이름", example = "홍길동", required = true)
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "전화번호는 9~12자리 숫자여야 합니다.")
    @Schema(description = "전화번호 (9~12자리 숫자)", example = "01012345678", required = true)
    private String telNo;

    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^[MW]$", message = "성별은 M 또는 W만 입력 가능합니다.")
    @Schema(description = "성별 (M: 남성, W: 여성)", example = "M", allowableValues = {"M", "W"}, required = true)
    private String gender;

    @JsonFormat(pattern = "yyyyMMdd")
    @NotNull(message = "생년월일은 필수입니다.")
    @Schema(description = "생년월일 (yyyyMMdd 형식)", example = "19901225", required = true)
    private LocalDate birthDt;

    // DTO - Entity 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .memberId(memberId)
                .password(encodedPassword)
                .email(email)
                .name(name)
                .telNo(telNo)
                .gender(gender)
                .birthDt(birthDt)
                .regDt(LocalDate.now())
                .build();
    }
}
