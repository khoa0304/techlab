package lab.datacapture.pdf.reader.service;

import java.io.IOException;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

@Service
public class SimplePdfReader {


	public String extractPdfContent(URL pdfFileURL) throws IOException {

		PDDocument document = null;
		String text = null;
		try {

			document = PDDocument.load(pdfFileURL.openStream());

			// Instantiate PDFTextStripper class
			PDFTextStripper pdfStripper = new PDFTextStripper();

			// Retrieving text from PDF document
			text = pdfStripper.getText(document);

		} catch (Exception e) {

		} finally {

			if (document != null) {
				// Closing the document
				document.close();
			}
		}

		return text;
	}

}
