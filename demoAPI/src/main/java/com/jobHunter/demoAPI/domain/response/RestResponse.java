package com.jobHunter.demoAPI.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private int statusCode;
    private String error;
    private Object message; // message có thể là bất cứ kiểu dữ liệu, cần ép kiểu
    private T data; // (T thì ko cần)
}
