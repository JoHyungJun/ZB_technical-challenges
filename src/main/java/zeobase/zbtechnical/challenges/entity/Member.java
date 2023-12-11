package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zeobase.zbtechnical.challenges.type.member.MemberRoleType;
import zeobase.zbtechnical.challenges.type.member.MemberSignedStatusType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 이용자 관련 Entity 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String UID;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private MemberRoleType role;

    private String name;

    @Column(unique = true)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private MemberSignedStatusType status = MemberSignedStatusType.ACTIVE;

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Store> stores = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(this.getStatus().name()));

        return grantedAuthorities;
    }

    @Override
    public String getUsername() {

        return this.getUID();
    }

    @Override
    public boolean isAccountNonExpired() {

        return this.getStatus() != MemberSignedStatusType.WITHDRAW;
    }

    @Override
    public boolean isAccountNonLocked() {

        return this.getStatus() != MemberSignedStatusType.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return this.getStatus() == MemberSignedStatusType.ACTIVE;
    }

    public Member modifyUID(String UID) {

        this.UID = UID;

        return this;
    }

    public Member modifyPasswordByEncodedPassword(String encodedPassword) {

        this.password = encodedPassword;

        return this;
    }

    public Member modifyRole(MemberRoleType role) {

        this.role = role;

        return this;
    }

    public Member modifyName(String name) {

        this.name = name;

        return this;
    }

    public Member modifyPhone(String phone) {

        this.phone = phone;

        return this;
    }

    public Member modifyStatus(MemberSignedStatusType status) {

        this.status = status;

        return this;
    }
}
