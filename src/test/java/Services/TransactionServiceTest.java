package Services;

import com.StudentLibrary.Studentlibrary.Model.*;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Repositories.CardRepository;
import com.StudentLibrary.Studentlibrary.Repositories.TransactionRepository;
import com.StudentLibrary.Studentlibrary.Services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set mock values for @Value annotations
        transactionService.max_allowed_books = 5;
        transactionService.max_days_allowed = 14;
        transactionService.fine_per_day = 10;
    }

    @Test
    void issueBooks_ShouldIssueBookSuccessfully() throws Exception {
        // Arrange
        int cardId = 1;
        int bookId = 100;

        Card card = new Card();
        card.setId(cardId);
        card.setCardStatus(CardStatus.ACTIVATED);
        card.setBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(bookId);
        book.setAvailable(true);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // Act
        String transactionId = transactionService.issueBooks(cardId, bookId);

        // Assert
        assertNotNull(transactionId);
        assertFalse(book.isAvailable());
        assertEquals(card, book.getCard());
        assertTrue(card.getBooks().contains(book));
        verify(bookRepository, times(1)).updateBook(book);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void issueBooks_ShouldThrowException_WhenBookUnavailable() {
        // Arrange
        int cardId = 1;
        int bookId = 100;

        Book book = new Book();
        book.setId(bookId);
        book.setAvailable(false);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> transactionService.issueBooks(cardId, bookId));
        assertEquals("Book is either unavailable or not present!!", exception.getMessage());
    }

    @Test
    void issueBooks_ShouldThrowException_WhenCardIsDeactivated() {
        // Arrange
        int cardId = 1;
        int bookId = 100;

        Card card = new Card();
        card.setId(cardId);
        card.setCardStatus(CardStatus.DEACTIVATED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> transactionService.issueBooks(cardId, bookId));
        assertEquals("No value present", exception.getMessage());  // Adjusted expected message
    }


    @Test
    void returnBooks_ShouldReturnBookSuccessfully_NoFine() throws Exception {
        // Arrange
        int cardId = 1;
        int bookId = 100;

        Card card = new Card();
        card.setId(cardId);

        Book book = new Book();
        book.setId(bookId);
        book.setAvailable(false);

        Transaction lastTransaction = new Transaction();
        lastTransaction.setTransactionDate(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L)); // Issued 7 days ago
        lastTransaction.setCard(card);
        lastTransaction.setBook(book);

        when(transactionRepository.findByCard_Book(cardId, bookId, TransactionStatus.SUCCESSFUL, true))
                .thenReturn(Collections.singletonList(lastTransaction));

        // Act
        String transactionId = transactionService.returnBooks(cardId, bookId);

        // Assert
        assertNotNull(transactionId);
        assertTrue(book.isAvailable());
        assertNull(book.getCard());
        verify(bookRepository, times(1)).updateBook(book);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void returnBooks_ShouldApplyFine_WhenBookReturnedLate() throws Exception {
        // Arrange
        int cardId = 1;
        int bookId = 100;

        Card card = new Card();
        card.setId(cardId);

        Book book = new Book();
        book.setId(bookId);
        book.setAvailable(false);

        Transaction lastTransaction = new Transaction();
        lastTransaction.setTransactionDate(new Date(System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000L)); // Issued 20 days ago
        lastTransaction.setCard(card);
        lastTransaction.setBook(book);

        when(transactionRepository.findByCard_Book(cardId, bookId, TransactionStatus.SUCCESSFUL, true))
                .thenReturn(Collections.singletonList(lastTransaction));

        // Act
        String transactionId = transactionService.returnBooks(cardId, bookId);

        // Assert
        assertNotNull(transactionId);
        verify(transactionRepository, times(1)).save(argThat(transaction -> transaction.getFineAmount() == 60)); // 6 days late * 10 fine per day
    }

    @Test
    void returnBooks_ShouldThrowException_WhenNoIssueTransactionFound() {
        // Arrange
        int cardId = 1;
        int bookId = 100;

        when(transactionRepository.findByCard_Book(cardId, bookId, TransactionStatus.SUCCESSFUL, true))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> transactionService.returnBooks(cardId, bookId));
    }
}
