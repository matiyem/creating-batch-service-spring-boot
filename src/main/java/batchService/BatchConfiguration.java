package batchService;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;


import javax.sql.DataSource;

/**
 * created by Atiye Mousavi
 * Date: 8/29/2021
 * Time: 3:56 PM
 */
@Configuration
@EnableBatchProcessing//این annotation بساری از bean های ضروری را اضافه میکند که از جاب ها پشتیبانی میکند
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Person> reader(){
        return new FlatFileItemReaderBuilder<Person>()
                .name("personFileItrmReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName","lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor processor(){
        return new PersonItemProcessor();
    }
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>())
                .sql("insert into people(first_name,last_name) values (:firstName,:lastName)")
                .dataSource(dataSource).build();
    }
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        //این متد برای ساخت یک جاب است
        //در این تعریف شما به یک incrementer نیاز دارید زیرا جاب از پایگاه داده برای حفظ وضعیت اجرا استفاده میکنند.
        //سپس هر مرحله لیست میشوددر اینجا فقط یک مرحله دارد
        //کار تمام میشود و جاوا یک کار کاملا configure شده تولید میکند
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }
    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer){
        //این متد برای ساخت یک step است.جاب ها براساس step ها ساخته میشوند جایی که هر step میتواند شامل reader processor و writer است
        return stepBuilderFactory.get("step1")
                //همچنین chunk یک جنریک متد است برای همین تایپ آن را یعنی person را قبل از chunk قرار میدهیم
                .<Person,Person> chunk(10)//در اینجا مشخص میکنیم که هم زمان میتوان 10 رکورد را هم زمان وارد دیتابیس کرد
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}
