package com.jobHunter.demoAPI.util.response;

import com.jobHunter.demoAPI.domain.response.RestResponse;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    // Hàm xác định khi nào sử dụng interceptor
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // áp dụng all controller methods (k phân biệt tên or kiểu data trả về)
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        /* check data response (content-type) if is not application/json
         * -> return body and not be wrapped by RestResponse
         * */
/*        if (!MediaType.APPLICATION_JSON.equals(selectedContentType)) {
            return body;
        }*/

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(status);

        if (status >= 400) {
            // case error
            return body;
        } else {
            // case success
            restResponse.setData(body);
            ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
            restResponse.setMessage(
                    apiMessage != null
                            ? apiMessage.value()
                            : "Call api successfully"
            );
        }

        return restResponse;
    }
}
