package in.mshitlearner.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
	private Job job;
	
	private final String TEMP_STORAGE = "D:\\BATCH_FILES\\";
	
	@PostMapping(value = "/import")
	public void importCustomers(@RequestParam("file") MultipartFile multipartFile) {

		try {
			
			String originalFileName = multipartFile.getOriginalFilename();
			String fileName = TEMP_STORAGE + originalFileName;
            File fileToImport = new File(fileName);
            multipartFile.transferTo(fileToImport);
            
			JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
			jobParametersBuilder.addLong("startAt", System.currentTimeMillis());
			jobParametersBuilder.addString("fileName", fileName);
			jobLauncher.run(job, jobParametersBuilder.toJobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException | IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
