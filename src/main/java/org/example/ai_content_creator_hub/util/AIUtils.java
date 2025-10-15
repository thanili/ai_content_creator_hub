package org.example.ai_content_creator_hub.util;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.example.ai_content_creator_hub.dto.ImageRequestDto;
import org.example.ai_content_creator_hub.dto.TextRequestDto;
import org.example.ai_content_creator_hub.dto.TextResponseDto;
import org.example.ai_content_creator_hub.dto.google.request.AnalyzeSentimentRequestDto;
import org.example.ai_content_creator_hub.dto.google.request.DocumentType;
import org.example.ai_content_creator_hub.dto.google.request.TextEncodingType;
import org.example.ai_content_creator_hub.dto.openai.request.*;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAIChoice;
import org.example.ai_content_creator_hub.dto.openai.response.OpenAITextResponseDto;
import org.example.ai_content_creator_hub.dto.google.request.DocumentDTO;
import org.example.ai_content_creator_hub.entity.ContentRole;
import org.example.ai_content_creator_hub.entity.ContentType;
import org.example.ai_content_creator_hub.entity.GeneratedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AIUtils {
    private static final Logger logger = LoggerFactory.getLogger(AIUtils.class);

    @NotNull
    public static OpenAITextRequestDto createOpenAISingleTextRequest(TextRequestDto textRequest, String openAiModel) {

        Content content = new Content();
        content.setType(ContentType.TEXT.getDisplayName());
        content.setText(textRequest.getInputText());

        Message message = new Message();
        message.setRole(ContentRole.USER.getDisplayName());
        message.setContent(List.of(content));

        OpenAITextRequestDto openAiRequest = new OpenAITextRequestDto();
        openAiRequest.setModel(openAiModel);
        openAiRequest.setMessages(List.of(message));
        return openAiRequest;
    }

    @NotNull
    public static OpenAITextRequestDto createOpenAIConversationTextRequest(
            List<GeneratedContent> conversationMessages,
            String openAiModel) {

        List<Message> messageList = new ArrayList<>();

        // (Optional) system preface per chat—helps with consistency
        Message system = new Message();
        system.setRole(ContentRole.SYSTEM.getDisplayName());
        Content sysContent = new Content();
        sysContent.setType("text");
        sysContent.setText("You are a helpful assistant.");
        system.setContent(List.of(sysContent));
        messageList.add(system);

        for (GeneratedContent msg : conversationMessages) {
            Message m = new Message();
            // Map roles 1:1
            m.setRole(msg.getContentRole().getDisplayName());

            Content c = new Content();
            c.setType("text");
            c.setText(msg.getContent());
            m.setContent(List.of(c));

            messageList.add(m);
        }

        OpenAITextRequestDto req = new OpenAITextRequestDto();
        req.setModel(openAiModel);
        req.setMessages(messageList);
        return req;
    }

    @NotNull
    public static OpenAITextRequestDto createOpenAISummarizeTextRequest(TextRequestDto textRequest, String openAiModel) {

        Content systemContent = new Content();
        systemContent.setType(ContentType.TEXT.getDisplayName());
        systemContent.setText("You are a highly proficient text summarizer. Your task is to generate concise, accurate summaries of provided content. Make sure the summaries are no longer than 100 words. Summarize in a neutral tone and focus on the main ideas of the text without losing key information.");

        Message systemMessage = new Message();
        systemMessage.setRole(ContentRole.SYSTEM.getDisplayName());
        systemMessage.setContent(List.of(systemContent));

        Content userContent = new Content();
        userContent.setType(ContentType.TEXT.getDisplayName());
        userContent.setText(textRequest.getInputText());

        Message userMessage = new Message();
        userMessage.setRole(ContentRole.USER.getDisplayName());
        userMessage.setContent(List.of(userContent));

        OpenAITextRequestDto openAiRequest = new OpenAITextRequestDto();
        openAiRequest.setModel(openAiModel);
        openAiRequest.setMessages(List.of(systemMessage, userMessage));

        openAiRequest.setMaxTokens(150);
        openAiRequest.setTemperature(0.3);
        openAiRequest.setToP(0.4);
        openAiRequest.setN(1);

        return openAiRequest;
    }

    @NotNull
    public static TextResponseDto getLastOpenAITextResponse(OpenAITextResponseDto response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            logger.error("OpenAI response/choices is null or empty");
            return new TextResponseDto("No response from model");
        }

        return response.getChoices().stream()
                .map(OpenAIChoice::getMessage)
                .filter(m -> m != null && "assistant".equalsIgnoreCase(m.getRole()))
                .reduce((first, second) -> second) // last assistant message
                .map(m -> new TextResponseDto(m.getContent()))
                .orElseGet(() -> {
                    logger.error("No assistant message found in OpenAI response");
                    return new TextResponseDto("No assistant message found in OpenAI response");
                });
    }

    @NotNull
    public static AnalyzeSentimentRequestDto createGoogleNLPSentimentAnalysisRequest(TextRequestDto textRequest) {
        DocumentDTO document = new DocumentDTO();
        document.setDocumentType(DocumentType.PLAIN_TEXT);
        document.setContent(textRequest.getInputText());

        AnalyzeSentimentRequestDto googleNlpRequest = new AnalyzeSentimentRequestDto();
        googleNlpRequest.setDocument(document);
        googleNlpRequest.setTextEncodingType(TextEncodingType.UTF8);

        return googleNlpRequest;
    }

    @NotNull
    public static OpenAIImageRequestDto createOpenAIGenerateImageRequest(@Valid ImageRequestDto imageRequest, String openAiImageMode) {
        OpenAIImageRequestDto imageRequestDto = new OpenAIImageRequestDto();
        imageRequestDto.setPrompt(imageRequest.getInputText());
        imageRequestDto.setN(1);
        imageRequestDto.setSize(ImageSize.IMAGE_SIZE_1024_1792);
        imageRequestDto.setModel(openAiImageMode);

        return imageRequestDto;
    }
}
