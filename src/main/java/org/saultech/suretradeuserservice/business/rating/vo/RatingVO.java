package org.saultech.suretradeuserservice.business.rating.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.saultech.suretradeuserservice.user.entity.User;
import org.saultech.suretradeuserservice.user.vo.UserProfileVO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingVO {
    private Double rating;
    private Integer ratingCount;

    private List<UserProfileVO> raters;
}
