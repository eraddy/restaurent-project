package com.epam.edai.run8.team11.service.feedback;

import com.epam.edai.run8.team11.dto.feedback.FeedbackUpdateDTO;
import com.epam.edai.run8.team11.dto.feedback.NewFeedbackDTO;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {
    void save(NewFeedbackDTO newFeedbackDTO);
    Feedback findById(String feedbackId);
    List<Feedback> findAll();
    Page<Feedback> findByLocationIdWithPagination(String locationId, FeedbackType type, String sort, Integer size, Integer page);
    List<Feedback> findByFeedbackType(FeedbackType type);
    void updateById(String id, FeedbackUpdateDTO updateDTO);
}
