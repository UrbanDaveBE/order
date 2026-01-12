package local.dev.order;

import com.microsoft.playwright.*;
import local.dev.order.model.Book;
import local.dev.order.service.BookCatalogService;
import org.junit.jupiter.api.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderE2ETest {
    @LocalServerPort
    private int port;

    @MockitoBean
    private BookCatalogService bookCatalogService;

    @Test
    void testSearchAndAdd() {
// GIVEN: Wenn der Service nach irgendwelchen Keywords gefragt wird...
        Book mockBook = new Book("978-0-13-235088-4", "Clean Architecture", "Robert C. Martin");
        given(bookCatalogService.searchBooks(anyList())).willReturn(List.of(mockBook));
        // Wichtig: findByIsbn muss auch gemockt werden, damit "add" funktioniert!
        given(bookCatalogService.findBookByIsbn("978-0-13-235088-4")).willReturn(mockBook);
        try (Playwright playwright = Playwright.create()) {
            boolean isHeadless = true;

            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isHeadless));
            Page page = browser.newPage();

            String baseUrl = "http://localhost:" + port;
            page.navigate(baseUrl + "/search");

            // 1. Suche über ID ausfüllen
            page.fill("#query", "clean arch");

            // 2. Klick auf Button über ID (Best Practice!)
            page.click("#search-button");

            // 3. Warten auf die Tabelle
            page.waitForSelector("table");

            // 4. Klick auf den spezifischen Warenkorb-Button
            // Clean Architecture = '978-0-13-235088-4'
            page.click("#add-to-cart-978-0-13-235088-4");
            // Oder wir nutzen einen Selektor, der den ersten Button mit dieser Klasse nimmt:
            //page.locator("button[id^='add-to-cart-']").first().click();


            // 5. Navigation & Kontrolle
            page.navigate(baseUrl + "/cart");
            // Warten auf die h1-Überschrift (IDs bei Überschriften sind oft Overkill, aber möglich)
            page.waitForSelector("h1");

            assertTrue(page.locator("table").innerText().contains("Clean Architecture"));

            browser.close();
        }
    }
}