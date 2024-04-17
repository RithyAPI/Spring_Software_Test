package com.example.demo.integration;

import com.example.demo.student.Gender;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.example.demo.student.StudentService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@TestPropertySource(
		locations = "classpath:application.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private StudentService studentService;

	@Autowired
	private StudentRepository studentRepository;

	private final Faker faker = new Faker();

	@Test
	public void getAllStudents() throws Exception {
		// Arrange
		List<Student> students = Arrays.asList(
				new Student(1L, "John Doe", "john@example.com", Gender.MALE),
				new Student(2L, "Jane Doe", "jane@example.com", Gender.FEMALE)
		);
		when(studentService.getAllStudents()).thenReturn(students);

		// Act and Assert
		mockMvc.perform(get("/api/v1/students")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("John Doe"))
				.andExpect(jsonPath("$[0].email").value("john@example.com"))
				.andExpect(jsonPath("$[0].gender").value("MALE"))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].name").value("Jane Doe"))
				.andExpect(jsonPath("$[1].email").value("jane@example.com"))
				.andExpect(jsonPath("$[1].gender").value("FEMALE"));
	}

	@Test
	public void createStudent() throws Exception {
		// Arrange
		Student newStudent = new Student("John Doe", "jamila@gmail.com", Gender.MALE);
		Student savedStudent = new Student(1L, "John Doe", "jamila@gmail.com", Gender.MALE);
		when(studentService.addStudent(any(Student.class))).thenReturn(savedStudent);

		// Act and Assert
		mockMvc.perform(post("/api/v1/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"John Doe\",\"email\":\"jamila@gmail.com\",\"gender\":\"MALE\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("John Doe"))
				.andExpect(jsonPath("$.email").value("jamila@gmail.com"))
				.andExpect(jsonPath("$.gender").value("MALE"));

		// Assert
		verify(studentService, times(1)).addStudent(newStudent);
	}


	@Test
	public void updateStudent() throws Exception {
		// Arrange
		Student updatedStudent = new Student(1L, "Yan Rithy", "yanrithy12357@gmail.com", Gender.MALE);

		// Act
		mockMvc.perform(put("/api/v1/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedStudent)))
				.andExpect(status().isOk());

		// Assert
		verify(studentService, times(1)).editStudent(updatedStudent);
	}

	@Test
	public void deleteStudent() throws Exception {
		// Arrange

		// Act and Assert
		mockMvc.perform(delete("/api/v1/students/{id}", 1L)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		// Assert
		verify(studentService, times(1)).deleteStudent(1L);
	}

	@Test
	public void canSaveMultipleStudents() throws Exception {
		// Mock the behavior of the student service
		when(studentService.addStudents(anyList())).thenReturn(createSampleStudents());

		// Perform the request and verify the response
		mockMvc.perform(post("/api/v1/students/all")
						.contentType(MediaType.APPLICATION_JSON)
						.content("[{\"name\":\"John Doe\",\"email\":\"991john@gmail.com\",\"gender\":\"MALE\"},{\"name\":\"John Doe\",\"email\":\"991john@gmail.com\",\"gender\":\"MALE\"},{\"name\":\"John Doe\",\"email\":\"991john@gmail.com\",\"gender\":\"MALE\"}]"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].name").value("John Doe"))
				.andExpect(jsonPath("$[0].email").value("991john@gmail.com"))
				.andExpect(jsonPath("$[0].gender").value("MALE"))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].name").value("John Doe"))
				.andExpect(jsonPath("$[1].email").value("991john@gmail.com"))
				.andExpect(jsonPath("$[1].gender").value("MALE"))
				.andExpect(jsonPath("$[2].id").value(3))
				.andExpect(jsonPath("$[2].name").value("John Doe"))
				.andExpect(jsonPath("$[2].email").value("991john@gmail.com"))
				.andExpect(jsonPath("$[2].gender").value("MALE"));

		// Verify that the service method was called with the correct arguments
		verify(studentService, times(1)).addStudents(anyList());
	}

	// Helper method to create sample students for mocking service behavior
	private List<Student> createSampleStudents() {
		List<Student> students = new ArrayList<>();
		students.add(new Student(1L, "John Doe", "991john@gmail.com", Gender.MALE));
		students.add(new Student(2L, "John Doe", "991john@gmail.com", Gender.MALE));
		students.add(new Student(3L, "John Doe", "991john@gmail.com", Gender.MALE));
		return students;
	}

}
