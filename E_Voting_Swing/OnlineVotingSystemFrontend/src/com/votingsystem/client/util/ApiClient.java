// src/com/votingsystem/client/util/ApiClient.java
package com.votingsystem.client.util;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ApiClient {

    // IMPORTANT: Replace with the actual URL of your Tomcat server and application context
    // Example: http://localhost:8080/OnlineVotingSystemBackend
    private static final String BASE_URL = "http://localhost:9090/OnlineVotingSystemBackend";

    // Session Management: Store JSESSIONID to maintain session across requests
    private static String jSessionId = null;

    // --- Public Methods for API Interaction ---

    /**
     * Performs a GET request to the specified endpoint with query parameters.
     * @param endpoint The API endpoint (e.g., "/login", "/admin/election").
     * @param params A map of query parameters.
     * @return A JSONObject representing the API response, or null if an error occurs.
     */
    public static JSONObject get(String endpoint, Map<String, String> params) {
        StringBuilder urlString = new StringBuilder(BASE_URL + endpoint);
        if (params != null && !params.isEmpty()) {
            urlString.append("?");
            params.forEach((key, value) -> {
                try {
                    urlString.append(key).append("=").append(java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.toString())).append("&");
                } catch (Exception e) {
                    System.err.println("Error encoding param: " + key + "=" + value);
                }
            });
            urlString.setLength(urlString.length() - 1); // Remove trailing '&'
        }
        return makeRequest(urlString.toString(), "GET", null);
    }

    /**
     * Performs a POST request to the specified endpoint with form data.
     * @param endpoint The API endpoint (e.g., "/register", "/login").
     * @param formData A map of form data (key-value pairs) to be sent as application/x-www-form-urlencoded.
     * @return A JSONObject representing the API response, or null if an error occurs.
     */
    public static JSONObject post(String endpoint, Map<String, String> formData) {
        StringBuilder postData = new StringBuilder();
        if (formData != null) {
            formData.forEach((key, value) -> {
                if (postData.length() != 0) postData.append('&');
                try {
                    postData.append(java.net.URLEncoder.encode(key, StandardCharsets.UTF_8.toString()));
                    postData.append('=');
                    postData.append(java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
                } catch (Exception e) {
                    System.err.println("Error encoding form data: " + key + "=" + value);
                }
            });
        }
        return makeRequest(BASE_URL + endpoint, "POST", postData.toString());
    }

    // --- Private Helper Method for HTTP Connection ---

    private static JSONObject makeRequest(String urlString, String method, String postData) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(method.equals("POST")); // Only send output for POST

            // Set content type for POST requests
            if (method.equals("POST")) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }

            // Include JSESSIONID for session management
            if (jSessionId != null) {
                conn.setRequestProperty("Cookie", "JSESSIONID=" + jSessionId);
            }

            // Send POST data if applicable
            if (method.equals("POST") && postData != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = postData.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Get response
            int responseCode = conn.getResponseCode();
            BufferedReader in;
            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Extract JSESSIONID from response headers if present (for subsequent requests)
            String cookieHeader = conn.getHeaderField("Set-Cookie");
            if (cookieHeader != null) {
                String[] cookies = cookieHeader.split(";");
                for (String cookie : cookies) {
                    if (cookie.trim().startsWith("JSESSIONID=")) {
                        jSessionId = cookie.trim().substring("JSESSIONID=".length());
                        break;
                    }
                }
            }
            System.out.println("Raw API response: " + response.toString());
            System.out.println("Raw response from server: " + response.toString());

            // Parse JSON response
            return new JSONObject(response.toString());

        } catch (Exception e) {
            System.err.println("API Request Error: " + e.getMessage());
            e.printStackTrace();
            // Return a JSON indicating error for UI to handle
            JSONObject errorJson = new JSONObject();
            errorJson.put("success", false);
            errorJson.put("message", "Network or server error: " + e.getMessage());
            return errorJson;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Clears the stored JSESSIONID, effectively logging out the client from the session.
     */
    public static void clearSession() {
        jSessionId = null;
    }
}