package com.chatgpt.data;

import cn.bugstack.chatglm.model.Model;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatgptDataAppApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test() {
		Model model = Model.valueOf("GLM_3_5_TURBO");
		System.out.println(model);
	}

}
