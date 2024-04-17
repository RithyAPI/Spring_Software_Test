package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    private StudentService underTest;
    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();
        // then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository)
                .save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void canEditStudent() {
        // given
        Student student = new Student(
                1L,
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(student));

        // when
        underTest.editStudent(student);

        // Assert
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    public void canNotEditStudent() {
        // Arrange
        Student student = new Student(1L, "John Doe", "john@example.com", Gender.MALE);
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> studentService.editStudent(student));
        assertEquals("Student with ID 1 not found.", exception.getMessage());

        // Verify that save method is not called
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());

    }

    @Test
    void canDeleteStudent() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    public void canAddStudents() {
        // Prepare test data
        List<Student> students = new ArrayList<>();
        students.add(new Student("John Doe", "john@example.com", Gender.MALE));
        students.add(new Student("Jane Smith", "jane@example.com", Gender.FEMALE));

        // Call the method under test
        studentService.addStudents(students);

        // Verify that saveAll method of studentRepository was called with the correct argument
        ArgumentCaptor<List<Student>> captor = ArgumentCaptor.forClass(List.class);
        verify(studentRepository, times(1)).saveAll(captor.capture());

        // Assert that the captured argument is equal to the original list of students
        List<Student> capturedStudents = captor.getValue();
        assertThat(capturedStudents).isEqualTo(students);
    }
}