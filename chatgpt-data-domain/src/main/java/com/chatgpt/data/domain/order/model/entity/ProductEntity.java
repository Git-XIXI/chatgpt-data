package com.chatgpt.data.domain.order.model.entity;

import com.chatgpt.data.types.enums.OpenAIProductEnableModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 商品实体对象
 * @date 2024/3/17 0:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {
    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品描述
     */
    private String productDesc;
    /**
     * 额度次数
     */
    private Integer quota;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 商品状态；0-无效、1-有效
     */
    private OpenAIProductEnableModel enable;
    public boolean isAvailable() {
        return OpenAIProductEnableModel.OPEN.equals(enable);
    }

}
