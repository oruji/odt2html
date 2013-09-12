package org.oruji.odt2html;

import java.io.File;
import java.io.IOException;

import org.jopendocument.dom.ODPackage;


public class App {
	public static void main(String[] args) throws IOException {
		ODPackage p = new ODPackage(new File("test.odt"));

		StringBuilder outputHTML = new StringBuilder("");

		for (int i = 0; i < p.getTextDocument().getParagraphCount(); i++) {
			outputHTML.append("<p>\n\t");
			outputHTML.append(p.getTextDocument().getParagraph(i).getCharacterContent());
			
			for (int j = 0; j < p.getTextDocument().getParagraph(i).getElement().getContent().size(); j++) {
				System.out.println(p.getTextDocument().getParagraph(i).getElement().getContent().get(j));
			}
				
			outputHTML.append("\n</p>\n");
		}

		System.out.println(outputHTML);

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
