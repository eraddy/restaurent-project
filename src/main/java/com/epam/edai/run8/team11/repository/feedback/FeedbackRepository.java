package com.epam.edai.run8.team11.repository.feedback;

import com.epam.edai.run8.team11.dto.feedback.FeedbackUpdateDTO;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {
    String TABLE_NAME = "feedbacks";

    void save(Feedback feedback);
    Optional<Feedback> findById(String feedbackId);
    List<Feedback> findAll();
    Page<Feedback> findByLocationIdWithPagination(String locationId, FeedbackType type, String sortField, Boolean isDescending, Integer size, Integer page);
    List<Feedback> findByFeedbackType(FeedbackType type);
    Feedback updateById(String id, Feedback updatedFeedback);
    void deleteById(String feedbackId);
}
