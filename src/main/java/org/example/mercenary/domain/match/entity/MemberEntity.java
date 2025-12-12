package org.example.mercenary.domain.match.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카카오에서 부여하는 고유 번호 (회원 식별용)
    @Column(nullable = false, unique = true)
    private Long kakaoId;

    private String email;

    private String nickname;

    // 권한 (USER, ADMIN 등)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public MemberEntity(Long kakaoId, String email, String nickname, Role role) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        this.role = role != null ? role : Role.USER;
    }

    public enum Role {
        USER, ADMIN
    }
}
