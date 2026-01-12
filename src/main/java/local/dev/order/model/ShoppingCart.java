package local.dev.order.model;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<Book> items = new ArrayList<>();

    public void addItem(Book book){
        items.add(book);
    }

    public List<Book> getItems(){
        return items;
    }

    public int getItemCount(){
        return items.size();
    }

    public boolean contains(String isbn) {
        return items.stream().anyMatch(item -> item.getIsbn().equals(isbn));
    }

    public void removeItem(String isbn) {
        this.items.removeIf(item -> item.getIsbn().equals(isbn));
    }
}
