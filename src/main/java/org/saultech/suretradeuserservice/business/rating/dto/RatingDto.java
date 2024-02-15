package org.saultech.suretradeuserservice.business.rating.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    public Double rating;
    public String comment;
    public Long userId;
}
