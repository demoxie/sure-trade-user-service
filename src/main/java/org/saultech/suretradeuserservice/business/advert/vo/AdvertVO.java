package org.saultech.suretradeuserservice.business.advert.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AdvertVO {
    private Long id;
    private String title;
    private String description;
    public String imageUrls;
    public String url;
    public String status;
}
