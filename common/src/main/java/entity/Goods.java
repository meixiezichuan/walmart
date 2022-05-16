package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    private String id;

    private String name;

    private String description;

    private String image;

    private String userId;

    private String storeId;

    private Boolean deprecated;

    private String userName;

    private String storeName;

}
