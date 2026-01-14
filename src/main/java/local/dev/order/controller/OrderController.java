package local.dev.order.controller;

import local.dev.order.model.ShoppingCart;
import local.dev.order.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import local.dev.order.model.Book;
import local.dev.order.service.BookCatalogService;

import java.util.Arrays;
import java.util.List;

@Controller
public class OrderController {

    private final BookCatalogService bookCatalogService;
    private final ShoppingCartService shoppingCartService;

    @Autowired
    public OrderController(BookCatalogService bookCatalogService, ShoppingCartService shoppingCartService) {
        this.bookCatalogService = bookCatalogService;
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/")
    public String showCadabra(Model model){
        model.addAttribute("allBooks",bookCatalogService.getAllBooks());
        model.addAttribute("cartCount",shoppingCartService.getShoppingCart().getItemCount());
        return "home"; // home.html anzeigen
    }

    // GET-Mapping zur Anzeige der Seite
    @GetMapping("/search")
    public String showSearchForm(@RequestParam(value = "query", required = false) List<String> query, Model model) {
        List<Book> searchResults;

        if (query != null && !query.isEmpty()) {
            List<String> splitKeywords = query.stream()
                    .flatMap(q -> Arrays.stream(q.split("[\\s+,]")))
                    .filter(q -> !q.isBlank())
                    .toList();

            searchResults = bookCatalogService.searchBooks(splitKeywords);

            model.addAttribute("searchQuery", String.join(" ", splitKeywords));
        } else {
            searchResults = bookCatalogService.getAllBooks();
            model.addAttribute("searchQuery", "");
        }

        model.addAttribute("books", searchResults);
        model.addAttribute("shoppingCartService", shoppingCartService);
        model.addAttribute("cartCount", shoppingCartService.getShoppingCart().getItemCount());

        return "search";
    }

    @PostMapping("/search")
    public String handleSearch(@RequestParam(required = false, name="query") String query) {
        return buildSearchRedirect(query);
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("isbn") String isbn,
                            @RequestParam("query") String lastQuery) {

        Book bookToAdd = bookCatalogService.findBookByIsbn(isbn);

        if(bookToAdd != null){
            shoppingCartService.addBook(bookToAdd);
            System.out.println("Buch zum Warenkorb hinzugefügt " + bookToAdd.getTitle());
        }

        return buildSearchRedirect(lastQuery);
    }

    @GetMapping("/cart")
    public String showCart(Model model) {
        ShoppingCart cart = shoppingCartService.getShoppingCart();
        model.addAttribute("cartCount", shoppingCartService.getShoppingCart().getItemCount());
        model.addAttribute("cartItems", cart.getItems());
        return "cart"; // Verweist auf die neue Thymeleaf-Datei: cart.html
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("isbn") String isbn,
                                 @RequestParam(required = false, name="query") String lastQuery) {

        shoppingCartService.removeBookByIsbn(isbn);

        return buildSearchRedirect(lastQuery);
    }

    private String buildSearchRedirect(String query) {
        if (query == null || query.isBlank()) {
            return "redirect:/search";
        }

        String[] keywords = query.trim().split("\\s+");

        StringBuilder redirectPath = new StringBuilder("redirect:/search?");

        for (int i = 0; i < keywords.length; i++) {
            String encodedKeyword = java.net.URLEncoder.encode(keywords[i], java.nio.charset.StandardCharsets.UTF_8);

            redirectPath.append("query=").append(encodedKeyword);

            if (i < keywords.length - 1) {
                redirectPath.append("&");
            }
        }

        return redirectPath.toString();
    }
}
