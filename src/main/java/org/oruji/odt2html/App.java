package org.oruji.odt2html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
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
		for (Object obj : textElement.getContent()) {
			Element myElement = ((Element) obj);

			if (myElement.getName().equals("sequence-decls"))
				continue;

			String endTag = "";

			if (myElement.getName().equals("p")) {
				outHTML.append("<p>");
				endTag = "</p>";
			}

			else if (myElement.getName().equals("h")) {
				myElement.getAttribute("style-name", myElement.getNamespace());
				automaticStyle.getChild("style");
				outHTML.append("<h1>");
				endTag = "</h1>";
			}

			else if (myElement.getName().equals("list")) {
				outHTML.append("<li>");
				endTag = "</li>";
			}

			for (Object myObj : myElement.getContent())
				recursiveElement(myObj);

			outHTML.append(endTag);
		}

		htmlBuilder();
		System.out.println(outHTML);
		saveToFile("test.html", outHTML.toString());
	}

	public static void recursiveElement(Object myObj) {
		Element currentContent = null;
		Text currentText = null;
		String createdStyle = null;
		String tagName = null;

		if (!myObj.toString().startsWith("[Text: ")) {
			if (((Element) myObj).getContent().size() > 0)
				recursiveElement(((Element) myObj).getContent().get(0));

			return;

		} else {
			currentText = (Text) myObj;
		}

		String myAtt = null;

		currentContent = ((Element) currentText.getParent());

		if (currentContent.getAttributes().size() > 0) {
			myAtt = ((Attribute) currentContent.getAttributes().get(0))
					.getValue();
		}

		createdStyle = createStyle(getStyleList(myAtt, automaticStyle));

		if (currentContent.getName().equals("h"))
			tagName = "span";

		else
			tagName = currentContent.getName();

		outHTML.append("<" + tagName + " " + createdStyle + ">"
				+ ((Text) myObj).getValue() + "</" + tagName + ">");
	}
	
	@SuppressWarnings("unchecked")
	public static List<Attribute> getStyleList(String attName,
			Element automaticStyle) {

		for (Object el : automaticStyle.getContent()) {
			for (Object att : ((Element) el).getAttributes()) {
				if (((Attribute) att).getName().equals("name")
						&& ((Attribute) att).getValue().equals(attName)) {
					((Attribute) att).getName();
					return ((Element) ((Element) el).getContent().get(0))
							.getAttributes();
				}
			}
		}

		return null;
	}

	public static String createStyle(List<Attribute> attList) {
		StringBuilder createdStyle = new StringBuilder("");
		createdStyle.append("style='");

		if (attList != null)
			for (Attribute att : attList) {
				if (att.getName().equals("font-name"))
					createdStyle.append("font-family:" + att.getValue() + ";");

				if (att.getNamespacePrefix().equals("fo")) {
					createdStyle.append(att.getName() + ":" + att.getValue()
							+ ";");
				}
			}

		createdStyle.append("'");

		return createdStyle.toString();
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

	public static void htmlBuilder() {
		StringBuilder myOutHTML = new StringBuilder();
		myOutHTML
				.append("<html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8' /></head><body>\n\n");
		myOutHTML.append(outHTML);
		myOutHTML.append("\n\n</body></html>");
		outHTML = new StringBuilder(myOutHTML);
	}
}
