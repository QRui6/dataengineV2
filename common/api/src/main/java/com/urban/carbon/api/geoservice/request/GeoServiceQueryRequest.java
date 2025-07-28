package com.urban.carbon.api.geoservice.request;

import com.urban.carbon.api.geoservice.request.condition.GeoServiceIdsQueryCondition;
import com.urban.carbon.api.geoservice.request.condition.GeoServiceNameQueryCondition;
import com.urban.carbon.api.geoservice.request.condition.GeoServiceloginIdQueryCondition;
import com.urban.carbon.base.request.BaseRequest;
import com.urban.carbon.base.request.QueryCondition;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GeoServiceQueryRequest extends BaseRequest {

    private QueryCondition condition;

    public GeoServiceQueryRequest(List<Long> ids, Long loginId) {
        this.condition = new GeoServiceIdsQueryCondition(ids);
        this.setLoginId(loginId);
    }

    public GeoServiceQueryRequest(Long loginId) {
        this.condition = new GeoServiceloginIdQueryCondition(loginId);
    }

    public GeoServiceQueryRequest(String serviceName) {
        this.condition = new GeoServiceNameQueryCondition(serviceName);
    }
}
