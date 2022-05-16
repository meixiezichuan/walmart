package entity;

import lombok.Data;

/**
 * @Author: zhouzhong
 * @Email: 21212010059@m.fudan.edu.cn
 * @Date: 2021/11/28 19:32
 * @Description:
 */
@Data
public class Notification {
    private String id;

    private String sender;

    private String receiver;

    private String content;

    private String createTime;

    private int isRead;
}