package com.epam.edai.run8.team11.exception;

import com.epam.edai.run8.team11.exception.access.InvalidAccessException;
import com.epam.edai.run8.team11.exception.dish.DishNotFoundException;
import com.epam.edai.run8.team11.exception.feedback.FeedbackNotFoundException;
import com.epam.edai.run8.team11.exception.feedback.InvalidReservationStateException;
import com.epam.edai.run8.team11.exception.location.InvalidLocationIdException;
import com.epam.edai.run8.team11.exception.location.LocationNotFoundException;
import com.epam.edai.run8.team11.exception.report.InvalidEventTypeException;
import com.epam.edai.run8.team11.exception.reservation.NoUpdateRequiredException;
import com.epam.edai.run8.team11.exception.reservation.ReservationAlreadyCancelledException;
import com.epam.edai.run8.team11.exception.reservation.ReservationNotFoundException;
import com.epam.edai.run8.team11.exception.table.SlotAlreadyBookedException;
import com.epam.edai.run8.team11.exception.table.TableNotFoundException;
import com.epam.edai.run8.team11.exception.user.UserAlreadyExistsException;
import com.epam.edai.run8.team11.exception.user.UserNotFoundException;
import com.epam.edai.run8.team11.exception.user.UserNotLoggedInException;
import com.epam.edai.run8.team11.exception.waiter.WaiterAlreadyExistsException;
import com.epam.edai.run8.team11.exception.waiter.WaiterNotFoundException;
import com.epam.edai.run8.team11.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import software.amazon.awssdk.services.sts.model.StsException;

import java.time.DateTimeException;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ResponseUtil responseUtil;

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(NoResourceFoundException e) {
        return responseUtil.buildNotFound(e.getMessage());
    }

    @ExceptionHandler({
            FeedbackNotFoundException.class, UserNotFoundException.class,
            LocationNotFoundException.class, WaiterNotFoundException.class,
            ReservationNotFoundException.class, DishNotFoundException.class,
            TableNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(RuntimeException e) {
        return responseUtil.buildNotFound(e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException e) {
        return responseUtil.buildUnauthorized(e.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String,Object>> handleBadRequest(NumberFormatException e)
    {
        return responseUtil.buildBadRequestResponse("Invalid input");
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
    })
    public ResponseEntity<Map<String, Object>> handleBadRequestException(RuntimeException e) {
        return responseUtil.buildBadRequestResponse(e.getMessage());
    }

    @ExceptionHandler({
            UserNotLoggedInException.class, InvalidAccessException.class
    })
    public ResponseEntity<Map<String, Object>> handleUserNotLoggedException(RuntimeException e) {
        return responseUtil.buildUnauthorized(e.getMessage());
    }

    @ExceptionHandler({StsException.class, InternalError.class})
    public ResponseEntity<Map<String, Object>> expiredCredentials(Exception e){
        return responseUtil.buildInternalServerResponse(Map.of("error", true, "message", e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException e) {
        return responseUtil.buildBadRequestResponse(e.getParameterName()+" can't be empty");
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<Map<String,Object>> handleDateTimeException(DateTimeException e)
    {
        return responseUtil.buildBadRequestResponse("Invalid input for date or time");
    }
}