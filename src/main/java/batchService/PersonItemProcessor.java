package batchService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Locale;


/**
 * created by Atiye Mousavi
 * Date: 8/29/2021
 * Time: 3:17 PM
 */

@Slf4j
public class PersonItemProcessor implements ItemProcessor<Person,Person> {
    //برای جابجایی آیتم از آن استفاده میشود


    @Override
    public Person process(final Person person) throws Exception {
        final String firstName= person.getFirstName().toUpperCase();
        final String lastName=person.getLastName().toUpperCase();
        final Person transformedPerson=new Person(firstName,lastName);

        log.info("Converting (" + person + ") into (" + transformedPerson + ")");
        return transformedPerson;
    }
}
