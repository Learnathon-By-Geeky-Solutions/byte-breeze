package com.bytebreeze.quickdrop.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/files")
public class FileController {

	private static final Logger logger = Logger.getLogger(FileController.class.getName());

	@Value("${storage.local.path}")
	private String uploadDir;

	@GetMapping("/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		try {
			Path filePath = Paths.get(uploadDir).resolve(filename).normalize();

			// Prevent directory traversal attack
			if (!filePath.startsWith(Paths.get(uploadDir))) {
				return ResponseEntity.badRequest().body(null);
			}

			Resource resource = new UrlResource(filePath.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				return ResponseEntity.notFound().build();
			}

			// Determine content type
			Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(resource);
			return ResponseEntity.ok()
					.contentType(mediaType.orElse(MediaType.APPLICATION_OCTET_STREAM))
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
					.body(resource);

		} catch (MalformedURLException e) {
			logger.severe("Malformed file URL: " + e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
}
