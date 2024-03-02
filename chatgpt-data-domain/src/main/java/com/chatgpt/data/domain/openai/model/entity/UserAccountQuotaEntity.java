package com.chatgpt.data.domain.openai.model.entity;

import com.chatgpt.data.domain.openai.model.valobj.UserAccountStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @description: 用户账号额度实体对象
 * @date 2024/3/1 23:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountQuotaEntity {

    /**
     * 用户ID
     */
    private String openId;
    /**
     * 总额度
     */
    private Integer totalQuota;
    /**
     * 剩余额度
     */
    private Integer surplusQuota;
    /**
     * 账号状态
     */
    private UserAccountStatusVO userAccountStatusVO;
    /**
     * 模型类型
     */
    private List<String> allowModelTypeList;

    public void genModelTypes(String modelTypes) {
        String[] vals = modelTypes.split(",");
        this.allowModelTypeList = Arrays.asList(vals);
    }
}
