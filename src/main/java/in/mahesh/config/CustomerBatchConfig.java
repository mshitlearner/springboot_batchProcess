package in.mahesh.config;

import java.security.Policy;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import in.mahesh.entity.Customer;
import in.mahesh.listener.CustomLogExceptionListener;
import in.mahesh.policies.CustomSkipPolicy;
import in.mahesh.processor.CustomerProcessor;
import in.mahesh.respository.CustomerRepository;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class CustomerBatchConfig {

	private CustomerRepository customerRepository;

	// Item Reader Process
	@Bean
	@StepScope
	public FlatFileItemReader<Customer> reader(@Value("#{jobParameters[fileName]}") String pathToFIle) {
		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<Customer>();
		itemReader.setResource(new FileSystemResource(pathToFIle));
		itemReader.setName("csvItemReader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());
		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {
		DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<Customer>();

		// Reading the from CSV file which are comma seperated fields
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("index", "customer_id", "firstName", "lastName", "company", "city", "country", "phone_1",
				"phone_2", "email", "subscriptionDate", "website");

		// Mapp the CSV file to the Customer Object
		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<Customer>();
		fieldSetMapper.setTargetType(Customer.class);

		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		return defaultLineMapper;
	}

	// ItemProcess
	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

	// ItemWriter
	@Bean
	public RepositoryItemWriter<Customer> writer() {
		RepositoryItemWriter<Customer> repWriter = new RepositoryItemWriter<Customer>();
		repWriter.setRepository(customerRepository);
		repWriter.setMethodName("save");
		return repWriter;
	}

	// step
	@Bean
	public Step step1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, FlatFileItemReader<Customer> reader) {
		return new StepBuilder("csvStep", jobRepository)
				.<Customer, Customer> chunk(10, platformTransactionManager)
				.reader(reader)
				.processor(processor())
				.writer(writer())
				.faultTolerant()
				.listener(skipListener())
				.skipLimit(1000)
				.skipPolicy(skipPolicy())
				.noRollback(RuntimeException.class)
				.noRollback(IllegalArgumentException.class)
				.noRollback(NullPointerException.class)
				.taskExecutor(taskExecutor())
				.build();
	}

	// Job
	@Bean
	public Job runJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager,
			FlatFileItemReader<Customer> reader) {
		return new JobBuilder("csvJob", jobRepository).flow(step1(jobRepository, platformTransactionManager, reader))
				.end().build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
		asyncTaskExecutor.setConcurrencyLimit(10);
		return asyncTaskExecutor;
	}

	@Bean
	public SkipPolicy skipPolicy() {
		return new CustomSkipPolicy();
	}

	@Bean
	public SkipListener<Customer, Number> skipListener() {
		return new CustomLogExceptionListener();
	}

}
