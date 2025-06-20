package com.batch.springbatch.Spring.batch.config;

import com.batch.springbatch.Spring.batch.entity.Employee;
import com.batch.springbatch.Spring.batch.repo.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SppringBatchConfig {

    @Autowired
    EmployeeRepository employeeRepository;

    @Bean
    public FlatFileItemReader<Employee> employeeFlatFileItemReader(){
        FlatFileItemReader reader= new FlatFileItemReader();
        reader.setResource(new FileSystemResource("src/main/resources/employees.csv"));
        reader.setName("reader1");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return  reader;
    }

    private LineMapper<Employee> lineMapper(){
        DefaultLineMapper <Employee> lineMapper=new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "salary");

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }



    @Bean
    public EmployeeProcessor employeeProcessor(){
        return new EmployeeProcessor();
    }

    @Bean
    public RepositoryItemWriter<Employee> writer(){
        RepositoryItemWriter<Employee> writer= new RepositoryItemWriter<>();
        writer.setRepository(employeeRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("step-1",jobRepository)
                . <Employee, Employee>chunk(5,platformTransactionManager)
                .reader(employeeFlatFileItemReader())
                .processor(employeeProcessor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job-builder-1", jobRepository)
                .flow(step1(jobRepository, transactionManager))
                .end()
                .build();
    }
}
