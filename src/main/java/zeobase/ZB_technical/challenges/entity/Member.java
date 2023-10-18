package zeobase.ZB_technical.challenges.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zeobase.ZB_technical.challenges.type.MemberRoleType;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberId;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private MemberRoleType role;

    private String phone;

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "member")
    private List<Store> stores;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews;
}
