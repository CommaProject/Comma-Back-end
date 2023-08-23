package com.team.comma.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "following_tb")
public class Following {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean blockFlag;

    @Setter
    @JoinColumn(name = "user_from")
    @ManyToOne(cascade = CascadeType.PERSIST , fetch = FetchType.LAZY)
    private User userFrom;

    @Setter
    @JoinColumn(name = "user_to")
    @ManyToOne(cascade = CascadeType.PERSIST , fetch = FetchType.LAZY)
    private User userTo;

    public static Following createFollowingToFrom(User userTo, User userFrom) {
        return Following.builder()
                .blockFlag(false)
                .userTo(userTo)
                .userFrom(userFrom)
                .build();
    }

}
