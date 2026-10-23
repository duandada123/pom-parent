package com.sinogale.dksdkdemo.dto.response;

import com.sinogale.dksdkdemo.entity.DwInstanceLogDetail;
import lombok.Data;
import java.util.Collections;
import java.util.List;

@Data
public class QueryLogsResponse {
    private Long instanceId;
    private Long nodeId;
    private String logContent;
    private List<DwInstanceLogDetail> logs = Collections.emptyList();
}
