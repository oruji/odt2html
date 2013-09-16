package org.oruji.odt2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jopendocument.dom.ODPackage;

public class App {
	private static StringBuilder outHTML = new StringBuilder("");
	private static ODPackage openDocumentPackage;
	private static Element automaticStyle;
	private static Element rootElement;
	private static Element bodyElement;
	private static Element textElement;

	public static void main(String[] args) throws IOException {
		openDocumentPackage = new ODPackage(new File("test.odt"));

		rootElement = openDocumentPackage.getTextDocument()
				.getContentDocument().getRootElement();

		automaticStyle = (Element) rootElement.getContent().get(2);
		bodyElement = (Element) rootElement.getContent().get(3);
		textElement = (Element) bodyElement.getContent().get(0);

		// iterating <office:text>
		for (Object myObj : textElement.getContent())
			recursiveElement(myObj);

		// Output HTML
		htmlBuilder();
		System.out.println(outHTML);
		saveToFile("test.html", outHTML.toString());
	}

	public static void recursiveElement(Object obj) {
		// Loop Condition
		if (obj.toString().startsWith("[Text: ")) {
			Text text = (Text) obj;
			String bodyTag = text.getValue().replace("<", "&lt;")
					.replace(">", "&gt;");
			outHTML.append(bodyTag);
			return;
		}

		Element element = ((Element) obj);
		String startTag = "";
		String endTag = "";

		if (element.getName().equals("sequence-decls")) {
			startTag = "";
			endTag = "";

		} else if (element.getName().equals("span")) {
			String currentAttr = element.getAttributeValue("style-name",
					element.getNamespace());
			String createdStyle = createStyle(getStyleList(currentAttr,
					automaticStyle));
			createdStyle = createdStyle.equals("") ? "" : " " + createdStyle;

			startTag = "<span" + createdStyle + ">";
			endTag = "</span>";

		} else if (element.getName().equals("h")) {
			String tagName = "";
			String myAttribute = element.getAttributeValue("style-name",
					element.getNamespace());

			for (Object con : automaticStyle.getContent()) {
				if (myAttribute.equals(((Element) con).getAttributeValue(
						"name", ((Element) con).getNamespace()))) {
					switch (((Element) con)
							.getAttributeValue("parent-style-name",
									((Element) con).getNamespace())) {
					case "Heading_20_1":
						tagName = "h1";
						break;

					case "Heading_20_2":
						tagName = "h2";
						break;

					case "Heading_20_3":
						tagName = "h3";
						break;

					default:
						break;
					}
				}
			}

			String currentAttr = element.getAttributeValue("style-name",
					element.getNamespace());
			String createdStyle = createStyle(getStyleList(currentAttr,
					automaticStyle));
			createdStyle = createdStyle.equals("") ? "" : " " + createdStyle;
			startTag = "<" + tagName + createdStyle + ">";
			endTag = "</" + tagName + ">";

		} else if (element.getName().equals("p")) {

			String currentAttr = element.getAttributeValue("style-name",
					element.getNamespace());
			String createdStyle = createStyle(getStyleList(currentAttr,
					automaticStyle));
			createdStyle = createdStyle.equals("") ? "" : " " + createdStyle;
			startTag = "<div" + createdStyle + ">";
			endTag = "</div>";

		} else if (element.getName().equals("a")) {
			String myUrl = element.getAttributeValue("href", Namespace
					.getNamespace("xlink", "http://www.w3.org/1999/xlink"));
			startTag = "<a href='" + myUrl + "'>";
			endTag = "</a>";

		} else if (element.getName().equals("tab")) {
			for (int i = 0; i < 8; i++)
				outHTML.append("&nbsp;");

		} else if (element.getName().equals("s")) {
			String spaceNo = element.getAttributeValue("c",
					element.getNamespace());

			if (spaceNo == null) {
				outHTML.append("&nbsp;");

			} else {
				for (int i = 0; i < (spaceNo == null ? 0 : Integer
						.parseInt(spaceNo)); i++)
					outHTML.append("&nbsp;");
			}

		} else if (element.getName().equals("list-item")) {
			startTag = "<li>";
			endTag = "</li>";

		} else if (element.getName().equals("list")) {
			startTag = "<ul>";
			endTag = "</ul>";
		}

		// start tag
		outHTML.append(startTag);

		// body tag
		for (Object obj2 : element.getContent()) {
			recursiveElement(obj2);
		}

		// end tag
		outHTML.append(endTag);
	}

	@SuppressWarnings("unchecked")
	public static List<Attribute> getStyleList(String attName,
			Element automaticStyle) {

		for (Object obj : automaticStyle.getContent()) {
			Element element = (Element) obj;

			if (attName != null
					&& attName.equals(element.getAttributeValue("name",
							element.getNamespace()))) {
				if (element.getContent().size() > 0)
					return ((Element) element.getContent().get(0))
							.getAttributes();
			}
		}

		return null;
	}

	public static String createStyle(List<Attribute> attList) {
		StringBuilder createdStyle = new StringBuilder("");

		if (attList != null && attList.size() > 0) {
			createdStyle.append("style='");

			for (Attribute att : attList) {
				if (att.getName().equals("font-name")) {
					createdStyle.append("font-family:" + att.getValue() + ";");

				} else if (att.getName().equals("writing-mode")) {
					// if (att.getValue().equals("rl-tb"))
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

				if (att.getNamespacePrefix().equals("fo")) {
					if (!att.getName().equals("text-align")) {
						createdStyle.append(att.getName() + ":"
								+ att.getValue() + ";");
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
}
