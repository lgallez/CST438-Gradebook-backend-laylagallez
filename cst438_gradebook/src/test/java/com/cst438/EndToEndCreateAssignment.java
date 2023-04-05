package com.cst438;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class EndToEndCreateAssignment {
	public static final String CHROME_DRIVER_FILE_LOCATION = "/usr/local/bin/chromedriver";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	
	public static final String TEST_ASSIGNMENT_NAME = "Test Quiz";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_ASSIGNMENT_DATE = "10-7-2022";
	public static final String TEST_COURSE_ID = "40443";
	public static final int TEST_ASSIGNMENT_ID = 1;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void createAssignmentTest() throws Exception {
		 
		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

        ChromeOptions options = new ChromeOptions();
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		options.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);

		try {
			
			// Find the button element by class name
			WebElement we = driver.findElement(By.className("MuiButtonBase-root MuiButton-root MuiButton-outlined makeStyles-button-3 MuiButton-outlinedSecondary"));

			// Click on the button element			
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Enter assignmentName, courseID and dueDate
			we = driver.findElement(By.xpath("//input[@name='courseID']"));
			we.sendKeys(TEST_COURSE_ID);
			we = driver.findElement(By.xpath("//input[@name='assignmentName']"));
			we.sendKeys(TEST_ASSIGNMENT_NAME);
			we = driver.findElement(By.xpath("//input[@name='dueDate']"));
			we.sendKeys(TEST_ASSIGNMENT_DATE);
			
			// Submit
			driver.findElement(By.xpath("//input[@name='handleSubmit']")).click();
			Thread.sleep(SLEEP_DURATION);

			// Verify that the assignment has been added
			Assignment a = assignmentRepository.findByCourseIdAndName(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			assertNotNull(a); 
			if(a !=null)
				assignmentRepository.delete(a);

		} catch (Exception ex) {
			throw ex;
		} finally {
			Assignment a = assignmentRepository.findByCourseIdAndName(TEST_COURSE_ID, TEST_ASSIGNMENT_NAME);
			
			if(a !=null)
				assignmentRepository.delete(a);
	         
			driver.quit();
		}

	}
}