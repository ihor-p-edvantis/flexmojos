/**
 * Flexmojos is a set of maven goals to allow maven users to compile, optimize and test Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008-2012  Marvin Froeder <marvin@flexmojos.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.rvin.mojo.flexmojo;

import info.rvin.flexmojos.utilities.MavenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;

/**
 * 
 * Encapsulate the access to Maven API. Some times just to hide Java 5 warnings
 * 
 */
public abstract class AbstractIrvinMojo extends AbstractMojo {

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter expression="${project.build}"
	 * @required
	 * @readonly
	 */
	protected Build build;

	/**
	 * @component
	 */
	protected MavenProjectHelper projectHelper;

	/**
	 * @component
	 */
	protected ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	protected ArtifactResolver resolver;

	/**
	 * @component
	 */
	protected ArtifactMetadataSource artifactMetadataSource;

	/**
	 * @component
	 */
	protected MavenProjectBuilder mavenProjectBuilder;

	/**
	 * Local repository to be used by the plugin to resolve dependencies.
	 * 
	 * @parameter expression="${localRepository}"
	 */
	protected ArtifactRepository localRepository;

	/**
	 * List of remote repositories to be used by the plugin to resolve dependencies.
	 * 
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 */
	@SuppressWarnings("unchecked")
	protected List remoteRepositories;

	/**
	 * Construct Mojo instance
	 */
	public AbstractIrvinMojo() {
		super();
	}

	// dependency artifactes
	private Set<Artifact> dependencyArtifacts;

	/**
	 * Returns Set of dependency artifacts which are resolved for the project.
	 * @return Set of dependency artifacts.
	 * @throws MojoExecutionException
	 */
	protected Set<Artifact> getDependencyArtifacts()
			throws MojoExecutionException {
		if (dependencyArtifacts == null) {
			dependencyArtifacts = MavenUtils.getDependencyArtifacts(project,
					resolver, localRepository, remoteRepositories,
					artifactMetadataSource);
		}
		return dependencyArtifacts;
	}

	/**
	 * Get dependency artifacts for given scope
	 * @param scope for which to get artifacts
	 * @return List of artifacts
	 * @throws MojoExecutionException
	 */
	protected List<Artifact> getDependencyArtifacts(String...scopes)
			throws MojoExecutionException {
		if (scopes == null)
			return null;
		
		if(scopes.length == 0) {
			return new ArrayList<Artifact>();
		}
		
		List<String> scopesList = Arrays.asList(scopes);

		List<Artifact> artifacts = new ArrayList<Artifact>();
		for (Artifact artifact : getDependencyArtifacts()) {
			if ("swc".equals(artifact.getType())
					&& scopesList.contains(artifact.getScope())) {
				artifacts.add(artifact);
			}
		}
		return artifacts;
	}

	/**
	 * Executes plugin
	 */
	public void execute() throws MojoExecutionException,
			MojoFailureException {
		setUp();
		run();
		tearDown();
	}

	/**
	 * Perform setup before plugin is run.
	 * 
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected abstract void setUp() throws MojoExecutionException,
			MojoFailureException;

	/**
	 * Perform plugin functionality
	 * 
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected abstract void run() throws MojoExecutionException,
			MojoFailureException;

	/**
	 * Perform (cleanup) actions after plugin has run
	 * 
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	protected abstract void tearDown() throws MojoExecutionException,
			MojoFailureException;

}