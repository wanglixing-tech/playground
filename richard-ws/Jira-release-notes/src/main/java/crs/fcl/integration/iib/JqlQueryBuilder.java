package crs.fcl.integration.iib;

import java.util.List;

public interface JqlQueryBuilder {
	String build();

	JqlQueryBuilder components(String components);

	JqlQueryBuilder components(List<String> components);

	JqlQueryBuilder filter(String filter);

	JqlQueryBuilder fixVersion(String fixVersion);

	JqlQueryBuilder fixVersionIds(String fixVersionIds);

	JqlQueryBuilder fixVersionIds(List<String> fixVersionIds);

	JqlQueryBuilder priorityIds(String priorityIds);

	JqlQueryBuilder priorityIds(List<String> priorityIds);

	JqlQueryBuilder project(String project);

	JqlQueryBuilder resolutionIds(String resolutionIds);

	JqlQueryBuilder resolutionIds(List<String> resolutionIds);

	JqlQueryBuilder sortColumnNames(String sortColumnNames);

	JqlQueryBuilder statusIds(String statusIds);

	JqlQueryBuilder statusIds(List<String> statusIds);

	JqlQueryBuilder typeIds(String typeIds);

	JqlQueryBuilder typeIds(List<String> typeIds);
}
