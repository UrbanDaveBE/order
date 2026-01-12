package local.dev.order.service;

import local.dev.order.model.Book;
import local.dev.order.model.ShoppingCart;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ShoppingCartService {
    private final ShoppingCart shoppingCart = new ShoppingCart();

    public void addBook(Book book) {
        this.shoppingCart.addItem(book);
    }
    public ShoppingCart getShoppingCart() {
        return this.shoppingCart;
    }

    public void removeBookByIsbn(String isbn) {
        this.shoppingCart.removeItem(isbn);
    }
}
