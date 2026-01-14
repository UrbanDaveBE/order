package local.dev.order.service;

import local.dev.order.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest(properties = "catalog.base.url=http://test-catalog")
@AutoConfigureMockRestServiceServer
class BookCatalogServiceTest {

    @Autowired
    private BookCatalogService service;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void shouldReturnFallbackWhenCatalogIsDown() {
        // GIVEN: Server wirft 500 Fehler
        // Wir erwarten hier ggf. 3 Aufrufe (wegen Retry), aber für den Mock reicht oft "expect(manyTimes)" oder einfach so.
        // Da Resilience4j erst retried und dann den Fallback ruft, feuert er mehrmals.
        // Wir nutzen 'expect', was standardmässig "einmal oder mehrmals" in diesem Kontext ok ist,
        // oder wir definieren nichts Spezifisches zur Anzahl, hauptsache er antwortet.
        server.expect(requestTo("http://test-catalog/api/books"))
                .andRespond(withServerError());

        // Resilience4j wird jetzt im Hintergrund retrien...
        // ... und da der Server immer 500 liefert, greift am Ende der Fallback.

        // WHEN
        List<Book> result = service.getAllBooks();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsbn()).isEqualTo("000-FALLBACK");
    }

    @Test
    void shouldSuccessfullyFindBookByIsbn() {
        String jsonResponse = """
                {
                    "isbn": "123", 
                    "title": "Test Buch", 
                    "author": "Dave"
                }
                """;

        server.expect(requestTo("http://test-catalog/api/books/123"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Book book = service.findBookByIsbn("123");

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Test Buch");
    }
}