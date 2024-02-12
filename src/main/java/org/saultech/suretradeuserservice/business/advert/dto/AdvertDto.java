package org.saultech.suretradeuserservice.business.advert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdvertDto {
    private String title;
    private String description;
    public String imageUrls;
    public String url;
}
