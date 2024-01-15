package org.saultech.suretradeuserservice.user.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.enums.Gender;
import org.saultech.suretradeuserservice.user.enums.Role;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "Users")
public class User extends BaseEntity implements UserDetails {
    @Column("firstName")
    private String firstName;
    @Column("lastName")
    private String lastName;
    @Column("middleName")
    private String middleName;
    @Column("username")
    private String username;
    @Column("token")
    private String token;
    @Column("otp")
    private String otp;
    @Column("transactionPin")
    private String transactionPin;
//    @Enumerated(EnumType.STRING)
    @Column("role")
    private Role roles;
    @Column("walletAddress")
    private String walletAddress;
    @Column("nonce")
    private String nonce;
    @Column("phoneNumber")
    private String phoneNumber;
    @Column("telegram")
    private String telegram;
    @Column("profilePicture")
    private String profilePicture;
    @Column("address")
    private String address;
    @Column("city")
    private String city;
    @Column("state")
    private String state;
    @Column("country")
    private String country;

//    @Enumerated(EnumType.STRING)
    @Column("gender")
    private Gender gender;
    @Column("email")
    private String email;
    @Column("password")
    private String password;
    @Column("tierId")
    private Long tierId;
    @Column("transactionProfileId")
    private Long transactionProfileId;
    @Column("referralCodes")
    private String referralCodes;
    @Column("telegramChatId")
    private String telegramChatId;
    @Column("isActive")
    private boolean isActive;
    @Column("isSuspended")
    private boolean isSuspended;
    @Column("isVerified")
    private boolean isVerified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(this.roles).stream().map(role -> new SimpleGrantedAuthority(role.name())).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isSuspended;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isActive;
    }

    @Override
    public boolean isEnabled() {
        return this.isVerified;
    }
}
