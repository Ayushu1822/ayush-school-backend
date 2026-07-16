package com.school.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.management.model.Role;
import com.school.management.model.Student;
import com.school.management.model.User;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.UserRepository;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        // 1. Auto-generate a unique Roll No (e.g., S1001, S1002, etc.)
        String rollNo = "S" + (1000 + studentRepository.count() + 1);
        student.setRollNo(rollNo);

        // 2. Save the Student Record
        Student savedStudent = studentRepository.save(student);

        // 3. Automatically create their User Portal Account in the database
        User portalUser = new User();
        portalUser.setUsername(rollNo); // Roll No serves as their login ID
        portalUser.setPassword(passwordEncoder.encode("student123")); // Default password
        portalUser.setRole(Role.ROLE_STUDENT);
        userRepository.save(portalUser);

        return savedStudent;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        student.setFirstName(studentDetails.getFirstName());
        student.setLastName(studentDetails.getLastName());
        student.setEmail(studentDetails.getEmail());
        student.setEnrollmentDate(studentDetails.getEnrollmentDate());

        Student updatedStudent = studentRepository.save(student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // Delete the student record
        studentRepository.delete(student);

        // Delete their corresponding portal user account so they can no longer log in
        userRepository.findByUsername(student.getRollNo()).ifPresent(user -> userRepository.delete(user));

        return ResponseEntity.ok("Student deleted successfully!");
    }
}