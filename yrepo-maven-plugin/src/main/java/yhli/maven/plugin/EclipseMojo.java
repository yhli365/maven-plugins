package yhli.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import yhli.tool.ide.EclipseTool;

/**
 * Generate eclipse configs.
 * <p/>
 * 1. Copy dependency jar files to target/repo/lib
 * <p/>
 * 2. Copy dependency source files to target/repo/lib/sources
 * <p/>
 * 3. Generate .classpath file to target/repo
 * <p/>
 */
@Mojo(name = "eclipse")
public class EclipseMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.directory}/repo", required = true)
	private File outputDir;

	@Parameter(defaultValue = "true")
	private boolean sources;

	@Parameter(defaultValue = "${basedir}", readonly = true, required = true)
	private File basedir;

	@Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
	private ArtifactRepository localRepository;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			File repoDir = new File(localRepository.getBasedir());
			if (!repoDir.exists()) {
				throw new MojoExecutionException("repo dir is not exist: "
						+ repoDir.getAbsolutePath());
			}

			EclipseTool tool = new EclipseTool();
			tool.setLog(new MavenToolLog(getLog()));
			tool.share(basedir, outputDir, sources, repoDir);
		} catch (IOException e) {
			throw new MojoExecutionException("io failed", e);
		}
	}

}