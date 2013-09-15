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
import org.jopendocument.dom.text.Paragraph;

public class App {
	private static StringBuilder outHTML = new StringBuilder("");
	private static ODPackage openDocumentPackage;
	private static Element automaticStyle;

	public static void recursiveElement(Object myObj) {
		Element currentContent = null;
		Text currentText = null;
		String createdStyle = null;
		String curStr = myObj.toString();

		if (!curStr.startsWith("[Text: ")) {
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

		outHTML.append("<" + currentContent.getName() + " " + createdStyle
				+ ">" + ((Text) myObj).getValue() + "</"
				+ currentContent.getName() + ">");
	}

	public static void main(String[] args) throws IOException {
		openDocumentPackage = new ODPackage(new File("test.odt"));

		automaticStyle = (Element) openDocumentPackage.getTextDocument()
				.getContentDocument().getRootElement().getContent().get(2);

		// Paragraph Iteration
		for (int i = 0; i < openDocumentPackage.getTextDocument()
				.getParagraphCount(); i++) {
			outHTML.append("<p>");
			Paragraph currentParagraph = openDocumentPackage.getTextDocument()
					.getParagraph(i);
			Element currentElement = currentParagraph.getElement();

			// Contents of Paragraph Iteration
			for (int j = 0; j < currentElement.getContent().size(); j++) {
				recursiveElement(currentElement.getContent().get(j));
			}

			outHTML.append("</p>");
		}

		htmlBuilder();
		System.out.println(outHTML);
		saveToFile("test.html", outHTML.toString());
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
