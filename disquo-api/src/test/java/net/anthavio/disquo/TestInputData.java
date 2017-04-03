package net.anthavio.disquo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import net.anthavio.disquo.api.DisqusApplicationKeys;
import net.anthavio.disquo.api.auth.SsoAuthData;
import net.anthavio.disquo.api.response.DisqusUser;

public class TestInputData {

	private final Properties properties;

	private final String url;

	private final String publicKey;

	private final String secretKey;

	private final String accessToken;

	private final long longThreadId;

	private final long threadId;

	private final String forum;

	private final DisqusUser admin;

	private final DisqusUser ssoUser1;

	private final DisqusUser ssoUser2;

	public static TestInputData load(String resource) {
		//Try to lookup system property with same value as default
		resource = System.getProperty(resource, resource);
		try {
			InputStream stream;
			File file = new File(resource); //cloudbees jenkins - /private/...
			if (file.exists()) {
				stream = new FileInputStream(file); //dev machine - /src/test/resources
			} else {
				ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				stream = contextClassLoader.getResourceAsStream(resource);
			}
			if (stream == null) {
				throw new IllegalArgumentException("Properties not found " + resource);
			}
			Properties props = new Properties();
			props.load(stream);
			stream.close();
			return new TestInputData(props);
		} catch (IOException iox) {
			throw new IllegalArgumentException("Failed to load properties " + resource, iox);
		}

	}

	public TestInputData(Properties properties) {
		if (properties == null) {
			throw new NullArgumentException("properties");
		}
		this.properties = properties;

		this.url = pStr("disqus_url");
		this.publicKey = pStr("api_key");
		this.secretKey = pStr("api_secret");
		this.accessToken = pStr("access_token");

		this.forum = pStr("forum_id");
		this.threadId = pInt("thread_id");
		this.longThreadId = pInt("long_thread_id");
		
		this.admin = null;
		this.ssoUser1 = null;
		this.ssoUser2 = null; 
/*
		this.admin = new DisqusUser();
		this.admin.setId(pLong("admin_id"));
		this.admin.setName(pStr("admin_username"));
		this.admin.setUsername(pStr("admin_username"));
		this.admin.setEmail(pStr("admin_email"));

		this.ssoUser1 = new DisqusUser();
		//this.ssoUser1.setId(pInt("sso1_user_id")); //genarated by Disqus
		this.ssoUser1.setName(pStr("sso1_user_remote_name"));
		this.ssoUser1.setUsername(pStr("sso1_user_name")); //genarated by Disqus
		this.ssoUser1.setEmail(pStr("sso1_user_email"));
		this.ssoUser1.setRemote(new Remote(pStr("sso_domain"), pStr("sso1_user_remote_id")));
		//sso1_user_remote_name	- try to use sso1_user_name

		this.ssoUser2 = new DisqusUser();
		//this.ssoUser2.setId(pInt("sso2_user_id")); //genarated by Disqus
		this.ssoUser2.setName(pStr("sso2_user_remote_name"));
		this.ssoUser2.setUsername(pStr("sso2_user_name")); //genarated by Disqus
		this.ssoUser2.setEmail(pStr("sso2_user_email"));
		this.ssoUser2.setRemote(new Remote(pStr("sso_domain"), pStr("sso2_user_remote_id")));
*/
	}

	public String pStr(String name) {
		String value = this.properties.getProperty(name);
		if (StringUtils.isBlank(value)) {
			throw new IllegalStateException("Property named '" + name + "' is null or blank");
		}
		return value;
	}

	public int pInt(String name) {
		String value = pStr(name);
		return Integer.parseInt(value);
	}

	public long pLong(String name) {
		String value = pStr(name);
		return Long.parseLong(value);
	}

	public String getUrl() {
		return this.url;
	}

	public DisqusApplicationKeys getApplicationKeys() {
		return new DisqusApplicationKeys(this.publicKey, this.secretKey, this.accessToken);
	}

	public String getForum() {
		return this.forum;
	}

	public String getPublicKey() {
		return this.publicKey;
	}

	public String getSecretKey() {
		return this.secretKey;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public long getLongThreadId() {
		return this.longThreadId;
	}

	public long getThreadId() {
		return threadId;
	}

	public DisqusUser getSsoUser1() {
		return this.ssoUser1;
	}

	public DisqusUser getSsoUser2() {
		return this.ssoUser2;
	}

	public DisqusUser getAdmin() {
		return this.admin;
	}

	public SsoAuthData getSsoAuthData1() {
		return getSsoAuthData(this.ssoUser1);
	}

	public SsoAuthData getSsoAuthData2() {
		return getSsoAuthData(this.ssoUser2);
	}

	private SsoAuthData getSsoAuthData(DisqusUser user) {
		return new SsoAuthData(user.getRemote().getIdentifier(), user.getName(), user.getEmail());
	}
}
