package org.openmastery.tags

import java.util.regex.Matcher
import java.util.regex.Pattern


class TagsUtil {

	public static Set<String> extractUniqueHashTags(String commentWithTags) {
		Set<String> hashtags = new HashSet<String>();

		if (commentWithTags != null) {
			Pattern hashTagPattern = Pattern.compile("(#\\w+)");
			Matcher matcher = hashTagPattern.matcher(commentWithTags);
			while (matcher.find()) {
				hashtags.add(matcher.group(1));
			}
		}

		return hashtags;
	}

}
