package com.bytebreeze.quickdrop.util;

import com.bytebreeze.quickdrop.dto.paymentapiresponse.SSLCommerzValidatorResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SSLCommerzUtil {

	public static SSLCommerzValidatorResponse extractValidatorResponse(String response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SSLCommerzValidatorResponse sslValidatorResponse =
				mapper.readValue(response, SSLCommerzValidatorResponse.class);
		return sslValidatorResponse;
	}

	public static String getByOpeningJavaUrlConnection(String stringUrl) throws IOException {
		String output = "";
		URL url = new URL(stringUrl);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String outputLine;
		while ((outputLine = br.readLine()) != null) {
			output = output + outputLine;
		}
		br.close();
		return output;
	}
}
