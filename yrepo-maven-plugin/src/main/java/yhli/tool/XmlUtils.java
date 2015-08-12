package yhli.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author yhli
 *
 */
public class XmlUtils {

	public static Element getRootElement(File fxml) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(fxml);
			return (Element) doc.getChildNodes().item(0);
		} catch (IOException e) {
			throw e;
		} catch (ParserConfigurationException e) {
			throw new IOException("parse xml: " + fxml.getAbsolutePath(), e);
		} catch (SAXException e) {
			throw new IOException("parse xml: " + fxml.getAbsolutePath(), e);
		}
	}

	public static List<Element> getElementsByTagName(Element parent, String name) {
		List<Element> result = new ArrayList<Element>();
		NodeList list = parent.getElementsByTagName(name);
		for (int i = 0; i < list.getLength(); i++) {
			result.add((Element) (list.item(i)));
		}
		return result;
	}

	public static String getAttribute(Element e, String name) {
		return e.getAttribute(name);
	}

}
