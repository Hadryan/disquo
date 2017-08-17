package net.anthavio.disquo.api.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.anthavio.disquo.api.response.DisqusUser.Avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * 
 * @author martin.vanek
 *
 */
public class DisqusForumDeserializer extends JsonDeserializer<DisqusForum> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public DisqusForum deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {

		DisqusForum bean = new DisqusForum();

		while (jp.nextToken() != JsonToken.END_OBJECT) {
			String field = jp.getCurrentName();
			jp.nextToken();
			if ("id".equals(field)) {
				bean.setId(jp.getValueAsString());
			} else if ("name".equals(field)) {
				bean.setName(jp.getValueAsString());
			} else if ("founder".equals(field)) {
				bean.setFounder(DeserializationUtils.getLong(jp));
			} else if ("settings".equals(field)) {
				bean.setSettings(jp.readValueAs(DisqusForumSettings.class));
			} else if ("url".equals(field)) {
				//This is only reason of this class. On detail, array is returned. When joined as related, only single element is sent
				List<String> values = new ArrayList<String>();
				if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
					while (jp.nextToken() != JsonToken.END_ARRAY) {
						String item = jp.getValueAsString();
						values.add(item);
					}
				} else {
					values.add(jp.getValueAsString());
				}
				bean.setUrl(values);
			} else if ("favicon".equals(field)) {
				bean.setFavicon(jp.readValueAs(DisqusImage.class));
			} else if ("language".equals(field)) {
				bean.setLanguage(jp.getValueAsString());
			} else if ("author".equals(field)) {
				bean.setAuthor(jp.readValueAs(DisqusUser.class));
			} else if ("avatar".equals(field)) {
				bean.setAvatar(jp.readValueAs(Avatar.class));
			} else if("twitterName".equals(field)){
				bean.setTwitterName(jp.getValueAsString());
			} else if("commentsLinkOne".equals(field)){
				bean.setCommentsLinkOne(jp.getValueAsString());
			} else if("commentsLinkZero".equals(field)){
				bean.setCommentsLinkZero(jp.getValueAsString());
			} else if("commentPolicyText".equals(field)){
				bean.setCommentPolicyText(jp.getValueAsString());
			} else if("createdAt".equals(field)){
				bean.setCreatedAt(ctxt.parseDate(jp.getValueAsString()));
			} else if("sort".equals(field)) {
				bean.setSort(jp.getIntValue());
			} else if("guidelines".equals(field)){
				bean.setGuidelines(jp.getValueAsString());
			} else if("raw_guidelines".equals(field)){
				bean.setRaw_guidelines(jp.getValueAsString());
			} else if("description".equals(field)){
				bean.setDescription(jp.getValueAsString());
			} else if("raw_description".equals(field)){
				bean.setRaw_description(jp.getValueAsString());
			} else {
				//throw new DisqusException("Unexpected element '" + field + "'");
				logger.debug("Unexpected element '" + field + "'");
			}
		}
		return bean;
	}
}
