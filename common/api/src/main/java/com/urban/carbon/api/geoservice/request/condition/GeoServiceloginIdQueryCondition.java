package com.urban.carbon.api.geoservice.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GeoServiceloginIdQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long loginId;
}
