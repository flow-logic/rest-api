package com.example.rest_api.controller;

import com.example.rest_api.dao.StudentDAO;
import com.example.rest_api.entity.Student;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class RestStudentController {
    
    private final StudentDAO studentDAO;
    
    @Autowired
    public RestStudentController(StudentDAO studentDAO){
        this.studentDAO = studentDAO;
    }
    
    @GetMapping("/students")
    public List<Student> getStudentList(){
        return studentDAO.getAllStudent();
    }
    
    @PostMapping("/students")
    public Student saveStudent(@RequestBody Student student ){
        this.studentDAO.save(student);
        return student;
    }
}
