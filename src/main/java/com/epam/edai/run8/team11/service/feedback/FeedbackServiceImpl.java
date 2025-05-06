package com.epam.edai.run8.team11.service.feedback;


import com.epam.edai.run8.team11.dto.feedback.FeedbackUpdateDTO;
import com.epam.edai.run8.team11.dto.feedback.NewFeedbackDTO;
import com.epam.edai.run8.team11.dto.sqs.EventPayloadDTO;
import com.epam.edai.run8.team11.dto.sqs.eventtype.EventType;
import com.epam.edai.run8.team11.dto.user.UserDto;
import com.epam.edai.run8.team11.exception.InvalidInputException;
import com.epam.edai.run8.team11.exception.feedback.FeedbackNotFoundException;
import com.epam.edai.run8.team11.exception.feedback.InvalidReservationStateException;
import com.epam.edai.run8.team11.exception.user.UserNotFoundException;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.service.reservaiton.ReservationService;
import com.epam.edai.run8.team11.service.sqs.SqsService;
import com.epam.edai.run8.team11.service.user.UserService;
import com.epam.edai.run8.team11.utils.AuthenticationUtil;
import com.epam.edai.run8.team11.utils.SortPageFields;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import com.epam.edai.run8.team11.model.reservation.Reservation;
import com.epam.edai.run8.team11.model.reservation.reservationstatus.ReservationStatus;
import com.epam.edai.run8.team11.repository.feedback.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService{

    private final FeedbackRepository feedbackRepository;
     private final ReservationService reservationService;
     private final UserService userService;
     private final AuthenticationUtil authenticationUtil;
     private final SqsService sqsService;

    private void validateNewFeedbackDTO(NewFeedbackDTO dto){
        List<String> invalidInputs = new ArrayList<>();

        final String reservationId = dto.getReservationId();
        if(reservationId == null || reservationId.isEmpty())
            invalidInputs.add(NewFeedbackDTO.RESERVATION_ID);

        final String cuisineComment = dto.getCuisineComment();
        if(cuisineComment == null || cuisineComment.isEmpty())
            invalidInputs.add(NewFeedbackDTO.CUISINE_COMMENT);

        final Double cuisineRating = dto.getCuisineRating();
        if(cuisineRating == null || cuisineRating<0 || cuisineRating>5)
            invalidInputs.add(NewFeedbackDTO.CUISINE_RATING);

        final String serviceComment = dto.getServiceComment();
        if(serviceComment == null || serviceComment.isEmpty())
            invalidInputs.add(NewFeedbackDTO.SERVICE_COMMENT);

        final Double serviceRating = dto.getServiceRating();
        if(serviceRating == null || serviceRating<0 || serviceRating>5)
            invalidInputs.add(NewFeedbackDTO.SERVICE_RATING);

        if(!invalidInputs.isEmpty())
            throw new InvalidInputException(invalidInputs);
    }

    private void validateFeedbackUpdateDTO(FeedbackUpdateDTO updateDTO){
        List<String> invalidInputs = new ArrayList<>();

        if(updateDTO.getRating() == null || updateDTO.getRating()<0 || updateDTO.getRating()>5)
            invalidInputs.add(FeedbackUpdateDTO.RATING);
        if(updateDTO.getReview() == null || updateDTO.getReview().length() < 3)
            invalidInputs.add(FeedbackUpdateDTO.REVIEW);

        if(!invalidInputs.isEmpty())
            throw new InvalidInputException(invalidInputs);
    }

    private boolean validateSortString(String sort){
        if(sort == null)
            return false;
        if(sort.isEmpty())
            return true;
       String[] sortParams = sort.split(",");
       String sortBy = sortParams[0];
       if(!(SortPageFields.DATE.equals(sortBy) || SortPageFields.RATE.equals(sortBy)))
           return false;
       if(sortParams.length == 1)
           return true;
       if(sortParams.length > 2)
           return false;
       String seq = sortParams[1];
       return SortPageFields.ASC.equals(seq) || SortPageFields.DESC.equals(seq);
    }

    private void validateFindByLocationIdWithPaginationParams(String locationId, FeedbackType type, String sort, Integer size, Integer page){
        List<String> invalidInputs = new ArrayList<>();

        if(locationId == null || locationId.isEmpty())
            invalidInputs.add(Feedback.LOCATION_ID);
        if(type == null || type.getValue().isEmpty())
            invalidInputs.add(Feedback.TYPE);
        if(!validateSortString(sort))
            invalidInputs.add(SortPageFields.SORT);
        if(size == null || size<0)
            invalidInputs.add(SortPageFields.SIZE);
        if(page == null || page<0)
            invalidInputs.add(SortPageFields.PAGE);

        if(!invalidInputs.isEmpty())
            throw new InvalidInputException(invalidInputs);
    }

    public void save(NewFeedbackDTO newFeedbackDTO) {
        log.info("Getting authenticated user");
        authenticationUtil.getAuthenticatedUser()
                .orElseThrow(UserNotLoggedInException::new);

        log.info("Validating request body");
        validateNewFeedbackDTO(newFeedbackDTO);

        Reservation reservation = reservationService.findById(newFeedbackDTO.getReservationId());
        log.info("Retrieved reservation from reservationId: {}", reservation);

        UserDto user = userService.getUserByUserId(reservation.getCustomerId())
                .getData().orElseThrow(() -> new UserNotFoundException(reservation.getCustomerId()));

        log.info("Retrieved user from userId: {}", user);
        if(!reservation.getStatus().equals(ReservationStatus.IN_PROGRESS))
            throw new InvalidReservationStateException(reservation.getStatus().getValue());

        //Cuisine Feedback
        Feedback cuisineFeedback = Feedback.builder()
                .feedbackId(UUID.randomUUID().toString())
                .type(FeedbackType.CUISINE_EXPERIENCE)
                .date(LocalDate.now())
                .locationId(reservation.getLocationId())
                .userEmail(user.getEmail())
                .rating(newFeedbackDTO.getCuisineRating())
                .review(newFeedbackDTO.getCuisineComment())
                .build();

        Feedback serviceFeedback = Feedback.builder()
                .feedbackId(UUID.randomUUID().toString())
                .type(FeedbackType.SERVICE_QUALITY)
                .date(LocalDate.now())
                .locationId(reservation.getLocationId())
                .userEmail(user.getEmail())
                .rating(newFeedbackDTO.getServiceRating())
                .review(newFeedbackDTO.getServiceComment())
                .build();

        feedbackRepository.save(cuisineFeedback);
        feedbackRepository.save(serviceFeedback);

        String feedbackIds = cuisineFeedback.getFeedbackId() + "," +
                serviceFeedback.getFeedbackId();

        reservation.setFeedbackId(feedbackIds);
        reservation.setStatus(ReservationStatus.FINISHED);
        reservationService.updateReservation(reservation);

        EventPayloadDTO feedbackReport = EventPayloadDTO
                .builder()
                .eventType(EventType.FEEDBACK)
                .reservationId(newFeedbackDTO.getReservationId())
                .build();
        log.info("Sending feedback to SQS: {}", feedbackReport);
        sqsService.sendMessage(feedbackReport);
    }

    public Feedback findById(String feedbackId) {
        if(feedbackId == null || feedbackId.isEmpty())
            throw new InvalidInputException(Feedback.FEEDBACK_ID, feedbackId);
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public Page<Feedback> findByLocationIdWithPagination(String locationId, FeedbackType type, String sort, Integer size, Integer page) {
        validateFindByLocationIdWithPaginationParams(locationId, type, sort, size, page);

        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        boolean isDescending = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]);

        return feedbackRepository.findByLocationIdWithPagination(locationId, type, sortField, isDescending, size, page);
    }

    @Override
    public List<Feedback> findByFeedbackType(FeedbackType type) {
        return feedbackRepository.findByFeedbackType(type);
    }

    @Override
    public void updateById(String id, FeedbackUpdateDTO updateDTO) {
        if(id == null || id.isEmpty())
            throw new InvalidInputException(Feedback.FEEDBACK_ID, id);
        validateFeedbackUpdateDTO(updateDTO);

        //TODO: Cannot get reservationId from feedbackId, reservation status check cannot be performed

        Feedback feedback = findById(id);
        feedback.setRating(updateDTO.getRating());
        feedback.setReview(updateDTO.getReview());

        feedbackRepository.updateById(id, feedback);
    }

}
