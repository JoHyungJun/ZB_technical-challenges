package zeobase.zbtechnical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zeobase.zbtechnical.challenges.type.MemberRoleType;
import zeobase.zbtechnical.challenges.type.MemberStatusType;

import javax.persistence.*;
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
    private MemberStatusType status;

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Store> stores;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews;


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

        return this.getStatus() != MemberStatusType.WITHDRAW;
    }

    @Override
    public boolean isAccountNonLocked() {

        return this.getStatus() != MemberStatusType.BLOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return this.getStatus() == MemberStatusType.ACTIVE;
    }
}
