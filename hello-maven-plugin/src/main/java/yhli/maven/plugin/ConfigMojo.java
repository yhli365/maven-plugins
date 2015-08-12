package yhli.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Detect maven settings.
 *
 */
@Mojo(name = "config")
public class ConfigMojo extends AbstractMojo {

	@Parameter(defaultValue = "${basedir}")
	private File basedir;

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project.version}")
	private String version;

	/**
	 * The current Maven project.
	 */
	// @Parameter(defaultValue = "${project}", readonly = true, required = true)
	// private MavenProject project;

	/**
	 * The local repository, from which to delete artifacts.
	 */
	@Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
	private ArtifactRepository localRepository;

	/**
	 * List of Remote Repositories used by the resolver
	 */
	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
	protected List<ArtifactRepository> remoteRepositories;

	public void execute() throws MojoExecutionException {
		getLog().info("basedir = " + basedir.getAbsolutePath());
		getLog().info(
				"project.build.directory = "
						+ outputDirectory.getAbsolutePath());
		getLog().info("project.version = " + version);

		getLog().info("-------------PluginContext");
		@SuppressWarnings({ "unchecked" })
		Map<String, ?> map = this.getPluginContext();
		List<String> keys = new ArrayList<String>();
		keys.addAll(map.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			getLog().info(key + " = " + map.get(key));
		}

		// getLog().info("");
		// getLog().info("-------------Project");
		// getLog().info(
		// "getBasedir() = " + project.getBasedir().getAbsolutePath());
		// getLog().info("getId() = " + project.getId());
		// getLog().info("getGroupId() = " + project.getGroupId());
		// getLog().info("getArtifactId() = " + project.getArtifactId());
		// getLog().info("getVersion() = " + project.getVersion());

		getLog().info("");
		getLog().info("-------------localRepository");
		getLog().info("getBasedir() = " + localRepository.getBasedir());
		getLog().info("getId() = " + localRepository.getId());
		getLog().info("getUrl() = " + localRepository.getUrl());

		for (int i = 0; i < remoteRepositories.size(); i++) {
			ArtifactRepository repo = remoteRepositories.get(i);
			getLog().info("");
			getLog().info("-------------remoteRepositories#" + (i + 1));
			getLog().info("getBasedir() = " + repo.getBasedir());
			getLog().info("getId() = " + repo.getId());
			getLog().info("getUrl() = " + repo.getUrl());
		}
	}

}
