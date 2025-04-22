package com.bytebreeze.quickdrop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "storage.local.path=uploads")
class FileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Value("${storage.local.path}")
	private String uploadDir;

	private Path validFilePath;

	@BeforeEach
	void setup() throws IOException {
		Files.createDirectories(Paths.get(uploadDir));
		validFilePath = Paths.get(uploadDir, "test.txt");
		Files.writeString(validFilePath, "This is a test file");
	}

	@Test
	void testGetFile_Success() throws Exception {
		mockMvc.perform(get("/files/test.txt"))
				.andExpect(status().isOk())
				.andExpect(header().string("Content-Disposition", "inline; filename=\"test.txt\""))
				.andExpect(content().contentType(MediaType.TEXT_PLAIN))
				.andExpect(content().string("This is a test file"));
	}

	@Test
	void testGetFile_FileNotFound() throws Exception {
		mockMvc.perform(get("/files/nonexistent.txt")).andExpect(status().isNotFound());
	}

	@Test
	void testGetFile_MalformedUrl() throws Exception {
		mockMvc.perform(get("/files/%")).andExpect(status().isBadRequest());
	}
}
