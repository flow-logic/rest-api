/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.rest_api.dao;

import com.example.rest_api.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author rglep
 */
@Repository
public class StudentDAOImpl implements StudentDAO{
    private EntityManager em;
    
    @Autowired
    public StudentDAOImpl(EntityManager em){
        this.em = em;
    }
    
    @Override
    @Transactional
    public void save(Student student){
        this.em.persist(student);
    }
    
        @Override
        public List<Student> getAllStudent(){
            TypedQuery<Student> query;
            query = em.createQuery("FROM Student", Student.class);
            return query.getResultList();
        }
}
