/**
 * 
 */
package com.matd.mock.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.extern.java.Log;

/**
 * @author madhu
 *
 *
 * https://docs.trellix.com/bundle/advanced-threat-defense-4.12.x-api-reference-guide/page/GUID-F600CDC5-827A-4435-BD37-E0DF91810AB1.html
 */

@RestController
@Validated
@Log
public class LogInToMatdController {

	private final ExecutorService executorService = Executors.newCachedThreadPool();

	@GetMapping("/login")
	public ResponseEntity<String> myEndpoint(
			@NotBlank @RequestHeader("Accept") String accepthHeaderVlaue,
			@NotBlank @RequestHeader(value = "Content-Type", defaultValue = "application/json") String contentTypeHeader,
			@NotBlank @RequestHeader(value = "VE-SDK-API", defaultValue = "") String veSDKApiString) {	
			
        String randomString = RandomStringUtils.random(26,Boolean.TRUE,  Boolean.TRUE);
        String randomUserId = RandomStringUtils.random(2,Boolean.FALSE,Boolean.TRUE);
        
		String responseData= "\n"
				+ "{\n"
				+ "   \"success\": true,\n"
				+ "   \"results\": {\n"
				+ "    \"session\": \"%s\",\n"
				+ "    \"userId\": \"%s\",\n"
				+ "    \"isAdmin\":\"0\",\n"
				+ "    \"serverTZ\": \"CEST\",\n"
				+ "    \"apiVersion\": \"1.5.0\",\n"
				+ "    \"matdVersion\": \"4.14.2.9\",\n"
				+ "    \"warning\": \"Client API version(1.0.0) is older than Server API version (1.5.0), some feature may not be avaialable, please refer the REST API-1.5.0 documentation\"\n"
				+ "   }\n"
				+ "}\n"
				+ "";
		
		
		String responseData2 = "{\"success\": true, \"results\": {\"session\": \"%s\",\"userId\": \"%s\",\"isAdmin\": \"1\",\"serverTZ\": \"PDT\",\"apiVersion\": \"1.5.0\",\"isCurrentAPI\": false, \"matdVersion\": \"4.x.x.x.x\",\"warning\": \"Client API version(1.0.0) is older than Server API version(1.5.0), some feature may not be available, please refer REST API-1.5.0 documentation\"} }";

		return ResponseEntity.ok(String.format(responseData2, randomString,randomUserId));
	}

	/**
	 * curl -X POST \ -H "Content-Type: multipart/form-data" \ -F
	 * "file=@/path/to/file" \ -F "name=John Doe" \ -F "email=johndoe@example.com" \
	 * -F "jsonData={\"id\": 123, \"title\": \"Example Title\", \"description\":
	 * \"Example Description\"}" \ http://localhost:8080/api/upload
	 * 
	 * @param file
	 * @param name
	 * @param email
	 * @param jsonData
	 * @return
	 */
	@PostMapping(value="/upload")
	public ResponseEntity<String> handleFileUpload(@NotBlank @RequestHeader("Accept") String accepthHeaderVlaue,
			@NotBlank @RequestHeader(value = "Content-Type") String contentTypeHeader,
			@NotBlank @RequestHeader(value = "VE-SDK-API") String veSDKApiString,
			@RequestParam("amas_filename") MultipartFile file, @RequestPart("data") String jsonData) {
		System.out.println("File Upload pending here");
	if (!contentTypeHeader.contains("boundary=")) {
            return ResponseEntity.badRequest().body("Multipart boundary not found in Content-Type header.");
        }
		
		System.out.println("The Original File name --->: {}"+file.getOriginalFilename());
		
		String responseData = "{\n"
				+ "  \"success\": true,\n"
				+ "  \"subId\": 144115188077973274,\n"
				+ "  \"mimeType\": \"application/vnd.openxmlformats-officedocument.presentational.presentation\",\n"
				+ "  \"fieldId\": \"\",\n"
				+ "  \"fileWait\": 0,\n"
				+ "  \"estimatedTime\": 0,\n"
				+ "  \"results\": [\n"
				+ "    {\n"
				+ "      \"taskId\": 144115188079332956,\n"
				+ "      \"messageId\": \"\",\n"
				+ "      \"file\": \"%s\",\n"
				+ "      \"submitType\": \"0\",\n"
				+ "      \"url\": \"\",\n"
				+ "      \"destIp\": \"\"\n"
				+ "    }\n"
				+ "  ]\n"
				+ "}\n"
				+ "";
		
		return ResponseEntity.ok(String.format(responseData, file.getName()));
	}

	@GetMapping("/showreport")
	public DeferredResult<ResponseEntity<String>> longPollingEndpoint(
			@RequestHeader("Accept") @NotBlank(message = "Accept header is required") String accepthHeaderVlaue,
			@NotBlank @RequestHeader(value = "Content-Type", defaultValue = "application/json") String contentTypeHeader,
			@NotBlank @RequestHeader(value = "VE-SDK-API", defaultValue = "") String veSDKApiString) {
		System.out.println("ShowReport method calling..........");
		DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>();
		CompletableFuture.supplyAsync(() -> performLongRunningTask(), executorService)
				.thenAccept(result -> deferredResult.setResult(ResponseEntity.ok(result))).exceptionally(ex -> {
					deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
					return null;
				});

		return deferredResult;
	}

	@Async
	public String performLongRunningTask() {
		String jsonData = "{\n"
				+ "  \"Summary\": {\n"
				+ "    \"MISversion\": \"4.14.2.9\",\n"
				+ "    \"SUMversion\": \"4.14.2.9\",\n"
				+ "    \"TaskId\": \"274567\",\n"
				+ "    \"JobId\": \"1777504\",\n"
				+ "    \"hasDynamicAnalysis\": \"false\",\n"
				+ "    \"Subject\": {\n"
				+ "      \"Name\": \"100mb.pdf\",\n"
				+ "      \"Type\": \"application/pdf\",\n"
				+ "      \"FileType\": \"4\"\n"
				+ "    },\n"
				+ "    \"Data\": {\n"
				+ "      \"analysis_seconds\": \"17\",\n"
				+ "      \"sandbox_analysis\": \"0\",\n"
				+ "      \"compiled_with\": \"Not Available\"\n"
				+ "    },\n"
				+ "    \"Selectors\": [\n"
				+ "      {\n"
				+ "        \"Engine\": \"Sandbox\",\n"
				+ "        \"MalwareName\": \"---\",\n"
				+ "        \"Severity\": \"-1\"\n"
				+ "      }\n"
				+ "    ],\n"
				+ "    \"Verdict\": {\n"
				+ "      \"Severity\": \"-1\",\n"
				+ "      \"Description\": \"Pre-Filter heuristic that this file does not contain suspicious content and is considered to be clean\"\n"
				+ "    },\n"
				+ "    \"Process\": [\n"
				+ "      {\n"
				+ "        \"Name\": \"100MB.pdf\",\n"
				+ "        \"Reason\": \"Static Analysis\",\n"
				+ "        \"Severtity\": \"-1\"\n"
				+ "      }\n"
				+ "    ]\n"
				+ "  }\n"
				+ "}\n"
				+ "";
		try {
			// Simulate a long-running task
			TimeUnit.SECONDS.sleep(6);
			
		} catch (InterruptedException e) {
			// Handle interruption if needed
			Thread.currentThread().interrupt();
		}
		return jsonData;
	}
}
