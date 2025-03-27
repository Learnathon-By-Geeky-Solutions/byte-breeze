package com.bytebreeze.quickdrop;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.bytebreeze.quickdrop.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class QuickdropApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Mock
	private FileStorageService fileStorageService;

	@Test
	void contextLoads() {
		assertNotNull(context);
	}
}
