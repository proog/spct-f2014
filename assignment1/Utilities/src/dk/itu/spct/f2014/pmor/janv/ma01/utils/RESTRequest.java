package dk.itu.spct.f2014.pmor.janv.ma01.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Class used to perform {@code HTTP GET} requests to a {@code REST} web service
 * that provides a {@code JSON} response.
 * 
 * @author Janus Varmarken
 * 
 */
public class RESTRequest {

	/**
	 * The base {@code URL} of the {@code REST} web service that this request is
	 * aimed at.
	 */
	private final String restBaseURL;

	/**
	 * Create a new {@code RESTRequest} that uses {@code restBaseURL} for its
	 * base URL. This base URL can be augmented with an element ID when the
	 * request is fired using {@link #performRequest(String)}.
	 * 
	 * @param restBaseURL
	 *            The base URL for this REST request.
	 * @throws NullPointerException
	 *             if {@code restBaseURL} is {@code null}.
	 */
	public RESTRequest(String restBaseURL) {
		this.restBaseURL = Objects.requireNonNull(restBaseURL);
	}

	/**
	 * Performs an HTTP GET request to a {@link URL} created by combining the
	 * {@link #restBaseURL} followed by the {@code elementId}. The response is
	 * returned as a {@link String}.
	 * 
	 * @param elementId
	 *            The ID used for the REST query (specifies the element(s) to
	 *            look up). This is relative to the {@link #restBaseURL}.
	 * @return The HTTP response body or {@code null} if the response
	 *         code is not {@code 200}.
	 * @throws IOException
	 *             if an {@link HttpURLConnection} cannot be established to the
	 *             URL that is composed of the {@link #restBaseURL} and the
	 *             {@code elementId}. Also thrown if the connection is
	 *             established but reading from it fails.
	 * @throws NullPointerException
	 *             if {@code elementId} is {@code null}.
	 */
	public String performRequest(String elementId) throws IOException {
		Objects.requireNonNull(elementId);
		// Build full REST URL
		String urlStr = this.restBaseURL + elementId;
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()))) {
			if (conn.getResponseCode() != 200) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}
	}
}
