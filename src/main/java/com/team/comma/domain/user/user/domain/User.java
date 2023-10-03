package com.team.comma.domain.user.user.domain;

import com.team.comma.domain.artist.domain.Artist;
import com.team.comma.domain.favorite.artist.domain.FavoriteArtist;
import com.team.comma.domain.user.history.domain.History;
import com.team.comma.domain.user.detail.domain.UserDetail;
import com.team.comma.domain.user.user.constant.UserRole;
import com.team.comma.domain.user.user.constant.UserType;
import com.team.comma.global.converter.BooleanConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tb")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 100)
    private String password;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @CreationTimestamp
    private Date joinDate;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean delFlag = false;

    @Setter
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private UserDetail userDetail;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<FavoriteArtist> favoriteArtist = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.PERSIST , mappedBy = "user")
    private List<History> history = new ArrayList<>();

    public static User buildUser(Long id, String email, String password, UserType type) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .type(type)
                .role(UserRole.USER)
                .build();
    }

    public static User createUser(String email, String password, UserType type) {
        return buildUser(null, email, password, type);
    }

    public static User createUser(Long id) {
        return buildUser(id, "email", "password", UserType.GENERAL_USER);
    }

    public static User createUser(String email) {
        return buildUser(null, email, "password", UserType.GENERAL_USER);
    }

    public static User createUser() {
        return buildUser(null, "email", "password", UserType.GENERAL_USER);
    }

    public void addFavoriteArtist(Artist artist) {
        FavoriteArtist artistData = FavoriteArtist.builder()
            .artist(artist)
            .user(this)
            .build();

        favoriteArtist.add(artistData);
    }

    public void addHistory(String history) {
        History historyEntity = History.builder()
                .searchHistory(history)
                .delFlag(false)
                .user(this)
                .build();

        this.history.add(historyEntity);
    }

    public void modifyPassword(String password){
        this.password = password;
    }

    // JWT Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getKey()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
