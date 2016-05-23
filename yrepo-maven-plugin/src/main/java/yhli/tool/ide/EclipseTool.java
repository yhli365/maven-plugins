package yhli.tool.ide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import yhli.tool.AbstractTool;

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

		public String dstPath;
		public String dstSourcePath;

		public String toString() {
			return "CPE(" + path + ", " + sourcepath + ")";
		}
	}

	public void share(File srcdir, File dstdir, boolean shareSources, File repoDir) throws IOException, JDOMException {
		File f = new File(srcdir, ".classpath");
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

			if (shareSources) {
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
		File dstFile = new File(dstdir, ".classpath");
		writeClasspathFile(dstFile, f, entries);
		log.info("Generate .classpath file ok.");
	}

	private void writeClasspathFile(File dstFile, File srcFile, List<ClasspathEntry> classpaths)
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
				if (ce.dstSourcePath != null) {
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
