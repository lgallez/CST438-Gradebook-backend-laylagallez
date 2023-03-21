package com.cst438.controllers;

import java.sql.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"})
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	@PostMapping("/assignments/new")
	@Transactional
	public void createAssignment(@RequestParam("id") int courseId, @RequestParam("name") String name, @RequestParam("due") String date) {
		Course course = courseRepository.findById(courseId).orElse(null);
		
		if(course != null) {
			Assignment assignment = new Assignment();
			assignment.setDueDate(Date.valueOf(date));
			assignment.setName(name);
			assignment.setNeedsGrading(1);
			assignment.setCourse(course);
			assignmentRepository.save(assignment);
		}
		else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment cannot be created for this course. " + courseId);
		}
		
	}
	
	@PutMapping("/assignments/update/{assignmentId}")
	@Transactional
	public void updateAssignmentName(@RequestParam("id") Integer assignmentId, @RequestParam("name") String name) {
		Assignment assignment = assignmentRepository.findById(assignmentId).orElse(null); 
		if(assignment !=null) {
			assignment.setName(name);
			assignmentRepository.save(assignment);
		}
		else {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Cannot Update Assignemnt" + assignmentId);
		}
		
	}
	
	

	@DeleteMapping("/assignment/delete/{assignmentId}")
    @Transactional
    public void deleteAssignment(@PathVariable int assignmentId) {
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		
		if(email.equals("dwisneski@csumb.edu")) {
			//Find assignment by ID
			Assignment assignment = checkAssignment(assignmentId);
			
			if(assignment.getNeedsGrading() == 0) {
				assignmentRepository.delete(assignment);
			} else {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Error, assignments with existing grades");
			}	
		}
	}
	
	private Assignment checkAssignment(int assignmentId) {
		// get assignment 
		Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
		if(!assignment.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
		}
		return null;
		
		
	}

	
}
