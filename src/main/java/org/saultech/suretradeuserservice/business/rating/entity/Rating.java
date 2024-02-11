package org.saultech.suretradeuserservice.business.rating.entity;

import lombok.*;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("Rating")
public class Rating extends BaseEntity {
    @Column("rating")
    public Double rating;
    @Column("comment")
    public String comment;
    @Column("userId")
    public Long userId;
    @Column("raterId")
    public Long raterId;
}
