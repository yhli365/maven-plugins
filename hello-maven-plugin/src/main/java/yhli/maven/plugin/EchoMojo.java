package yhli.maven.plugin;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Echo parameters to the user.
 *
 */
@Mojo(name = "echo")
public class EchoMojo extends AbstractMojo {

	/**
	 * The greeting to display.
	 */
	@Parameter(property = "greeting", defaultValue = "Hello World!")
	private String greeting;

	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	// -----Parameter Types With One Value------------
	/**
	 * Boolean: This includes variables typed boolean and Boolean.
	 */
	@Parameter
	private boolean myBoolean;

	/**
	 * Fixed-Point Numbers: This includes variables typed byte, Byte, int,
	 * Integer, long, Long, short, and Short.
	 */
	@Parameter
	private Integer myInteger;

	/**
	 * Floating-Point Numbers: This includes variables typed double, Double,
	 * float, and Float.
	 */
	@Parameter
	private Double myDouble;

	/**
	 * Dates: This includes variables typed Date. date formats:
	 * "yyyy-MM-dd HH:mm:ss.S a" (a sample date is "2005-10-06 2:22:55.1 PM") or
	 * "yyyy-MM-dd HH:mm:ssa" (a sample date is "2005-10-06 2:22:55PM").
	 */
	@Parameter
	private Date myDate;

	/**
	 * Files and Directories: This includes variables typed File.
	 */
	@Parameter
	private File myFile;

	/**
	 * URLs: This includes variables typed URL.
	 * (scheme://host:port/path/to/file)
	 */
	@Parameter
	private URL myURL;

	/**
	 * Plain Text: This includes variables typed char, Character, StringBuffer,
	 * and String.
	 */
	@Parameter
	private StringBuffer myText;

	public enum Color {
		GREEN, RED, BLUE
	}

	/**
	 * Enums: Enumeration type parameters can also be used. First you need to
	 * define your enumeration type and afterwards you can use the enumeration
	 * type in the parameter definition.
	 */
	@Parameter(defaultValue = "GREEN")
	private Color myColor;

	// -----Parameter Types With Multiple Values------------
	/**
	 * Arrays: Array type parameters are configured by specifying the parameter
	 * multiple times.
	 */
	@Parameter
	private String[] myArray;

	/**
	 * Collections: This category covers any class which implements
	 * java.util.Collection such as ArrayList or HashSet. These parameters are
	 * configured by specifying the parameter multiple times just like an array.
	 */
	@Parameter
	private List<String> myList;

	/**
	 * Maps: This category covers any class which implements java.util.Map such
	 * as HashMap but does not implement java.util.Properties. These parameters
	 * are configured by including XML tags in the form <key>value</key> in the
	 * parameter configuration.
	 */
	@Parameter
	private Map<String, String> myMap;

	/**
	 * Properties: This category covers any map which implements
	 * java.util.Properties. These parameters are configured by including XML
	 * tags in the form <property><name>myName</name> <value>myValue</value>
	 * </property> in the parameter configuration.
	 */
	@Parameter
	private Properties myProperties;

	public static class MyObject {
		private String myField;

		public String toString() {
			return "MyObject(myField=" + myField + ")";
		}
	}

	/**
	 * Other Object Classes: This category covers any class which does not
	 * implement java.util.Map, java.util.Collection, or java.util.Dictionary.
	 */
	@Parameter
	private MyObject myObject;

	public void execute() throws MojoExecutionException {
		getLog().info("------------------");
		getLog().info(greeting);
		getLog().info("outputDir = " + outputDirectory.getAbsolutePath());

		// -----Parameter Types With One Value------------
		getLog().info("#Parameter Types With One Value");
		getLog().info("myBoolean = " + myBoolean);
		getLog().info("myInteger = " + myInteger);
		getLog().info("myDouble = " + myDouble);
		getLog().info("myDate = " + myDate);
		getLog().info(
				"myFile = "
						+ ((myFile == null) ? "null" : myFile.getAbsolutePath()));
		getLog().info("myText = " + myText);
		getLog().info("myColor = " + myColor.name());

		// -----Parameter Types With Multiple Values------------
		getLog().info("#Parameter Types With Multiple Values");
		getLog().info("myArray = " + Arrays.toString(myArray));
		getLog().info("myList = " + myList);
		getLog().info("myMap = " + myMap);
		getLog().info("myProperties = " + myProperties);
		getLog().info("myObject = " + myObject);
	}

}
