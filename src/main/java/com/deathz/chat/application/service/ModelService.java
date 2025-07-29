package com.deathz.chat.application.service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ModelService {

    @Autowired
    private OllamaChatModel chatModel;

    public static enum Model {

        LLAMA3("llama3.1:8b"),
        DEEPSEEK("deepseek-r1:8b"),
        GEMMA3("gemma3:4b"),
        LLAVA("llava-llama3:8b");

        private String value;

        Model(String value) {

            this.value = value;
        }

        public String getValue() {

            return value;
        }
    }

    private String currentModel;

    public ModelService(@Value("${LLM_MODEL:}") String model) {

        this.currentModel = model;
    }

    public String getCurrentModel() {

        return currentModel;
    }

    public void setCurrentModel(String model) {

        this.currentModel = model;
    }

    public OllamaChatModel getChatModel() {

        return chatModel;
    }
}