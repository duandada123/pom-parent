package com.sinogale.dksdkdemo.service;

import com.sinogale.dksdkdemo.config.ConfigKeys;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.aliyun.dataworks_20240902.Client;
import com.aliyun.dataworks_20240902.models.*;
import com.aliyun.tea.TeaModel;
import org.springframework.stereotype.Service;

@Service
public class DataWorksSdkInvoker {


    private final Client client;

    public DataWorksSdkInvoker(Client client) {
        this.client = client;
    }

    /**
     * 动态执行 DataWorks API
     * @param actionName API 动作名称
     * @param params 请求参数
     * @return 包含执行结果的 Map
     */
    public Map<String, Object> executeFunction(String actionName, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 构建请求类名（如：RunCycleDagNodesRequest）
            String requestClassName = ConfigKeys.REQUEST_PACKAGE  + actionName + "Request";
            // 通过反射创建请求对象
            Class<?> requestClass = Class.forName(requestClassName);
            Constructor<?> constructor = requestClass.getDeclaredConstructor();
            TeaModel request = (TeaModel) constructor.newInstance();
            // 设置请求参数
            processRequestParams(request, params);
            // 构建方法名（如：runCycleDagNodes）
            String methodName = actionName.substring(0, 1).toLowerCase() + actionName.substring(1);
            Method clientMethod = Client.class.getMethod(methodName,requestClass);
            Object response = clientMethod.invoke(client,request);
            Object body = response.getClass().getMethod("getBody").invoke(response);
            boolean success = body != null && Boolean.TRUE.equals(body.getClass().getMethod("getSuccess").invoke(body));
            result.put("success", success);
            if (!success && body != null) {
                result.put("errCode", body.getClass().getMethod("getErrorCode").invoke(body));
                result.put("errorMessage", body.getClass().getMethod("getErrorMessage").invoke(body));
            }
            result.put("response", response);
            result.put("requestId", extractRequestId(response));
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            result.put("success", false);
            result.put("errorMessage", "执行请求失败: " + target.getMessage());
            Object errorCode = invokeNoArg(target, "getCode");
            Object requestId = invokeNoArg(target, "getRequestId");
            Object errorData = invokeNoArg(target, "getData");
            if (errorData instanceof Map) {
                Map<?, ?> data = (Map<?, ?>) errorData;
                if (requestId == null) requestId = firstNotNull(data.get("RequestId"), data.get("requestId"));
                if (errorCode == null) errorCode = firstNotNull(data.get("Code"), data.get("code"));
            }
            if (errorCode != null) result.put("errCode", errorCode);
            if (requestId != null) result.put("requestId", requestId);
        } catch (ReflectiveOperationException e) {
            result.put("success", false);
            result.put("errorMessage", "SDK 方法或参数不匹配: " + e.getMessage());
        } catch (Exception e) {
            result.put("success", false);
            result.put("errorMessage", "未知异常: " + e.getMessage());
        }
        if (!Boolean.TRUE.equals(result.get("success"))) {
            // 兼容原调用方使用 errMsg/errCode，Controller 使用 errorMessage 的写法。
            if (result.get("errorMessage") == null) result.put("errorMessage", "DataWorks 返回失败或响应体为空");
            result.put("errMsg", result.get("errorMessage"));
            if (result.get("errCode") == null) result.put("errCode", "SDK_ERROR");
        }
        return result;
    }

    /**
     * 处理请求参数，包括 NodeParams 的特殊处理
     */
    private void processRequestParams(TeaModel request, Map<String, Object> params)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> requestClass = request.getClass();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            if(Objects.isNull(paramValue)) continue;
            // 特殊处理 NodeParams：将 Map 转换为 JSON 字符串
//            if ("NodeParams".equalsIgnoreCase(paramName) && paramValue instanceof Map) {
//                paramValue = JSON.toJSONString(paramValue);
//            }
            // 拼接 setter 方法名（如：setNodeParams）
            String methodName = "set" + paramName.substring(0, 1).toUpperCase() + paramName.substring(1);
            // 查找并调用 setter 方法
            Method method = findSetterMethod(requestClass, methodName, paramValue);
            method.invoke(request, paramValue);
        }
    }

    /**
     * 查找匹配的 setter 方法
     */
    private Method findSetterMethod(Class<?> clazz, String methodName, Object value) throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, value.getClass());
        } catch (NoSuchMethodException e) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    if (isAssignable(paramType, value.getClass())) {
                        return method;
                    }
                }
            }
            throw e;
        }
    }

    /**
     * 判断类型是否可赋值
     */
    private boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }
        // 处理基础类型与包装类的转换
        if (targetType == int.class && sourceType == Integer.class) return true;
        if (targetType == long.class && sourceType == Long.class) return true;
        if (targetType == double.class && sourceType == Double.class) return true;
        if (targetType == boolean.class && sourceType == Boolean.class) return true;
        return false;
    }

    /**
     * 从响应中提取 RequestId
     */
    private String extractRequestId(Object response) {
        try {
            Object body = response.getClass().getMethod("getBody").invoke(response);
            Method method = body.getClass().getMethod("getRequestId");
            return (String) method.invoke(body);
        } catch (Exception e) {
            return "unknown";
        }
    }

    private Object invokeNoArg(Object target, String methodName) {
        if (target == null) return null;
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Object firstNotNull(Object first, Object second) {
        return first == null ? second : first;
    }


}
