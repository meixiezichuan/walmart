package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response<T> implements Serializable {
    /**
     * 1 true, 0 false
     */
    Integer status;

    String detail;
    T data;
}
