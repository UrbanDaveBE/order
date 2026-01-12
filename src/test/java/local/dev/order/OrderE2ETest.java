package local.dev.order;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class OrderE2ETest {

    @Test
    void testSearchAndAdd() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            Page page = browser.newPage();

            page.navigate("http://localhost:8081/search");

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
            page.navigate("http://localhost:8081/cart");

            // Warten auf die h1-Überschrift (IDs bei Überschriften sind oft Overkill, aber möglich)
            page.waitForSelector("h1");

            assertTrue(page.locator("table").innerText().contains("Clean Architecture"));

            browser.close();
        }
    }
}