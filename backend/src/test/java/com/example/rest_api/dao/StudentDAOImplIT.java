package com.example.rest_api.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.rest_api.entity.Student;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudentDAOImplIT {
    
 @Mock
    private EntityManager em; // Creamos el mock del EntityManager

    @InjectMocks
    private StudentDAOImpl studentDAO; // Inyecta el mock anterior en el constructor de la clase

    @Test
    void testSave() {
        // Arrange
        Student student = new Student(); // Asegúrate de tener tu entidad configurada
        student.setId(1);
        student.setFirstName("Tito");
        student.setLastName("Moscas");

        // Act
        studentDAO.save(student);

        // Assert: Verificamos que el método persist fue llamado exactamente 1 vez con el objeto student
        verify(em, times(1)).persist(student);
    }

    @Test
    void testGetAllStudent() {
        // Arrange
        // Como createQuery devuelve un TypedQuery, también debemos mockearlo
        TypedQuery<Student> queryMock = mock(TypedQuery.class);
        List<Student> expectedStudents = Arrays.asList(new Student(), new Student());

        when(em.createQuery("FROM Student", Student.class)).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(expectedStudents);

        // Act
        List<Student> actualStudents = studentDAO.getAllStudent();

        // Assert
        assertNotNull(actualStudents);
        assertEquals(2, actualStudents.size());

        // Verificamos que las interacciones ocurrieron como esperábamos
        verify(em, times(1)).createQuery("FROM Student", Student.class);
        verify(queryMock, times(1)).getResultList();
    }
}