package com.cursor.common.dto;

import com.cursor.common.enum_.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private StatusEnum status;
    private String code;
    private String message;
    private T data;
}


