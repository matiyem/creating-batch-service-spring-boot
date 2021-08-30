package batchService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by Atiye Mousavi
 * Date: 8/29/2021
 * Time: 3:14 PM
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Person {

    private String lastName;
    private String firstName;

    @Override
    public String toString() {
        return "firstName: " + firstName + ", lastName: " + lastName;
    }
}
