package com.epam.edai.run8.team11.repository.reservation;

import com.epam.edai.run8.team11.model.reservation.Reservation;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository{

    private final DynamoDbTable<Reservation> reservationTable;

    public ReservationRepositoryImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.reservationTable = dynamoDbEnhancedClient.table("reservations", TableSchema.fromBean(Reservation.class));
    }

    @Override
    public void putReservation(Reservation reservation) {
        reservationTable.putItem(reservation);
    }

    @Override
    public List<Reservation> reservationsForWaiter(String id) {
        return reservationTable.scan().items().stream().filter(reservation -> id.equalsIgnoreCase(reservation.getWaiterId())).collect(Collectors.toList());
    }

    @Override
    public List<Reservation> reservationsForCustomer(String id) {
        return reservationTable.scan().items().stream().filter(reservation -> id.equalsIgnoreCase(reservation.getCustomerId())).collect(Collectors.toList());
    }

    @Override
    public Optional<Reservation> getReservationById(String reservationId) {
        return Optional.ofNullable(reservationTable.getItem(Key.builder().partitionValue(reservationId).build()));
    }

    @Override
    public void updateReservation(Reservation reservation) {
        reservationTable.updateItem(reservation);
    }
}
