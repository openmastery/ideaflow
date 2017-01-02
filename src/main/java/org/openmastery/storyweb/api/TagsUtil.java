/*
 * Copyright 2017 New Iron Group, Inc.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openmastery.storyweb.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TagsUtil {

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
