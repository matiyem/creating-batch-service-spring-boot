package batchService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * created by Atiye Mousavi
 * Date: 8/30/2021
 * Time: 2:37 PM
 */
@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    //متد afterJob بعد از کار جاب تمام شد اجرا میشود
    //متد beforJob قبل از اجرای جاب اجرا میشود

    @Override
    public void afterJob(JobExecution jobExecution) {
        //در اینجا وقتی کار جاب تموم میشه اعلام میشه که بعد از میره سراغ insert در دیتابیس
        if (jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("!!! JOB FINISHED! Time to verify the results");
            jdbcTemplate.query("SELECT first_name, last_name FROM people",
                    (rs,row)->new Person(
                            rs.getString(1),
                            rs.getString(2))).forEach(person -> log.info("Found < " + person + "> in the database"));
        }
    }
}
