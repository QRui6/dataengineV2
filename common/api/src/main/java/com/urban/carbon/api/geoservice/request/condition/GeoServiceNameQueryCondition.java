package com.urban.carbon.api.geoservice.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeoServiceNameQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = -1L;

    private String serviceName;
}
