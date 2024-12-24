package Services;

import com.StudentLibrary.Studentlibrary.Model.Book;
import com.StudentLibrary.Studentlibrary.Repositories.BookRepository;
import com.StudentLibrary.Studentlibrary.Services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBook() {
        Book book = new Book();
        book.setId(1);
        book.setName("Test Book");

        bookService.createBook(book);

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testGetBooksByGenreAndAuthor() {
        String genre = "Fiction";
        String author = "John Doe";
        boolean isAvailable = true;

        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1);
        book.setName("Test Book");
        books.add(book);

        when(bookRepository.findBooksByGenre_Author(genre, author, isAvailable)).thenReturn(books);

        List<Book> result = bookService.getBooks(genre, isAvailable, author);

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getName());
        verify(bookRepository, times(1)).findBooksByGenre_Author(genre, author, isAvailable);
    }

    @Test
    void testGetBooksByGenre() {
        String genre = "Fiction";
        boolean isAvailable = true;

        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1);
        book.setName("Genre Test Book");
        books.add(book);

        when(bookRepository.findBooksByGenre(genre, isAvailable)).thenReturn(books);

        List<Book> result = bookService.getBooks(genre, isAvailable, null);

        assertEquals(1, result.size());
        assertEquals("Genre Test Book", result.get(0).getName());
        verify(bookRepository, times(1)).findBooksByGenre(genre, isAvailable);
    }

    @Test
    void testGetBooksByAuthor() {
        String author = "Jane Doe";
        boolean isAvailable = false;

        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1);
        book.setName("Author Test Book");
        books.add(book);

        when(bookRepository.findBooksByAuthor(author, isAvailable)).thenReturn(books);

        List<Book> result = bookService.getBooks(null, isAvailable, author);

        assertEquals(1, result.size());
        assertEquals("Author Test Book", result.get(0).getName());
        verify(bookRepository, times(1)).findBooksByAuthor(author, isAvailable);
    }

    @Test
    void testGetBooksByAvailability() {
        boolean isAvailable = true;

        List<Book> books = new ArrayList<>();
        Book book = new Book();
        book.setId(1);
        book.setName("Available Test Book");
        books.add(book);

        when(bookRepository.findBooksByAvailability(isAvailable)).thenReturn(books);

        List<Book> result = bookService.getBooks(null, isAvailable, null);

        assertEquals(1, result.size());
        assertEquals("Available Test Book", result.get(0).getName());
        verify(bookRepository, times(1)).findBooksByAvailability(isAvailable);
    }
}
