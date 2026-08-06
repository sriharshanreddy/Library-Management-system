package service;

import dao.AuditDAO;
import dao.BookDAO;
import dao.CategoryDAO;
import dao.FineDAO;
import dao.IssueDAO;
import dao.LibrarianDAO;
import dao.ReportDAO;
import dao.ReservationDAO;
import dao.StudentDAO;
import dsa.BookSearch;
import dsa.BookSorter;
import model.Book;
import model.BookIssue;
import model.Category;
import model.Fine;
import model.Librarian;
import model.Reservation;
import model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LibraryService {
    private final BookDAO bookDAO = new BookDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final IssueDAO issueDAO = new IssueDAO();
    private final FineDAO fineDAO = new FineDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final LibrarianDAO librarianDAO = new LibrarianDAO();
    private final AuditDAO auditDAO = new AuditDAO();
    private final ReportDAO reportDAO = new ReportDAO();

    private final BookSearch bookSearch = new BookSearch();
    private final BookSorter bookSorter = new BookSorter();

    public int addBook(Book book) throws SQLException {
        return bookDAO.save(book);
    }

    public boolean updateBook(Book book) throws SQLException {
        return bookDAO.update(book);
    }

    public boolean deleteBook(int bookId) throws SQLException {
        return bookDAO.delete(bookId);
    }

    public int addCategory(Category category) throws SQLException {
        return categoryDAO.save(category);
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.findAll();
    }

    public List<Librarian> getAllLibrarians() throws SQLException {
        return librarianDAO.findAll();
    }

    public int registerStudent(Student student) throws SQLException {
        return studentDAO.save(student);
    }

    public int issueBook(int studentId, int bookId, Integer librarianId, LocalDate issueDate, LocalDate dueDate) throws SQLException {
        return issueDAO.issueBook(studentId, bookId, librarianId, issueDate, dueDate);
    }

    public boolean returnBook(int issueId, LocalDate returnDate, String conditionOfBook) throws SQLException {
        return issueDAO.returnBook(issueId, returnDate, conditionOfBook);
    }

    public int reserveBook(Reservation reservation) throws SQLException {
        return reservationDAO.save(reservation);
    }

    public boolean updateReservationStatus(int reservationId, String status) throws SQLException {
        return reservationDAO.updateStatus(reservationId, status);
    }

    public int createFine(Fine fine) throws SQLException {
        return fineDAO.save(fine);
    }

    public boolean markFineAsPaid(int fineId) throws SQLException {
        return fineDAO.markAsPaid(fineId);
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAll();
    }

    public List<Book> searchBooks(String query) throws SQLException {
        return bookSearch.linearSearchByTitle(bookDAO.findAll(), query);
    }

    public List<Book> getBooksSortedByTitle() throws SQLException {
        return bookSorter.sortByTitle(bookDAO.findAll());
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public List<BookIssue> getAllIssues() throws SQLException {
        return issueDAO.findAllIssues();
    }

    public List<BookIssue> getIssuesByStudent(int studentId) throws SQLException {
        return issueDAO.findIssuesByStudent(studentId);
    }

    public List<Fine> getAllFines() throws SQLException {
        return fineDAO.findAll();
    }

    public List<Fine> getPendingFines() throws SQLException {
        return fineDAO.findPending();
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.findAll();
    }

    public List<Map<String, Object>> getAuditLogs() throws SQLException {
        return auditDAO.findAllAuditLogs();
    }

    public Map<String, Object> getDashboardStats() throws SQLException {
        return reportDAO.getDashboardStats();
    }

    public List<Map<String, Object>> getBookAvailabilityView() throws SQLException {
        return reportDAO.getBookAvailabilityView();
    }

    public List<Map<String, Object>> getStudentIssueDetailsView() throws SQLException {
        return reportDAO.getStudentIssueDetailsView();
    }

    public List<Map<String, Object>> getFineReportView() throws SQLException {
        return reportDAO.getFineReportView();
    }

    public List<Map<String, Object>> getCategoryDistribution() throws SQLException {
        return reportDAO.getCategoryDistribution();
    }

    public boolean updateStudent(Student student) {
        try {
            return studentDAO.update(student);
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        try {
            return studentDAO.delete(studentId);
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }
}
