package org.saultech.suretradeuserservice.user.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table("UserDeviceDetails")
public class UserDeviceDetails extends BaseEntity{
    @Column("userId")
    private Long userId;
    @Column("role")
    private String role;
    @Column("deviceToken")
    private String deviceToken;
    @Column("ip")
    private String ip;
    @Column("userAgent")
    private String userAgent;
}
