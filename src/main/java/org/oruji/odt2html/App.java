package org.oruji.odt2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jopendocument.dom.ODPackage;

public class App {
	private static StringBuilder outHTML = new StringBuilder("");
	private static ODPackage openDocumentPackage;
	private static Element automaticStylesElement;
	private static Element rootElement;
	private static Element bodyElement;
	private static Element textElement;

	public static void main(String[] args) throws IOException {
		openDocumentPackage = new ODPackage(new File("test.odt"));

		rootElement = openDocumentPackage.getTextDocument()
				.getContentDocument().getRootElement();

		automaticStylesElement = getChildByName(rootElement, "automatic-styles");
		bodyElement = getChildByName(rootElement, "body");
		textElement = getChildByName(bodyElement, "text");

		// iterating <office:text>
		recursiveElement(textElement);

		// Output HTML
		htmlBuilder();
		System.out.println(outHTML);
		saveToFile("test.html", outHTML.toString());
	}

	public static void recursiveElement(Object obj) {
		// Loop Condition
		if (obj instanceof Text) {
			Text text = (Text) obj;
			outHTML.append(text.getValue().replace("<", "&lt;")
					.replace(">", "&gt;"));
			return;
		}

		Element element = (Element) obj;

		// tab tag
		if (element.getName().equals("tab")) {
			for (int i = 0; i < 8; i++)
				outHTML.append("&nbsp;");

			// space tag
		} else if (element.getName().equals("s")) {
			String spaceNo = getAttVal(element, "c");

			if (spaceNo == null) {
				outHTML.append("&nbsp;");

			} else {
				for (int i = 0; i < (spaceNo == null ? 0 : Integer
						.parseInt(spaceNo)); i++)
					outHTML.append("&nbsp;");
			}
		}

		String tagName = "";
		String startTag = "";
		String attributeStr = "";
		String endTag = "";

		tagName = tagNameBuilder(element);
		attributeStr = attBuilder(element);

		if (tagName != null && !tagName.equals("")) {
			startTag = "<" + tagName + attributeStr + ">";
			endTag = "</" + tagName + ">";
		}

		// start tag
		outHTML.append(startTag);

		// body
		for (Object obj2 : element.getContent()) {
			recursiveElement(obj2);
		}

		// end tag
		outHTML.append(endTag);
	}

	public static String attBuilder(Element element) {

		if (element.getName().equals("list")) {
			Element myEl6 = null;
			myEl6 = (Element) element.getContent().get(0);
			element = (Element) myEl6.getContent().get(0);
		}

		if (element.getName().equals("span") || element.getName().equals("h")
				|| element.getName().equals("p")) {
			String currentAtt = getAttVal(element, "style-name");
			String createdStyle = createStyle(
					getStyleList(currentAtt, automaticStylesElement),
					element.getName());

			return createdStyle.equals("") ? "" : " " + createdStyle;

		} else if (element.getName().equals("a")) {
			String myUrl = element.getAttributeValue("href", Namespace
					.getNamespace("xlink", "http://www.w3.org/1999/xlink"));
			return " href='" + myUrl + "';";
		}

		return "";
	}

	public static String tagNameBuilder(Element element) {
		switch (element.getName()) {
		case "span":
		case "a":
			return element.getName();

		case "p":
			return "div";

		case "list":
			return "ul";

		case "list-item":
			return "li";

		case "h":
			String currentAtt = getAttVal(element, "style-name");

			for (Object con : automaticStylesElement.getContent()) {
				Element loopEl = (Element) con;

				if (currentAtt.equals(getAttVal(loopEl, "name"))) {
					switch (getAttVal(loopEl, "parent-style-name")) {
					case "Heading_20_1":
						return "h1";

					case "Heading_20_2":
						return "h2";

					case "Heading_20_3":
						return "h3";

					case "Heading_20_4":
						return "h4";

					case "Heading_20_5":
						return "h5";

					case "Heading_20_6":
						return "h6";

					default:
						break;
					}
				}
			}
		}

		return "";
	}

	@SuppressWarnings("unchecked")
	public static List<Attribute> getStyleList(String attValue,
			Element automaticStyle) {

		for (Element element : getElements(automaticStyle)) {
			if (attValue != null && isSameEl(element, "name", attValue)) {
				if (element.getContent().size() > 0) {
					List<Attribute> attList = new ArrayList<>();

					for (Element element2 : getElements(element)) {
						for (Attribute att : (List<Attribute>) element2
								.getAttributes()) {
							attList.add(att);
						}
					}

					return attList;
				}
			}
		}

		return null;
	}

	public static String createStyle(List<Attribute> attList, String elementName) {
		StringBuilder createdStyle = new StringBuilder("");

		if (attList != null && attList.size() > 0) {
			createdStyle.append("style='");

			for (Attribute att : attList) {
				if (att.getName().equals("font-name")
						|| att.getName().equals("font-name-complex")) {
					createdStyle.append("font-family:" + att.getValue() + ";");

				} else if (att.getName().equals("writing-mode")) {
					switch (att.getValue()) {
					case "rl-tb":
						createdStyle.append("direction:rtl;");
						break;

					case "lr-tb":
						createdStyle.append("direction:ltr;");
						break;

					default:
						break;
					}

				} else if (att.getName().equals("text-align")) {
					switch (att.getValue()) {
					case "start":
						createdStyle.append("text-align:left;");
						break;

					case "end":
						createdStyle.append("text-align:right;");
						break;

					default:
						break;
					}
				}

				if (!elementName.equals("list")) {
					if (att.getNamespacePrefix().equals("fo")) {
						if (!att.getName().equals("text-align")) {
							createdStyle.append(att.getName() + ":"
									+ att.getValue() + ";");
						}
					}
				}
			}

			createdStyle.append("'");
		}

		if (createdStyle.toString().equals("style=''"))
			createdStyle = new StringBuilder();

		return createdStyle.toString();
	}

	public static void htmlBuilder() {
		StringBuilder myOutHTML = new StringBuilder();
		myOutHTML
				.append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head><body>\n\n");
		myOutHTML.append(outHTML);
		myOutHTML.append("\n\n</body></html>");
		outHTML = new StringBuilder(myOutHTML);
	}

	public static void saveToFile(String fileName, String text) {
		Writer out = null;
		File file = new File(fileName);

		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(text);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Element getChildByName(Element element, String name) {
		for (Object obj : element.getContent()) {
			Element myEl = ((Element) obj);
			if (name.equals(myEl.getName())) {
				return myEl;
			}
		}
		return null;
	}

	public static Attribute getAtt(Element element, String attName) {
		return element.getAttribute(attName, element.getNamespace());
	}

	public static String getAttVal(Element element, String attName) {
		return getAtt(element, attName) == null ? null : getAtt(element,
				attName).getValue();
	}

	public static boolean isSameEl(Element element, String attName,
			String attValue) {
		Attribute attribute = getAtt(element, attName);

		if (attribute != null && attValue.equals(attribute.getValue()))
			return true;

		return false;
	}

	public static List<Element> getElements(Element element) {
		List<Element> elementList = new ArrayList<>();

		for (Object obj : element.getContent()) {
			if (obj instanceof Element) {
				elementList.add((Element) obj);
			}
		}

		return elementList;
	}
}
