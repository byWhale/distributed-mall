package com.mall.order.biz.handler;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.biz.context.CreateOrderContext;
import com.mall.order.biz.context.TransHandlerContext;
import com.mall.user.IMemberService;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.QueryMemberRequest;
import com.mall.user.dto.QueryMemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * ciggar
 * create-date: 2019/8/1-下午4:47
 */
@Slf4j
@Component
public class ValidateHandler extends AbstractTransHandler {

    @Reference
    private IMemberService memberService;

    /**
     * 验证用户合法性
     *
     * @return
     */

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public boolean handle(TransHandlerContext context) {
        CreateOrderContext createOrderContext = (CreateOrderContext) context;

        QueryMemberRequest request = new QueryMemberRequest();
        request.setUserId(createOrderContext.getUserId());
        QueryMemberResponse response = memberService.queryMemberById(request);
        if (response.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())) {
            String username = response.getUsername();
            if (username == null) {
                throw new BizException(response.getCode(), response.getMsg());
            }
            createOrderContext.setBuyerNickName(username);
        }

        return true;
    }
}
