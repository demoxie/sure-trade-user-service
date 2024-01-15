package org.saultech.suretradeuserservice.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table("BankDetails")
public class BankDetails extends BaseEntity {
    @Column("bankName")
    private String bankName;
    @Column("accountName")
    private String accountName;
    @Column("accountNumber")
    private String accountNumber;
    @Column("currency")
    private String currency;
    @Column("accountType")
    private String accountType;
    @Column("bankCode")
    private String bankCode;
    @Column("bankCountry")
    private String bankCountry;
    @Column("userId")
    private Long userId;
}
