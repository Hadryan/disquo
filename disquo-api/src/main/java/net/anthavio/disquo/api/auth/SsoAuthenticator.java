package net.anthavio.disquo.api.auth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.anthavio.httl.util.Cutils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * https://help.disqus.com/customer/portal/articles/236206-integrating-single-sign-on
 * 
 * @author martin.vanek
 *
 */
public class SsoAuthenticator {

	private static final ObjectMapper mapper = new ObjectMapper();

	private final String secretKey;

	public SsoAuthenticator(String secretKey) {
		if (Cutils.isBlank(secretKey)) {
			throw new IllegalArgumentException("secretKey is blank '" + secretKey + "'");
		}
		this.secretKey = secretKey;
	}

	public String remote_auth_s3(SsoAuthData authData) {
		return remote_auth_s3(authData, this.secretKey);
	}

	public static SsoAuthData decode_remote_auth(String remote_auth, String api_secret) {
		String[] split = remote_auth.split(" ");
		if (split.length != 3) {
			throw new IllegalArgumentException("Not a remote token. Must have 3 space character separated parts");
		}
		String message = split[0];
		String sigRecieved = split[1];
		long timestamp = Long.parseLong(split[2]); //yet we can check for token expiration
		String sigComputed = HmacSha1Hex(api_secret, message + " " + timestamp);
		if (!sigRecieved.equals(sigComputed)) {
			throw new IllegalArgumentException("Signatures differs " + sigRecieved + " vs " + sigComputed);
		}
		return DeBase64Json(message);
	}

	public static String remote_auth_s3(SsoAuthData auth, String api_secret) {
		String message = JsonBase64(auth);
		long timestamp = System.currentTimeMillis() / 1000;
		String sig = HmacSha1Hex(api_secret, message + " " + timestamp);
		return message + " " + sig + " " + timestamp;
	}

	private static String HmacSha1Hex(String key, String text) {
		byte[] keyBytes = key.getBytes();
		byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));
		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			mac.init(secretKey);
			byte[] rawHmac = mac.doFinal(textBytes);
			return new String(Hex.encodeHex(rawHmac));
		} catch (GeneralSecurityException gsx) {
			throw new IllegalStateException(gsx);
		}
	}

	private static SsoAuthData DeBase64Json(String message) {
		Charset charset = Charset.forName("UTF-8");
		byte[] decodeBase64 = Base64.decodeBase64(message.getBytes(charset));
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(decodeBase64), charset);
		try {
			Map<String, String> map = mapper.readValue(reader, Map.class);
			return new SsoAuthData(map.get("id"), map.get("username"), map.get("email"));
		} catch (IOException iox) {
			throw new IllegalStateException(iox);
		}
	}

	private static String JsonBase64(SsoAuthData auth) {
		String userId = auth.getUserId();
		if (Cutils.isBlank(userId)) {
			throw new IllegalArgumentException("userId is blank '" + userId + "'");
		}
		String fullName = auth.getFullName();
		if (Cutils.isBlank(fullName)) {
			throw new IllegalArgumentException("fullName is blank '" + fullName + "'");
		}
		String email = auth.getEmail();
		if (Cutils.isBlank(email)) {
			throw new IllegalArgumentException("email is blank '" + email + "'");
		}

		Map<String, String> data = new HashMap<String, String>();
		data.put("id", userId);
		data.put("username", fullName);
		data.put("email", email);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(baos, Charset.forName("UTF-8"));
		try {
			mapper.writeValue(writer, data);
		} catch (IOException iox) {
			throw new IllegalStateException(iox);
		}
		return new String(Base64.encodeBase64(baos.toByteArray(), false));
	}
}
