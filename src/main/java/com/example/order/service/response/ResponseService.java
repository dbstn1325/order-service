package com.example.order.service.response;

import com.example.order.model.response.CommonResult;
import com.example.order.model.response.ListResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    /**
     * List 타입 데이터의 응답 메시지를 가공하는 메서드
     * @param list List 타입의 DTO
     * @param <T> DTO 타입
     * @return DTO를 포함한 성공 응답
     */
    public <T> ListResult<T> getListResult(List<T> list){
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        addSuccessResult(result);
        return result;
    }

    /**
     * 응답 모델을 성공 모델로 설정하는 메서드
     * @param result data(List or Single)를 담은 응답 모델
     */
    private void addSuccessResult(CommonResult result){
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMessage(CommonResponse.SUCCESS.getMessage());
    }
}
