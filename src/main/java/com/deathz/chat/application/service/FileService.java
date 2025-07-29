package com.deathz.chat.application.service;

import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import com.deathz.chat.domain.exceptions.FailedToParseFileException;
import com.deathz.chat.domain.exceptions.FileNameIsNullException;

@Service
public class FileService {

    Tika tika = new Tika();

    public String extractText(MultipartFile file) {

        StringBuilder finalMessage = new StringBuilder();
        try {

            finalMessage.append("\n=== Beginning of file: " + file.getOriginalFilename() + " ===\n");
            finalMessage.append(tika.parseToString(file.getInputStream()));
            finalMessage.append("\n=== End of file: " + file.getOriginalFilename() + " ===\n");
            return finalMessage.toString();
        } catch (Exception e) {

            throw new FailedToParseFileException(e);
        }
    }

    public Media buildMedia(MultipartFile file) {

        try {

            String mimeType = tika.detect(file.getInputStream());
            String[] parts = mimeType.split("/");
            return Media.builder()
                    .mimeType(new MimeType(parts[0], parts[1]))
                    .data(file.getBytes())
                    .build();
        } catch (IOException e) {

            throw new FailedToParseFileException(e);
        }
    }

    public String getFileType(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            return "";
        }

        try {

            return tika.detect(file.getInputStream());
        } catch (Exception e) {

            throw new FailedToParseFileException(e);
        }
    }
}
