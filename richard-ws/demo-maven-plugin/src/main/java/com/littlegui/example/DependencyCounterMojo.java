package com.littlegui.example;

import java.util.List;

import org.apache.maven.model.Dependency;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * 
 * @phase process-sources
 */
@Mojo(name = "dependency-counter", defaultPhase = LifecyclePhase.COMPILE)
public class DependencyCounterMojo extends AbstractMojo {

	@Parameter(property = "name")
	private String name;

	@Parameter(property = "scope")
	String scope;

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		List<Dependency> dependencies = project.getDependencies();
		
		long numDependencies = dependencies.stream()
				.filter(d -> (scope == null || scope.isEmpty()) || scope.equals(d.getScope())).count();
		getLog().info("scope: " + scope);
		getLog().info("name: " + name);
		getLog().info("Number of dependencies: " + numDependencies);
		
		long allNumDependencies = dependencies.stream().count();          
	    getLog().info("Number of all dependencies: " + allNumDependencies);
	}
}
