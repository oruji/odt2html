package org.oruji.odt2html;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom.Element;
import org.jdom.Text;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.text.Paragraph;

public class App {

	public static List<String> getStyleList(String attName) {
		return null;
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
