/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.rest_api.dao;

import com.example.rest_api.entity.Student;
import java.util.List;

/**
 *
 * @author rglep
 */
public interface StudentDAO {
    void save(Student student);
    List<Student> getAllStudent();    
}
