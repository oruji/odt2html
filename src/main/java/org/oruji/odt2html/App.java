package org.oruji.odt2html;

import java.io.File;
import java.io.IOException;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jopendocument.dom.ODPackage;
import org.jopendocument.dom.text.Paragraph;

public class App {
	public static void main(String[] args) throws IOException {
		ODPackage p = new ODPackage(new File("test.odt"));

		p.getTextDocument().getContentDocument().getRootElement();
		
		for (int i = 0; i < p.getTextDocument().getParagraphCount(); i++) {
			Paragraph currentParagraph = p.getTextDocument().getParagraph(i);
			Element currentElement = currentParagraph.getElement();

			for (int j = 0; j < currentElement.getContent().size(); j++) {
				Element currentContent = (Element) currentElement.getContent()
						.get(j);
				Attribute attribute = (Attribute) currentContent
						.getAttributes().get(0);

				if (attribute.getValue().equals("T10"))
					System.out.println("bold" + currentContent.getValue());

				else if (attribute.getValue().equals("T1"))
					System.out.println("italic" + currentContent.getValue());

				else
					System.out.println(currentContent.getValue());
			}
		}

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
