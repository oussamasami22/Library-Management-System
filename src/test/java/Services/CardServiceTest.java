package Services;

import com.StudentLibrary.Studentlibrary.Model.Card;
import com.StudentLibrary.Studentlibrary.Model.CardStatus;
import com.StudentLibrary.Studentlibrary.Model.Student;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import com.StudentLibrary.Studentlibrary.Services.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCard_ShouldCreateAndSaveCard() {
        // Arrange
        Student student = new Student();
        student.setId(1);
        student.setName("John Doe");

        Card expectedCard = new Card();
        expectedCard.setStudent(student);
        student.setCard(expectedCard);

        when(cardRepository.save(any(Card.class))).thenReturn(expectedCard);

        // Act
        Card result = cardService.createCard(student);

        // Assert
        assertNotNull(result);
        assertEquals(student, result.getStudent());
        verify(cardRepository, times(1)).save(result);
    }

    @Test
    void deactivate_ShouldDeactivateCard() {
        // Arrange
        int studentId = 1;
        String status = CardStatus.DEACTIVATED.toString();

        // Act
        cardService.deactivate(studentId);

        // Assert
        verify(cardRepository, times(1)).deactivateCard(studentId, status);
    }
}
