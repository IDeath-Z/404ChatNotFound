package com.deathz.chat.application.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.domain.exceptions.FailedToParseFileException;
import com.deathz.chat.domain.exceptions.FileNameIsNullException;
import com.deathz.chat.domain.exceptions.UnsupportedFileTypeException;

@Service
public class DocumentService {

    private enum DocumentType {
        PDF(".pdf"),
        DOCX(".docx"),
        TXT(".txt"),
        UNKNOWN("");

        private final String extension;

        DocumentType(String extension) {
            this.extension = extension;
        }

        private static DocumentType fromFileName(String fileName) {
            for (DocumentType type : values()) {
                if (fileName != null && fileName.toLowerCase().endsWith(type.extension)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
    }

    public String processDocument(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        DocumentType type = DocumentType.fromFileName(fileName);

        try {
            if (fileName != null) {

                switch (type) {
                    case PDF:
                        return processPDF(file);

                    case UNKNOWN:
                    default:

                        throw new UnsupportedFileTypeException(type.extension);
                }
            } else {
                throw new FileNameIsNullException();
            }
        } catch (Exception e) {

            throw new FailedToParseFileException(e);
        }
    }

    private String processPDF(MultipartFile pdf) throws Exception {

        StringBuilder content = new StringBuilder();

        content.append("\n======== Start of PDF Content ========\n");

        PDDocument document = PDDocument.load(pdf.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());
        String text = stripper.getText(document);
        content.append(text);

        content.append("\n======== End of PDF Content ========\n");
        return content.toString();
    }
}
