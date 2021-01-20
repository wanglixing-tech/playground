package crs.fcl.integration.iib;

public class UrlBuilder {
	private static final String AMPERSAND = "&";

	private static final String QUESTION_MARK = "?";

	private StringBuilder query = new StringBuilder();

	public UrlBuilder(String url, String context) {
		query.append(url).append("/").append(context);
	}

	public UrlBuilder addParameter(String key, String value) {
		if (key != null && value != null) {
			if (query.toString().contains(QUESTION_MARK)) {
				query.append(AMPERSAND);
			} else {
				query.append(QUESTION_MARK);
			}
			query.append(key).append("=").append(value);
		}
		return this;
	}

	public UrlBuilder addParameter(String key, int value) {
		addParameter(key, String.valueOf(value));
		return this;
	}

	public String build() {
		return query.toString();
	}
}
