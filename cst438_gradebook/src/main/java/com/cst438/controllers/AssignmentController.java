package com.cst438.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.GradebookDTO;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"})
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;

	@GetMapping("/assignments")
	public AssignmentListDTO getAssignmentsNeedGrading( ) {
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		
		List<Assignment> assignments = assignmentRepository.findNeedGradingByEmail(email);
		AssignmentListDTO result = new AssignmentListDTO();
		for (Assignment a: assignments) {
			result.assignments.add(new AssignmentListDTO.AssignmentDTO(a.getId(), a.getCourse().getCourse_id(), a.getName(), a.getDueDate().toString() , a.getCourse().getTitle()));
		}
		return result;
	}
	
	@PostMapping("/assignments")
	@Transactional
	public void createAssignment (@RequestParam String name, @RequestParam Date dueDate, @RequestParam Course courseID) {
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		
		if(email == "dwisneski@csumb.edu") {
			//Create new assignment object
			Assignment assignment = new Assignment();

			//Set information, name, due date, courseID
			assignment.setName(name);
			assignment.setDueDate(dueDate);
			assignment.setCourse(courseID);
			assignmentRepository.save(assignment);
			}
			else {
				throw new ResponseStatusException( HttpStatus.UNAUTHORIZED, "Not Authorized. " );
			}
		
	}
	
	@PutMapping("/assignments/{assignmentId}")
	@Transactional
	public ResponseEntity<Void> updateAssignmentName (@PathVariable int assignmentId, @RequestParam String name) {
			
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
			
		if(email.equals("dwisneski@csumb.edu")) {
			
			// get assignment 
			Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
				
			if(assignmentOpt.isPresent()) {
				Assignment assignment = assignmentOpt.get();
				//Update assignment name and save record
				assignment.setName(name);
				assignmentRepository.save(assignment);
				return ResponseEntity.noContent().build();
			}
			else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
			}
			
		}
		else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized");
		}
	}
	
	

	@DeleteMapping("/assignment/{assignmentId}")
    @Transactional
    public void deleteAssignment(@PathVariable int assignmentId) {
		
		String email = "dwisneski@csumb.edu";  // user name (should be instructor's email)
		
		if(email.equals("dwisneski@csumb.edu")) {
			//Find assignment by ID
			Assignment assignment = checkAssignment(assignmentId);
			
			if(assignment.getNeedsGrading() == 0) {
				assignmentRepository.delete(assignment);
			} else {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Error, assignments with existing grades");
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
