package com.deathz.chat.application.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.domain.exceptions.FailedToParseFileException;
import com.deathz.chat.domain.exceptions.FileNameIsNullException;

@Service
public class DocumentService {

    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new FileNameIsNullException();
        }

        StringBuilder finalMessage = new StringBuilder();
        try {

            Tika tika = new Tika();
            finalMessage.append("\n=== Beginning of file: " + file.getOriginalFilename() + " ===\n");
            finalMessage.append(tika.parseToString(file.getInputStream()));
            finalMessage.append("\n=== End of file: " + file.getOriginalFilename() + " ===\n");
            return finalMessage.toString();
        } catch (Exception e) {

            throw new FailedToParseFileException(e);
        }
    }
}
