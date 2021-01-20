package crs.fcl.integration.iib;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class JqlQueryBuilderImpl implements JqlQueryBuilder {
	private String filter = "";

	private boolean urlEncode = true;

	private StringBuilder orderBy = new StringBuilder();

	private StringBuilder query = new StringBuilder();

	public JqlQueryBuilderImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String build() {
		try {
			String jqlQuery;
			// If the user has defined a filter - use that
			if ((this.filter != null) && (this.filter.length() > 0)) {
				jqlQuery = filter;
			} else {
				jqlQuery = query.toString() + orderBy.toString();
			}

			if (urlEncode) {
				String encodedQuery = URLEncoder.encode(jqlQuery, "UTF-8");
				return encodedQuery;
			} else {
				return jqlQuery;
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unable to encode JQL query with UTF-8");
			throw new RuntimeException(e);
		}
	}

	public JqlQueryBuilder components(String components) {
		addCommaSeparatedValues("component", components);
		return this;
	}

	public JqlQueryBuilder components(List<String> components) {
		addValues("component", components);
		return this;
	}

	public JqlQueryBuilder filter(String filter) {
		this.filter = filter;
		return this;
	}

	/**
	 * When both {@link #fixVersion(String)} and {@link #fixVersionIds(String)} are
	 * used then you will probably end up with a JQL query that is valid, but
	 * returns nothing. Unless they both only reference the same fixVersion
	 *
	 * @param fixVersion
	 *            a single fix version
	 * @return the builder.
	 */
	public JqlQueryBuilder fixVersion(String fixVersion) {
		addSingleValue("fixVersion", fixVersion);
		return this;
	}

	/**
	 * When both {@link #fixVersion(String)} and {@link #fixVersionIds(String)} are
	 * used then you will probably end up with a JQL query that is valid, but
	 * returns nothing. Unless they both only reference the same fixVersion
	 *
	 * @param fixVersionIds
	 *            a comma-separated list of version ids.
	 * @return the builder.
	 */
	public JqlQueryBuilder fixVersionIds(String fixVersionIds) {
		addCommaSeparatedValues("fixVersion", fixVersionIds);
		return this;
	}

	/**
	 * Add a sequence of version IDs already in a list.
	 * 
	 * @param fixVersionIds
	 *            the version ids.
	 * @return the builder.
	 */
	public JqlQueryBuilder fixVersionIds(List<String> fixVersionIds) {
		addValues("fixVersion", fixVersionIds);
		return this;
	}

	public JqlQueryBuilder priorityIds(String priorityIds) {
		addCommaSeparatedValues("priority", priorityIds);
		return this;
	}

	public JqlQueryBuilder priorityIds(List<String> priorityIds) {
		addValues("priority", priorityIds);
		return this;
	}

	public JqlQueryBuilder project(String project) {
		addSingleValue("project", project);
		return this;
	}

	public JqlQueryBuilder resolutionIds(String resolutionIds) {
		addCommaSeparatedValues("resolution", resolutionIds);
		return this;
	}

	public JqlQueryBuilder resolutionIds(List<String> resolutionIds) {
		addValues("resolution", resolutionIds);
		return this;
	}

	public JqlQueryBuilder sortColumnNames(String sortColumnNames) {
		if (sortColumnNames != null) {
			orderBy.append(" ORDER BY ");

			String[] sortColumnNamesArray = sortColumnNames.split(",");

			for (int i = 0; i < sortColumnNamesArray.length - 1; i++) {
				addSingleSortColumn(sortColumnNamesArray[i]);
				orderBy.append(", ");
			}
			addSingleSortColumn(sortColumnNamesArray[sortColumnNamesArray.length - 1]);
		}
		return this;
	}

	public JqlQueryBuilder statusIds(String statusIds) {
		addCommaSeparatedValues("status", statusIds);
		return this;
	}

	public JqlQueryBuilder statusIds(List<String> statusIds) {
		addValues("status", statusIds);
		return this;
	}

	public JqlQueryBuilder typeIds(String typeIds) {
		addCommaSeparatedValues("type", typeIds);
		return this;
	}

	public JqlQueryBuilder typeIds(List<String> typeIds) {
		addValues("type", typeIds);
		return this;
	}

	public JqlQueryBuilder urlEncode(boolean doEncoding) {
		urlEncode = doEncoding;
		return this;
	}

	public boolean urlEncode() {
		return urlEncode;
	}

	/* --------------------------------------------------------------------- */
	/* Private methods */
	/* --------------------------------------------------------------------- */

	private void addCommaSeparatedValues(String key, String values) {
		if (values != null) {
			if (query.length() > 0) {
				query.append(" AND ");
			}

			query.append(key).append(" in (");

			String[] valuesArr = values.split(",");

			for (int i = 0; i < (valuesArr.length - 1); i++) {
				trimAndQuoteValue(valuesArr[i]);
				query.append(", ");
			}
			trimAndQuoteValue(valuesArr[valuesArr.length - 1]);
			query.append(")");
		}
	}

	private void addValues(String key, List<String> values) {
		if (values != null && values.size() > 0) {
			if (query.length() > 0) {
				query.append(" AND ");
			}

			query.append(key).append(" in (");

			for (int i = 0; i < (values.size() - 1); i++) {
				trimAndQuoteValue(values.get(i));
				query.append(", ");
			}
			trimAndQuoteValue(values.get(values.size() - 1));
			query.append(")");
		}
	}

	private void addSingleSortColumn(String name) {
		boolean descending = false;
		name = name.trim().toLowerCase(Locale.ENGLISH);
		if (name.endsWith("desc")) {
			descending = true;
			name = name.substring(0, name.length() - 4).trim();
		} else if (name.endsWith("asc")) {
			descending = false;
			name = name.substring(0, name.length() - 3).trim();
		}
		// Strip any spaces from the column name, or it will trip up JIRA's JQL parser
		name = name.replaceAll(" ", "");
		orderBy.append(name);
		orderBy.append(descending ? " DESC" : " ASC");
	}

	private void addSingleValue(String key, String value) {
		if (value != null) {
			if (query.length() > 0) {
				query.append(" AND ");
			}
			query.append(key).append(" = ");
			trimAndQuoteValue(value);
		}
	}

	private void trimAndQuoteValue(String value) {
		String trimmedValue = value.trim();
		if (trimmedValue.contains(" ") || trimmedValue.contains(".")) {
			query.append("\"").append(trimmedValue).append("\"");
		} else {
			query.append(trimmedValue);
		}
	}
}
