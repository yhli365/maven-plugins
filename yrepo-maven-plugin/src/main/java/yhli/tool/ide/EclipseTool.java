package yhli.tool.ide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Element;

import yhli.tool.AbstractTool;
import yhli.tool.XmlUtils;

/**
 * Eclipse项目管理工具.
 * <p>
 * 1.分析Maven等工具生成的.classpath文件.<br/>
 * 2.从本地仓库中自动下载jar及其源码到指定目录.<br/>
 * 3.据此生成新.classpath以方便项目共享.<br/>
 * </p>
 * 
 * @author yhli
 *
 */
public class EclipseTool extends AbstractTool {

	public static class ClasspathEntry {
		public String path;
		public String sourcepath;

		public String toString() {
			return "CPE(" + path + ", " + sourcepath + ")";
		}
	}

	public void share(File srcdir, File dstdir, boolean shareSources,
			File repoDir) throws IOException {
		File f = new File(srcdir, ".classpath");
		if (!f.exists() || !f.isFile()) {
			throw new FileNotFoundException(f.getAbsolutePath());
		}

		List<ClasspathEntry> entries = getClasspathEntries(f, "var");
		if (entries.isEmpty()) {
			log.info("no dependencies jars found!");
			return;
		}

		log.info("output to " + dstdir.getAbsolutePath());

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

			if (shareSources) {
				src = newRepoFile(repoDir, ce.sourcepath);
				if (src.exists()) {
					dst = new File(sourcedir, src.getName());
					FileUtils.copyFileIfModified(src, dst);
					srcCount++;
				}
			}
		}
		log.info("copy " + libCount + " jar files");
		if (shareSources) {
			log.info("copy " + srcCount + " source files");
		}
	}

	private File newRepoFile(File repoDir, String path) {
		if (path.startsWith("M2_REPO")) {
			path = path.substring(path.indexOf('/'));
		}
		return new File(repoDir, path);
	}

	private List<ClasspathEntry> getClasspathEntries(File fClasspath,
			String type) throws IOException {
		List<ClasspathEntry> result = new ArrayList<ClasspathEntry>();
		Element root = XmlUtils.getRootElement(fClasspath);
		List<Element> list = XmlUtils.getElementsByTagName(root,
				"classpathentry");
		for (Element e : list) {
			if (XmlUtils.getAttribute(e, "kind").equals(type)) {
				ClasspathEntry ce = new ClasspathEntry();
				ce.path = XmlUtils.getAttribute(e, "path");
				ce.sourcepath = XmlUtils.getAttribute(e, "sourcepath");
				result.add(ce);
			}
		}
		return result;
	}

}
