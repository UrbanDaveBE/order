package local.dev.order.service;

import local.dev.order.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
    public BookCatalogService(@Value("${catalog.base.url}") String catalogBaseUrl) {

        // 🛠️ NEU: Der RestClient wird mit der injizierten URL initialisiert
        this.restClient = RestClient.builder()
                .baseUrl(catalogBaseUrl) // Nutzt die dynamische URL (z.B. http://catalog:8080)
                .build();
    }


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

    public Object getAllBooks() {
        return restClient.get()
                .uri("/api/books")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Book>>() {});
    }
}
