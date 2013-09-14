package org.oruji.odt2html;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Text;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.text.Paragraph;

public class App {
	private static StringBuilder outHTML = new StringBuilder("");
	private static ODPackage p;
	private static Element automaticStyle;

	public static void recursiveElement(Element element) {
		Element currentContent = null;
		Text currentText = null;
		String createdStyle = null;

		if (element.getContent().size() > 0) {
			String curStr = element.getContent().get(0).toString();

			if (!curStr.startsWith("[Text: ")) {
				currentContent = (Element) element.getContent().get(0);
				recursiveElement(currentContent);
				return;
			} else {
				currentText = (Text) element.getContent().get(0);
			}
		}

		String myAtt = null;

		currentContent = ((Element) currentText.getParent());

		if (currentContent.getAttributes().size() > 0) {
			myAtt = ((Attribute) currentContent.getAttributes().get(0))
					.getValue();
		}

		createdStyle = createStyle(getStyleList(myAtt, automaticStyle));

		outHTML.append("<" + currentContent.getName() + " " + createdStyle
				+ ">" + element.getValue() + "</" + currentContent.getName()
				+ ">");
	}

	public static void main(String[] args) throws IOException {
		// StringBuilder outHTML = new StringBuilder("");
		String createdStyle = null;
		p = new ODPackage(new File("test.odt"));

		automaticStyle = (Element) p.getTextDocument().getContentDocument()
				.getRootElement().getContent().get(2);

		// Paragraph Iteration
		for (int i = 0; i < p.getTextDocument().getParagraphCount(); i++) {
			outHTML.append("<p>");
			Paragraph currentParagraph = p.getTextDocument().getParagraph(i);
			Element currentElement = currentParagraph.getElement();

			recursiveElement(currentParagraph.getElement());

			// Contents of Paragraph Iteration
//			for (int j = 0; j < currentElement.getContent().size(); j++) {
//				Element currentContent = null;
//				Text currentText = null;
//				String curStr = currentElement.getContent().get(j).toString();
//
//				if (curStr.startsWith("[Text: ")) {
//					currentText = (Text) currentElement.getContent().get(j);
//					outHTML.append(currentText.getValue());
//
//				} else if (curStr.startsWith("[Element: ")) {
//					currentContent = (Element) currentElement.getContent().get(
//							j);
//
//					String myAtt = null;
//
//					if (currentContent.getAttributes().size() > 0) {
//						myAtt = ((Attribute) currentContent.getAttributes()
//								.get(0)).getValue();
//					}
//
//					createdStyle = createStyle(getStyleList(myAtt,
//							automaticStyle));
//
//					outHTML.append("<" + currentContent.getName() + " "
//							+ createdStyle + ">" + currentContent.getValue()
//							+ "</" + currentContent.getName() + ">");
//				}
//			}
			outHTML.append("</p>");
		}

		System.out.println(outHTML);

		// System.out.println(p.getTextDocument().getParagraph(0)
		// .getCharacterContent(true));

		// System.out.println(p.getTextDocument().getCharacterContent(true));

		// ODSingleXMLDocument doc = p.toSingle();

		// final Heading heading = new Heading();
		// heading.setStyle("Movie_20_Heading");
		// heading.addContent("Here is my title");
		// doc.add(heading);
		//
		// final Paragraph paragraph = new Paragraph();
		// paragraph.setStyle("Synopsis_20_Para");
		// paragraph.addContent("Here is my paragraph");
		// doc.add(paragraph);
	}

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
				if (att.getNamespacePrefix().equals("fo")) {
					createdStyle.append(att.getName() + ":" + att.getValue()
							+ ";");
				}
			}

		createdStyle.append("'");

		return createdStyle.toString();
	}
}
