package Services;

import com.StudentLibrary.Studentlibrary.Model.Author;
import com.StudentLibrary.Studentlibrary.Repositories.AuthorRepository;
import com.StudentLibrary.Studentlibrary.Services.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAuthor_ShouldSaveAuthor() {
        Author author = new Author();
        author.setId(1);
        author.setName("John Doe");
        authorService.createAuthor(author);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void getAuthors_ShouldReturnListOfAuthors() {

        Author author1 = new Author("El Mehdi El Otmani", "elmehdielotmani1@gmail.com", 21, "Morocco");
        Author author2 = new Author("Otman El Otmani", "otmanelotmani@gmail.com", 22, "Paris");
        List<Author> authors = Arrays.asList(author1, author2);

        when(authorRepository.findAll()).thenReturn(authors);

        // Act
        List<Author> result = authorService.getAuthors();

        // Assert
        assertEquals(2, result.size());
        assertEquals("El Mehdi El Otmani", result.get(0).getName());  // Corrected expected name
        assertEquals("Otman El Otmani", result.get(1).getName());  // Corrected expected name
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void updateAuthor_ShouldUpdateAuthorDetails() {

        Author author = new Author("El Mehdi El Otmani","elmehdielotmani1@gmail.com",21,"Morocco");
        authorService.updateAuthor(author);
        verify(authorRepository, times(1)).updateAuthorDetails(author);
    }

    @Test
    void deleteAuthor_ShouldDeleteAuthorById() {
        int authorId = 1;
        authorService.deleteAuthor(authorId);
        verify(authorRepository, times(1)).deleteCustom(authorId);
    }
}
