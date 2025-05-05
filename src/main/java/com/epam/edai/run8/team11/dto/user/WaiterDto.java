package com.epam.edai.run8.team11.dto.user;

import com.epam.edai.run8.team11.model.user.User;
import lombok.AllArgsConstructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class WaiterDto extends User {
    private Map<String, List<String>> slots;
    private String locationId;
    private Integer count;

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", slots=" + slots +
                ", locationId='" + locationId + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ", role=" + this.getRole() +
                ", userId='" + this.getUserId() + '\'' +
                '}';
    }
}
