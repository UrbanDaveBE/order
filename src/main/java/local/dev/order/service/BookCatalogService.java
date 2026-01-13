package local.dev.order.service;

import io.github.resilience4j.retry.annotation.Retry;
import local.dev.order.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BookCatalogService {

    // Durch docker ersetzen whoop whooop
    //private static final String CATALOG_BASE_URL = "http://localhost:8080";

    //public BookCatalogService() {
    //    this.restClient =  RestClient.builder()
    //            .baseUrl(CATALOG_BASE_URL)
    //            .build();
    //}

    private final RestClient restClient;
    /**
     * Konstruktor, der die Basis-URL über @Value injiziert.
     * Der Wert wird aus application.properties oder der Docker Compose Environment gelesen.
     */
    public BookCatalogService(RestClient.Builder builder,
                              @Value("${catalog.base.url}") String catalogBaseUrl) {
        this.restClient = builder
                .baseUrl(catalogBaseUrl)
                .build();
    }

    @Retry(name = "catalogService", fallbackMethod = "fallbackSearchBooks")
    public List<Book> searchBooks(List<String> keywords) {

        StringBuilder queryParams = new StringBuilder();
        for (String keyword : keywords) {
            queryParams.append("query=").append(keyword).append("&"); // für jeden suchbegriff
        }

        // letztes "&" entfernen
        if (queryParams.length() > 0) {
            queryParams.deleteCharAt(queryParams.length() - 1);
        }

        // Die vollständige URI zusammenbauen: Pfad + "?" + Parameter-String
        String fullUri = "/api/books/search?" + queryParams.toString();

        // DEBUG: Fügen Sie hier einen Print hinzu, um die gesendete URL zu überprüfen!
        // System.out.println("DEBUG: Sending to: " + restClient.getUriBuilder().build().toUriString() + fullUri);

        return restClient.get()
                .uri(fullUri)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Book>>() {});
    }

    public Book findBookByIsbn(String isbn) {
        return restClient.get()
                .uri("/api/books/{isbn}",isbn)
                .retrieve()
                .body(Book.class);
    }
    @Retry(name = "catalogService", fallbackMethod = "fallbackGetAllBooks")
    public List<Book> getAllBooks() {
        return restClient.get()
                .uri("/api/books")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Book>>() {});
    }

    // Fallback Methoden
    public List<Book> fallbackSearchBooks(List<String> keywords, Throwable t) {
        System.err.println("FALLBACK searchBooks ausgelöst durch: " + t.getMessage());
        // Wir geben eine leere Liste zurück, damit die Webseite nicht abstürzt
        return Collections.emptyList();
    }

    public List<Book> fallbackGetAllBooks(Throwable t) {
        System.err.println("FALLBACK getAllBooks ausgelöst durch: " + t.getMessage());

        // Damit du auf der Webseite siehst, dass der Fallback greift, bauen wir ein "Dummy Buch"
        List<Book> fallbackList = new ArrayList<>();

        // Achtung: Passe den Konstruktor an dein Book-Model an!
        // Ich nehme an: ISBN, Title, Author, Price
        Book errorBook = new Book();
        errorBook.setIsbn("000-FALLBACK");
        errorBook.setTitle("Der Catalog Service ist nicht erreichbar");
        errorBook.setAuthor("System Admin");

        fallbackList.add(errorBook);

        return fallbackList;
    }
}
