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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public String showSearchForm(Model model){
        model.addAttribute("book", Collections.emptyList());
        model.addAttribute("cartCount", shoppingCartService.getShoppingCart().getItemCount());
        return "search";
    }

    // POST-Mapping zur Verarbeitung der Suchanfrage
    @PostMapping("/search")
    public String handleSearch(@RequestParam(required = false, name="query") String query, Model model){
        List<Book> searchResults;
        List<String> keywords;

        if(!query.isEmpty() && query != null) {

            // mehrfachsuche
            keywords = Arrays.stream(query.trim().split("\\s+"))
                    .filter(k -> !k.isEmpty())
                    .toList();

            if (!keywords.isEmpty()) {
                searchResults = bookCatalogService.searchBooks(keywords);
            } else {
                searchResults = Collections.emptyList();
            }
        } else {
            searchResults = Collections.emptyList();
        }


        model.addAttribute("books", searchResults);
        model.addAttribute("searchQuery", query);
        model.addAttribute("cartCount", shoppingCartService.getShoppingCart().getItemCount());
        return "search";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("isbn") String isbn, @RequestParam("query") String lastQuery, Model model){
        Book bookToAdd = bookCatalogService.findBookByIsbn(isbn);

        if(bookToAdd != null){
            shoppingCartService.addBook(bookToAdd);
            System.out.println("Buch zum Warenkorb hinzugefügt " + bookToAdd.getTitle());
        }
        model.addAttribute("cartCount", shoppingCartService.getShoppingCart().getItemCount());
        return "redirect:/search?query="+lastQuery;
    }

    @GetMapping("/cart")
    public String showCart(Model model) {
        // Ruft den Session-gebundenen Warenkorb ab
        ShoppingCart cart = shoppingCartService.getShoppingCart();
        model.addAttribute("cartCount", shoppingCartService.getShoppingCart().getItemCount());
        model.addAttribute("cartItems", cart.getItems());
        return "cart"; // Verweist auf die neue Thymeleaf-Datei: cart.html
    }

}
