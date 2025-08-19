package com.sinogale.common.model;

import com.sinogale.common.constants.BaseEnum;
import com.sinogale.common.constants.ErrorCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

@Data
public final class OpenAPIResponse<T>
        implements Serializable {

    private Integer code;

    private String msg;

    private T result;

    public static <E> OpenAPIResponse<E> success(E e) {
        OpenAPIResponse<E> object = new OpenAPIResponse();
        object.setCode(ErrorCode.SUCCESS.getCode());
        object.setMsg(ErrorCode.SUCCESS.getName());
        object.setResult(e);
        return object;
    }

    public static <E> OpenAPIResponse<E> success(E t, String msg) {
        OpenAPIResponse<E> obj = success(t);
        obj.setMsg(msg);
        return obj;
    }

    public static OpenAPIResponse fail(BaseEnum ErrorCode) {
        OpenAPIResponse object = new OpenAPIResponse();
        object.setMsg(ErrorCode.getName());
        object.setCode(ErrorCode.getCode());
        return object;
    }

    public static OpenAPIResponse fail(String msg) {
        OpenAPIResponse object = new OpenAPIResponse();
        object.setMsg(msg);
        object.setCode(ErrorCode.FAIL.getCode());
        return object;
    }

    public static <E> OpenAPIResponse<E> fail(E e, Integer code, String msg) {
        OpenAPIResponse<E> object = new OpenAPIResponse();
        object.setCode(code);
        object.setMsg(msg);
        object.setResult(e);
        return object;
    }

    public static <E> OpenAPIResponse<E> fail(E e, String msg) {
        OpenAPIResponse<E> object = new OpenAPIResponse();
        object.setCode(ErrorCode.FAIL.getCode());
        object.setMsg(msg);
        object.setResult(e);
        return object;
    }

    public static <E> OpenAPIResponse<E> res(BaseEnum ErrorCode, E t) {
        OpenAPIResponse object = new OpenAPIResponse();
        object.setMsg(ErrorCode.getName());
        object.setCode(ErrorCode.getCode());
        object.setResult(t);
        return object;
    }

    public static boolean isSuccess(OpenAPIResponse res) {
        return Objects.equals(ErrorCode.SUCCESS.getCode(), res.getCode());
    }


    public <X extends Throwable> T isOrThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if(isSuccess(this)){
            return this.result;
        }else {
            throw  exceptionSupplier.get();
        }
    }
}