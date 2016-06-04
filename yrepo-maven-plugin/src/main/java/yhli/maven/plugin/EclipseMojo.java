package yhli.maven.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Generate eclipse configs for local use.
 * <p/>
 * 1. Copy dependency jar files to target/repo/lib/
 * <p/>
 * 2. Copy dependency source files to target/repo/lib/sources/
 * <p/>
 * 3. Generate .classpath file to target/repo/
 * <p/>
 */
@Mojo(name = "eclipse")
public class EclipseMojo extends AbstractMojo {

	/**
	 * 输出根目录位置.
	 */
	@Parameter(property = "outputdir", defaultValue = "${project.build.directory}/repo", required = true)
	private File outputDir;

	/**
	 * 是否输出依赖包的源码包.
	 */
	@Parameter(property = "sources", defaultValue = "true")
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
				throw new MojoExecutionException("repo dir is not exist: " + repoDir.getAbsolutePath());
			}

			execute(basedir, outputDir, sources, repoDir);
		} catch (IOException e) {
			throw new MojoExecutionException("process io failed", e);
		} catch (JDOMException e) {
			throw new MojoExecutionException("process xml failed", e);
		}
	}

	public static class ClasspathEntry {
		public String path;
		public String sourcepath;

		public String dstPath;
		public String dstSourcePath;

		public String toString() {
			return "CPE(" + path + ", " + sourcepath + ")";
		}
	}

	public void execute(File basedir, File dstdir, boolean shareSources, File repoDir)
			throws IOException, JDOMException {
		Log log = getLog();
		File f = new File(basedir, ".classpath");
		if (!f.exists() || !f.isFile()) {
			throw new FileNotFoundException(f.getAbsolutePath());
		}

		List<ClasspathEntry> entries = getClasspathEntries(f, "var");
		if (entries.isEmpty()) {
			log.info("no dependencies jars found!");
			return;
		}

		log.info("repo srcdir: " + repoDir.getAbsolutePath());
		log.info("repo dstdir: " + dstdir.getAbsolutePath());

		int libCount = 0;
		int srcCount = 0;

		File libdir = new File(dstdir, "lib");
		if (!libdir.exists()) {
			libdir.mkdirs();
		}

		File sourcedir = new File(dstdir, "lib/sources");
		if (shareSources && !sourcedir.exists()) {
			sourcedir.mkdirs();
		}
		for (ClasspathEntry ce : entries) {
			File src = newRepoFile(repoDir, ce.path);
			File dst = new File(libdir, src.getName());
			FileUtils.copyFileIfModified(src, dst);
			libCount++;
			ce.dstPath = "lib/" + dst.getName();

			if (shareSources && StringUtils.isNotBlank(ce.sourcepath)) {
				src = newRepoFile(repoDir, ce.sourcepath);
				if (src.exists()) {
					dst = new File(sourcedir, src.getName());
					FileUtils.copyFileIfModified(src, dst);
					srcCount++;
					ce.dstSourcePath = "lib/sources/" + dst.getName();
				}
			}
		}
		log.info("Copy " + libCount + " dependency jar files.");
		if (shareSources) {
			log.info("Copy " + srcCount + " dependency source files.");
		}
		File dstFile = new File(dstdir, "lib/sources/conf/.classpath");
		writeClasspathFile(dstFile, f, entries, false);
		File dstFile2 = new File(dstdir, "lib/sources/.classpath");
		writeClasspathFile(dstFile2, f, entries, true);
		log.info("Generate .classpath file ok.");
	}

	private void writeClasspathFile(File dstFile, File srcFile, List<ClasspathEntry> classpaths, boolean hasSourceJar)
			throws IOException, JDOMException {
		Element eClasspath = new Element("classpath");

		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(srcFile);
		Element root = doc.getRootElement();

		List<Element> list2 = root.getChildren();
		for (int i2 = 0; i2 < list2.size(); i2++) {
			Element node2 = list2.get(i2);
			if ("classpathentry".equals(node2.getName()) && "var".equals(node2.getAttributeValue("kind"))) {
				// 后续处理
			} else {
				eClasspath.addContent(node2.clone());
			}
		}

		// 后续处理：classpath
		if (eClasspath != null) {
			for (ClasspathEntry ce : classpaths) {
				Element e = new Element("classpathentry");
				e.setAttribute("kind", "lib");
				e.setAttribute("path", ce.dstPath);
				if (hasSourceJar && ce.dstSourcePath != null) {
					e.setAttribute("sourcepath", ce.dstSourcePath);
				}
				eClasspath.addContent(e);
			}
		}

		// 输出文件
		Document doc2 = new Document(eClasspath);
		XMLOutputter xmlOut = new XMLOutputter();
		Format f = Format.getPrettyFormat();
		f.setEncoding("UTF-8");// default=UTF-8
		xmlOut.setFormat(f);
		if (!dstFile.getParentFile().exists()) {
			dstFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(dstFile);
		xmlOut.output(doc2, fos);
		fos.close();
	}

	private File newRepoFile(File repoDir, String path) {
		if (path.startsWith("M2_REPO")) {
			path = path.substring(path.indexOf('/'));
		}
		return new File(repoDir, path);
	}

	private List<ClasspathEntry> getClasspathEntries(File fClasspath, String type) throws IOException, JDOMException {
		List<ClasspathEntry> result = new ArrayList<ClasspathEntry>();
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(fClasspath);
		Element root = doc.getRootElement();
		List<Element> list = root.getChildren("classpathentry");
		for (Element e : list) {
			if (type.equals(e.getAttributeValue("kind"))) {
				ClasspathEntry ce = new ClasspathEntry();
				ce.path = e.getAttributeValue("path");
				ce.sourcepath = e.getAttributeValue("sourcepath");
				result.add(ce);
			}
		}
		return result;
	}

}
