package dsa;

import model.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookSorter {
    public List<Book> sortByTitle(List<Book> books) {
        List<Book> sorted = new ArrayList<>(books);
        sorted.sort(Comparator.comparing(book -> book.getTitle() == null ? "" : book.getTitle().toLowerCase()));
        return sorted;
    }

    public List<Book> sortByAuthor(List<Book> books) {
        List<Book> sorted = new ArrayList<>(books);
        sorted.sort(Comparator.comparing(book -> book.getAuthor() == null ? "" : book.getAuthor().toLowerCase()));
        return sorted;
    }

    public List<Book> sortByYearDescending(List<Book> books) {
        List<Book> sorted = new ArrayList<>(books);
        sorted.sort((first, second) -> {
            Integer firstYear = first.getPublishYear() == null ? Integer.MIN_VALUE : first.getPublishYear();
            Integer secondYear = second.getPublishYear() == null ? Integer.MIN_VALUE : second.getPublishYear();
            return secondYear.compareTo(firstYear);
        });
        return sorted;
    }
}
