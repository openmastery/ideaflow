package org.openmastery.storyweb.core


class SearchUtils {

	static String createSearchPattern(List<String> tags) {
		List<String> prefixedHashtags = tags.collect {
			if (it.startsWith('#')) {
				return it.toLowerCase()
			} else {
				return "#" + it.toLowerCase()
			}
		}
		return "%(" + prefixedHashtags.join("|") + ")%";
	}
}
