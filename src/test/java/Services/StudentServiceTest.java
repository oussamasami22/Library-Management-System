package Services;

import com.StudentLibrary.Studentlibrary.Model.Card;
import com.StudentLibrary.Studentlibrary.Model.Student;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import com.StudentLibrary.Studentlibrary.Repositories.StudentRepository;
import com.StudentLibrary.Studentlibrary.Services.CardService;
import com.StudentLibrary.Studentlibrary.Services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createStudent_ShouldCreateCardAndLogInfo() {
        // Arrange
        Student student = new Student();
        student.setId(1);
        student.setName("John Doe");

        Card card = new Card();
        card.setId(101);
        card.setStudent(student);

        when(cardService.createCard(student)).thenReturn(card);

        // Act
        studentService.createStudent(student);

        // Assert
        verify(cardService, times(1)).createCard(student);
        verify(cardRepository, never()).save(any(Card.class)); // Card saving is handled by `CardService`
    }

    @Test
    void updateStudent_ShouldReturnUpdateCount() {
        // Arrange
        Student student = new Student();
        student.setId(1);
        student.setName("Jane Doe");

        when(studentRepository.updateStudentDetails(student)).thenReturn(1);

        // Act
        int result = studentService.updateStudent(student);

        // Assert
        assertEquals(1, result);
        verify(studentRepository, times(1)).updateStudentDetails(student);
    }

    @Test
    void deleteStudent_ShouldDeactivateCardAndDeleteStudent() {
        // Arrange
        int studentId = 1;

        // Act
        studentService.deleteStudent(studentId);

        // Assert
        verify(cardService, times(1)).deactivate(studentId);
        verify(studentRepository, times(1)).deleteCustom(studentId);
    }
}
