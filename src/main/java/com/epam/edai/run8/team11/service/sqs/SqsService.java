package com.epam.edai.run8.team11.service.sqs;

import com.epam.edai.run8.team11.dto.sqs.EventPayloadDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;

@Service
@Slf4j
public class SqsService {

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("sqsUrl")
    private String url;

    public SdkHttpResponse sendMessage(EventPayloadDTO dto) {
        try {
            String messageGroupId = String.format("MSG-%s", dto.getEventType().toString());
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(url)
                    .messageBody(objectMapper.writeValueAsString(dto))
                    .messageGroupId(messageGroupId)
                    .messageDeduplicationId(String.format("%s-%s", messageGroupId, UUID.randomUUID()))
                    .build();
            SendMessageResponse response = sqsClient.sendMessage(request);
            log.info("Sqs Response: {}", response);
            return response.sdkHttpResponse();
        }catch (JsonProcessingException e){
            throw new InternalError();
        }
    }

}
