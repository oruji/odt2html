package org.oruji.odt2html;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Text;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.text.Paragraph;

public class App {

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
		StringBuilder outStr = new StringBuilder("");
		outStr.append("style='");

		for (Attribute att : attList)
			System.out.println(att);

		return outStr.toString();
	}

	public static void main(String[] args) throws IOException {
		StringBuilder outHTML = new StringBuilder("");
		ODPackage p = new ODPackage(new File("test.odt"));

		Element automaticStyle = (Element) p.getTextDocument()
				.getContentDocument().getRootElement().getContent().get(2);

		automaticStyle.getContent();

		// Paragraph Iteration
		for (int i = 0; i < p.getTextDocument().getParagraphCount(); i++) {
			Paragraph currentParagraph = p.getTextDocument().getParagraph(i);
			Element currentElement = currentParagraph.getElement();

			// Contents of Paragraph Iteration
			for (int j = 0; j < currentElement.getContent().size(); j++) {
				Element currentContent = null;
				Text currentText = null;
				String curStr = currentElement.getContent().get(j).toString();

				if (curStr.startsWith("[Text: ")) {
					currentText = (Text) currentElement.getContent().get(j);
					outHTML.append(currentText.getValue());

				} else if (curStr.startsWith("[Element: ")) {
					currentContent = (Element) currentElement.getContent().get(
							j);

					getStyleList(((Attribute) currentContent.getAttributes()
							.get(0)).getValue(), automaticStyle);

					outHTML.append("<" + currentContent.getName() + ">"
							+ currentContent.getValue() + "</"
							+ currentContent.getName() + ">");

					// Attribute attribute = (Attribute) currentContent
					// .getAttributes().get(0);
				}
			}
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
}
