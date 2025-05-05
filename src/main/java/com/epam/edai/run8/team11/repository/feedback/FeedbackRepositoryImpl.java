package com.epam.edai.run8.team11.repository.feedback;

import com.epam.edai.run8.team11.exception.feedback.FeedbackNotFoundException;
import com.epam.edai.run8.team11.exception.location.LocationNotFoundException;
import com.epam.edai.run8.team11.model.Location;
import com.epam.edai.run8.team11.model.feedback.Feedback;
import com.epam.edai.run8.team11.model.feedback.feedbacktype.FeedbackType;
import com.epam.edai.run8.team11.utils.SortPageFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@Slf4j
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final DynamoDbTable<Feedback> feedbackTable;
    private final DynamoDbTable<Location> locationTable;
    public FeedbackRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.feedbackTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Feedback.class));
        this.locationTable = dynamoDbEnhancedClient.table("locations", TableSchema.fromBean(Location.class));
    }

    public void save(Feedback feedback) {
        feedbackTable.putItem(feedback);
    }

    public Optional<Feedback> findById(String feedbackId) {
        return Optional.ofNullable(feedbackTable.getItem(Key.builder().partitionValue(feedbackId).build()));
    }

    @Override
    public List<Feedback> findAll() {
        return feedbackTable.scan().items().stream().toList();
    }

    @Override
    public Page<Feedback> findByLocationIdWithPagination(String locationId, FeedbackType type, String sortField, Boolean isDescending, Integer size, Integer page) {
        log.info("Received params: locationId: {}, type: {}, sortField: {}, isDescending: {}, size: {}, page: {}",
                locationId, type, sortField, isDescending, size, page);

        Key build = Key.builder().partitionValue(locationId).build();
        Location item = locationTable.getItem(build);
        if(item == null) throw new LocationNotFoundException("Location not found");

        Expression.Builder expressionBuilder = Expression.builder();

        StringBuilder expressionString = new StringBuilder("#locationId = :locationId");
        expressionBuilder.putExpressionName("#locationId", "locationId")
                .putExpressionValue(":locationId", AttributeValue.fromS(locationId));

        if (type != null) {
            expressionString.append(" AND #type = :type");
            expressionBuilder.putExpressionName("#type", "type")
                    .putExpressionValue(":type", AttributeValue.fromS(type.getValue()));
        }

        expressionBuilder.expression(expressionString.toString());
        Expression expression = expressionBuilder.build();

        ScanEnhancedRequest.Builder queryRequestBuilder = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .limit(size);
        SdkIterable<software.amazon.awssdk.enhanced.dynamodb.model.Page<Feedback>> paginatedResults = feedbackTable.scan(queryRequestBuilder.build());

        List<Feedback> feedbacks = paginatedResults.stream()
                .flatMap(p -> p.items().stream())
                .toList();

        Comparator<Feedback> dateComparator = Comparator.comparing(
                Feedback::getDate,
                isDescending ? Comparator.reverseOrder() : Comparator.naturalOrder()
        );
        Comparator<Feedback> ratingComparator = Comparator.comparing(
                Feedback::getRating,
                isDescending ? Comparator.reverseOrder() : Comparator.naturalOrder()
        );

        List<Feedback> filteredList = feedbacks.stream()
                .sorted(SortPageFields.DATE.equals(sortField) ? dateComparator : ratingComparator)
                .skip((long) page * size)
                .limit(size)
                .toList();

        return new PageImpl<>(filteredList, PageRequest.of(page, size), feedbacks.size());
    }

    @Override
    public List<Feedback> findByFeedbackType(FeedbackType type) {
        return findAll().stream()
                .filter(feedback -> feedback.getType().equals(type))
                .toList();
    }

    @Override
    public Feedback updateById(String id, Feedback updatedFeedback) {
        findById(id).orElseThrow(() -> new FeedbackNotFoundException(id));
        updatedFeedback.setFeedbackId(id);
        return feedbackTable.updateItem(updatedFeedback);
    }

    public void deleteById(String feedbackId) {
        feedbackTable.deleteItem(Key.builder().partitionValue(feedbackId).build());
    }
}
