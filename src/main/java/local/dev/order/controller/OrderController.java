package local.dev.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    // GET-Mapping zur Anzeige der Seite
    @GetMapping("/search")
    public String showSearchForm(){
        return "search";
    }

    // POST-Mapping zur Verarbeitung der Suchanfrage
    @PostMapping("/search")
    public String handleSearch(@RequestParam(required = false, name="query") String query){
        //TODO: mit Logik für den Aufruf des Catalog Microservice
        System.out.println("query:"+query);
        return "search";
    }
}
