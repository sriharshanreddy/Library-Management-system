package dsa;

import model.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookSearch {
    public List<Book> linearSearchByTitle(List<Book> books, String query) {
        List<Book> matches = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle() != null && book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                matches.add(book);
            }
        }
        return matches;
    }

    public Book binarySearchByTitle(List<Book> books, String title) {
        List<Book> sortedBooks = new ArrayList<>(books);
        sortedBooks.sort(Comparator.comparing(book -> book.getTitle() == null ? "" : book.getTitle().toLowerCase()));
        int left = 0;
        int right = sortedBooks.size() - 1;
        String target = title.toLowerCase();

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String current = sortedBooks.get(mid).getTitle() == null ? "" : sortedBooks.get(mid).getTitle().toLowerCase();
            int comparison = current.compareTo(target);
            if (comparison == 0) {
                return sortedBooks.get(mid);
            }
            if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }
}
