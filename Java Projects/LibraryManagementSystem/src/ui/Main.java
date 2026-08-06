package ui;

import model.Book;
import model.Reservation;
import model.Student;
import service.LibraryService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LibraryService service = new LibraryService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("=== Library Management System ===");
            System.out.println("1. Show all books");
            System.out.println("2. Search book by title");
            System.out.println("3. Add book");
            System.out.println("4. Register student");
            System.out.println("5. Issue book");
            System.out.println("6. Return book");
            System.out.println("7. Reserve book");
            System.out.println("8. Show all students");
            System.out.println("9. Show issue history by student");
            System.out.println("10. Show pending fines");
            System.out.println("11. Launch Web Server (HTML/CSS/JS Web UI)");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = readInt(scanner);

            try {
                switch (choice) {
                    case 1:
                        printBooks(service.getAllBooks());
                        break;
                    case 2:
                        System.out.print("Enter title keyword: ");
                        printBooks(service.searchBooks(scanner.nextLine()));
                        break;
                    case 3:
                        service.addBook(readBook(scanner));
                        System.out.println("Book added successfully.");
                        break;
                    case 4:
                        service.registerStudent(readStudent(scanner));
                        System.out.println("Student registered successfully.");
                        break;
                    case 5:
                        System.out.print("Student ID: ");
                        int issueStudentId = readInt(scanner);
                        System.out.print("Book ID: ");
                        int issueBookId = readInt(scanner);
                        System.out.print("Librarian ID (0 for none): ");
                        int librarianId = readInt(scanner);
                        System.out.print("Issue date (YYYY-MM-DD): ");
                        LocalDate issueDate = LocalDate.parse(scanner.nextLine());
                        System.out.print("Due date (YYYY-MM-DD): ");
                        LocalDate dueDate = LocalDate.parse(scanner.nextLine());
                        service.issueBook(issueStudentId, issueBookId, librarianId == 0 ? null : librarianId, issueDate, dueDate);
                        System.out.println("Book issued successfully.");
                        break;
                    case 6:
                        System.out.print("Issue ID: ");
                        int issueId = readInt(scanner);
                        System.out.print("Return date (YYYY-MM-DD): ");
                        LocalDate returnDate = LocalDate.parse(scanner.nextLine());
                        System.out.print("Condition of book: ");
                        String condition = scanner.nextLine();
                        service.returnBook(issueId, returnDate, condition);
                        System.out.println("Book returned successfully.");
                        break;
                    case 7:
                        System.out.print("Student ID: ");
                        int reservationStudentId = readInt(scanner);
                        System.out.print("Book ID: ");
                        int reservationBookId = readInt(scanner);
                        System.out.print("Reservation date (YYYY-MM-DD): ");
                        LocalDate reservationDate = LocalDate.parse(scanner.nextLine());
                        Reservation reservation = new Reservation();
                        reservation.setStudentId(reservationStudentId);
                        reservation.setBookId(reservationBookId);
                        reservation.setReservationDate(reservationDate);
                        reservation.setStatus("Reserved");
                        service.reserveBook(reservation);
                        System.out.println("Reservation saved successfully.");
                        break;
                    case 8:
                        printStudents(service.getAllStudents());
                        break;
                    case 9:
                        System.out.print("Student ID: ");
                        printIssues(service.getIssuesByStudent(readInt(scanner)));
                        break;
                    case 10:
                        System.out.println(service.getPendingFines());
                        break;
                    case 11:
                        web.WebServer.main(args);
                        break;
                    case 0:
                        System.out.println("Exiting application.");
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (SQLException exception) {
                System.out.println("Database error: " + exception.getMessage());
            } catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }
    }

    private static Book readBook(Scanner scanner) {
        Book book = new Book();
        System.out.print("ISBN: ");
        book.setIsbn(scanner.nextLine());
        System.out.print("Title: ");
        book.setTitle(scanner.nextLine());
        System.out.print("Author: ");
        book.setAuthor(scanner.nextLine());
        System.out.print("Publisher: ");
        book.setPublisher(scanner.nextLine());
        System.out.print("Edition: ");
        book.setEdition(scanner.nextLine());
        System.out.print("Publish year: ");
        String year = scanner.nextLine();
        book.setPublishYear(year.isBlank() ? null : Integer.parseInt(year));
        System.out.print("Category ID: ");
        String categoryId = scanner.nextLine();
        book.setCategoryId(categoryId.isBlank() ? null : Integer.parseInt(categoryId));
        System.out.print("Total copies: ");
        book.setTotalCopies(Integer.parseInt(scanner.nextLine()));
        System.out.print("Available copies: ");
        book.setAvailableCopies(Integer.parseInt(scanner.nextLine()));
        System.out.print("Shelf no: ");
        book.setShelfNo(scanner.nextLine());
        return book;
    }

    private static Student readStudent(Scanner scanner) {
        Student student = new Student();
        System.out.print("Roll no: ");
        student.setRollNo(scanner.nextLine());
        System.out.print("First name: ");
        student.setFirstName(scanner.nextLine());
        System.out.print("Last name: ");
        student.setLastName(scanner.nextLine());
        System.out.print("Gender: ");
        student.setGender(scanner.nextLine());
        System.out.print("Department: ");
        student.setDepartment(scanner.nextLine());
        System.out.print("Semester: ");
        String semester = scanner.nextLine();
        student.setSemester(semester.isBlank() ? null : Integer.parseInt(semester));
        System.out.print("Phone: ");
        student.setPhone(scanner.nextLine());
        System.out.print("Email: ");
        student.setEmail(scanner.nextLine());
        System.out.print("Address: ");
        student.setAddress(scanner.nextLine());
        System.out.print("Join date (YYYY-MM-DD): ");
        student.setJoinDate(LocalDate.parse(scanner.nextLine()));
        return student;
    }

    private static int readInt(Scanner scanner) {
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            input = scanner.nextLine().trim();
        }
        return Integer.parseInt(input);
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        for (Student student : students) {
            System.out.println(student);
        }
    }

    private static void printIssues(List<?> issues) {
        if (issues.isEmpty()) {
            System.out.println("No issue records found.");
            return;
        }
        for (Object issue : issues) {
            System.out.println(issue);
        }
    }
}
