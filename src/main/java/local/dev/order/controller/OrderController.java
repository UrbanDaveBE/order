package local.dev.order.controller;

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

    @Autowired
    public OrderController(BookCatalogService bookCatalogService) {
        this.bookCatalogService = bookCatalogService;
    }

    // GET-Mapping zur Anzeige der Seite
    @GetMapping("/search")
    public String showSearchForm(Model model){
        model.addAttribute("book", Collections.emptyList());
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

        return "search";
    }
}
