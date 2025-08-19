package com.sinogale.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public abstract class AbstractJpaResponse implements Response {
    private int version;
    private Long id;
    private Long createdAt;
    private Long updatedAt;

}
