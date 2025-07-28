package com.urban.carbon.api.geoservice.request;

import com.urban.carbon.api.geoservice.request.condition.GeoServiceNameQueryCondition;
import com.urban.carbon.api.geoservice.request.condition.GeoServiceloginIdQueryCondition;
import com.urban.carbon.base.request.PageRequest;
import com.urban.carbon.base.request.QueryCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeoServicePageQueryRequest extends PageRequest {

    private QueryCondition condition;

    public GeoServicePageQueryRequest(Long loginId) {
        this.condition = new GeoServiceloginIdQueryCondition(loginId);
    }

    public GeoServicePageQueryRequest(String serviceName, Long loginId) {
        this.condition = new GeoServiceNameQueryCondition(serviceName);
        this.setLoginId(loginId);
    }
}
