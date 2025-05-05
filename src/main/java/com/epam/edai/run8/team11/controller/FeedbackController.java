package com.epam.edai.run8.team11.controller;

import com.epam.edai.run8.team11.dto.feedback.FeedbackUpdateDTO;
import com.epam.edai.run8.team11.dto.feedback.NewFeedbackDTO;
import com.epam.edai.run8.team11.exception.InvalidInputException;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.service.feedback.FeedbackService;
import com.epam.edai.run8.team11.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/feedbacks")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<String> createFeedback(@RequestBody NewFeedbackDTO newFeedbackDTO) {
        feedbackService.save(newFeedbackDTO);
        return ResponseEntity.ok("Feedback created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> findFeedbackById(@PathVariable String id) {
        if(id == null || id.isEmpty())
            throw new InvalidInputException(Feedback.FEEDBACK_ID, id);
        Feedback feedback = feedbackService.findById(id);
        return ResponseEntity.ok(feedback);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateById(@PathVariable String id, @RequestBody FeedbackUpdateDTO updateDTO){
        feedbackService.updateById(id, updateDTO);
        return ResponseEntity.ok("Feedback updated successfully");
    }

}
