package com.sinogale.common.mapper;

import com.sinogale.common.constants.BitFlag;
import com.sinogale.common.constants.ResourceType;
import com.sinogale.common.constants.ValidStatus;

/**
 * @ClassName GenericEnumMapper
 * @Author duanchao
 * @Date 2021/7/10 0:36
 **/


public class GenericEnumMapper {
    public GenericEnumMapper() {
    }

    public Integer asInteger(ValidStatus status) {
        return status.getCode();
    }

    public ValidStatus asValidStatus(Integer code) {
        return (ValidStatus)ValidStatus.of(code).orElse(ValidStatus.INVALID);
    }

    public Integer asInteger(BitFlag flag) {
        return flag.getCode();
    }

    public BitFlag asBitFlag(Integer code) {
        return (BitFlag)BitFlag.of(code).orElse(BitFlag.N);
    }

    public Integer asInteger(ResourceType type) {
        return type.getCode();
    }

    public ResourceType asResourceType(Integer code) {
        return (ResourceType)ResourceType.of(code).orElse(ResourceType.MODULE);
    }
}
