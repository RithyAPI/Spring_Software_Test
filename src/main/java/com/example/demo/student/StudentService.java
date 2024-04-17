package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        Boolean existsEmail = studentRepository
                .selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException(
                    "Email " + student.getEmail() + " taken");
        }

        studentRepository.save(student);
        return student;
    }

    public List<Student> addStudents(List<Student> students) {
        studentRepository.saveAll(students);
        return students;
    }

    public void editStudent(Student student) {
        Student studentObject = studentRepository.findById(student.getId()).orElse(null);
        if(studentObject != null) {
            studentRepository.save(student);
        } else {
            throw new BadRequestException(
                    "Student with ID " + student.getId() + " not found.");
        }

    }

    public void deleteStudent(Long studentId) {
        if(!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(
                    "Student with id " + studentId + " does not exists");
        }
        studentRepository.deleteById(studentId);
    }
}
