package local.dev.order.model;

import local.dev.order.model.Book;
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
}
