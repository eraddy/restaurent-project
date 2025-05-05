package com.epam.edai.run8.team11.exception;

import com.epam.edai.run8.team11.exception.access.InvalidAccessException;
import com.epam.edai.run8.team11.exception.feedback.FeedbackNotFoundException;
import com.epam.edai.run8.team11.exception.feedback.InvalidReservationStateException;
import com.epam.edai.run8.team11.exception.location.InvalidLocationIdException;
import com.epam.edai.run8.team11.exception.location.LocationNotFoundException;
import com.epam.edai.run8.team11.exception.report.InvalidEventTypeException;
import com.epam.edai.run8.team11.exception.reservation.NoUpdateRequiredException;
import com.epam.edai.run8.team11.exception.reservation.ReservationAlreadyCancelledException;
import com.epam.edai.run8.team11.exception.reservation.ReservationNotFoundException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.user.UserAlreadyExistsException;
import com.epam.edai.run8.team11.exception.user.UserNotFoundException;
import com.epam.edai.run8.team11.exception.user.UserNotLogedInException;
import com.epam.edai.run8.team11.exception.waiter.WaiterAlreadyExistsException;
import com.epam.edai.run8.team11.exception.waiter.WaiterNotFoundException;
import com.epam.edai.run8.team11.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.sts.model.StsException;

import java.time.DateTimeException;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResponseUtil responseUtil;

    @ExceptionHandler({
            FeedbackNotFoundException.class, UserNotFoundException.class,
            LocationNotFoundException.class, WaiterNotFoundException.class,
            ReservationNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(RuntimeException e) {
        return responseUtil.buildNotFound(e.getMessage());
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class, WaiterAlreadyExistsException.class,
            SlotAlreadyBookedException.class, NoUpdateRequiredException.class})
    public ResponseEntity<Map<String, Object>> handleConflictExceptions(RuntimeException e) {
        return responseUtil.buildConflictResponse(e.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class, InvalidInputException.class,
            InvalidEventTypeException.class, InvalidReservationStateException.class,
            InvalidLocationIdException.class, ReservationAlreadyCancelledException.class,
            DateTimeException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequestException(RuntimeException e) {
        return responseUtil.buildBadRequestResponse(e.getMessage());
    }

    @ExceptionHandler({
            UserNotLogedInException.class, InvalidAccessException.class
    })
    public ResponseEntity<Map<String, Object>> handleUserNotLoggedException(RuntimeException e) {
        return responseUtil.buildUnauthorized(e.getMessage());
    }

    @ExceptionHandler(StsException.class)
    public ResponseEntity<Map<String, Object>> expiredCredentials(StsException e){
        return responseUtil.buildInternalServerResponse(Map.of("error", true, "message", "aws credentials expired"));
    }
}