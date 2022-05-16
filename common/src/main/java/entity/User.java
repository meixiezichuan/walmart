package entity;

import lombok.Data;

@Data
public class User {
    public static int admin_role = 1;
    public static int seller_role = 2;
    public static int customer_role = 3;
    private String id;
    private String name;
    private String pwd;
    private int role;
    private String email;
    private String phone_num;
    private String address;
}
