package com.chatgpt.data.infrastructure.dao;

import com.chatgpt.data.infrastructure.po.OpenAIProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOpenAIProductDao {
    OpenAIProductPO queryProductByProductId(Integer productId);
    List<OpenAIProductPO> queryProductList();
}
