package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class Store {

    private String id;

    private String name;

    private String description;

    private String userId;

    private String createTime;

    private int grade;

    private String status;
}
