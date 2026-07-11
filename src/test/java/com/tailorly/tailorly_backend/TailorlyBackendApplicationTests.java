package com.tailorly.tailorly_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"jwt.secret=test-secret",
		"jwt.expiration=3600000",
		"spring.data.mongodb.uri=mongodb://localhost:27017/tailorly_test",
		"cloudinary.cloud-name=test",
		"cloudinary.api-key=test",
		"cloudinary.api-secret=test",
		"openai.api-key=test"
})
class TailorlyBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
