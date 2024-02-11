package org.saultech.suretradeuserservice.business.advert.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.saultech.suretradeuserservice.user.entity.BaseEntity;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table("Adverts")
public class Advert extends BaseEntity {
    @Column(value = "title")
    public String title;
    @Column(value = "description")
    public String description;
    @Column(value = "imageUrls")
    public String imageUrls;
    @Column(value = "url")
    public String url;
    @Column(value = "status")
    public String status;
}
