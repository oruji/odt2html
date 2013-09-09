package org.oruji.odt2html;

import java.io.File;
import java.io.IOException;

import org.jopendocument.dom.ODPackage;

public class App {
	public static void main(String[] args) throws IOException {
		ODPackage p = new ODPackage(new File("/home/aminpy/durud.odt"));

		for (int i = 0; i < p.getTextDocument().getParagraphCount(); i++) {
			System.out.println(p.getTextDocument().getParagraph(i)
					.getCharacterContent());
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
