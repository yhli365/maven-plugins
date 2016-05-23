package yhli.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

/**
 * Clean output directory.
 */
@Mojo(name = "clean")
public class CleanMojo extends AbstractMojo {

	/**
	 * 输出目录位置.
	 */
	@Parameter(property = "outputdir", defaultValue = "${project.build.directory}/repo", required = true)
	private File outputDir;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(outputDir);
			getLog().info("clean dir ok: " + outputDir);
		} catch (IOException e) {
			throw new MojoExecutionException("process io failed", e);
		}
	}

}
