package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Book;
import model.Category;
import model.Fine;
import model.Librarian;
import model.Reservation;
import model.Student;
import service.LibraryService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiHandler implements HttpHandler {
    private final LibraryService libraryService = new LibraryService();
    private final String publicDirPath;

    public ApiHandler(String publicDirPath) {
        this.publicDirPath = publicDirPath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS Headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        String method = exchange.getRequestMethod().toUpperCase();
        if ("OPTIONS".equals(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();

        try {
            if (path.startsWith("/api/")) {
                handleApi(exchange, path, method);
            } else {
                serveStaticFile(exchange, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, "{\"error\": " + toJsonString(e.getMessage()) + "}");
        }
    }

    private void handleApi(HttpExchange exchange, String path, String method) throws Exception {
        if ("/api/dashboard".equals(path) && "GET".equals(method)) {
            Map<String, Object> stats = libraryService.getDashboardStats();
            sendJsonResponse(exchange, 200, mapToJson(stats));
            return;
        }

        if ("/api/books".equals(path)) {
            if ("GET".equals(method)) {
                String query = getQueryParam(exchange, "search");
                List<Book> books;
                if (query != null && !query.trim().isEmpty()) {
                    books = libraryService.searchBooks(query);
                } else {
                    books = libraryService.getAllBooks();
                }
                sendJsonResponse(exchange, 200, booksToJson(books));
                return;
            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Book book = new Book();
                book.setIsbn(data.get("isbn"));
                book.setTitle(data.get("title"));
                book.setAuthor(data.get("author"));
                book.setPublisher(data.get("publisher"));
                book.setEdition(data.get("edition"));
                if (data.get("publishYear") != null && !data.get("publishYear").isEmpty()) {
                    book.setPublishYear(Integer.parseInt(data.get("publishYear")));
                }
                if (data.get("categoryId") != null && !data.get("categoryId").isEmpty()) {
                    book.setCategoryId(Integer.parseInt(data.get("categoryId")));
                }
                book.setTotalCopies(Integer.parseInt(data.getOrDefault("totalCopies", "1")));
                book.setAvailableCopies(Integer.parseInt(data.getOrDefault("availableCopies", "1")));
                book.setShelfNo(data.get("shelfNo"));

                int newId = libraryService.addBook(book);
                sendJsonResponse(exchange, 201, "{\"message\": \"Book added successfully\", \"bookId\": " + newId + "}");
                return;
            } else if ("PUT".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Book book = new Book();
                book.setBookId(Integer.parseInt(data.get("bookId")));
                book.setIsbn(data.get("isbn"));
                book.setTitle(data.get("title"));
                book.setAuthor(data.get("author"));
                book.setPublisher(data.get("publisher"));
                book.setEdition(data.get("edition"));
                if (data.get("publishYear") != null && !data.get("publishYear").isEmpty()) {
                    book.setPublishYear(Integer.parseInt(data.get("publishYear")));
                }
                if (data.get("categoryId") != null && !data.get("categoryId").isEmpty()) {
                    book.setCategoryId(Integer.parseInt(data.get("categoryId")));
                }
                book.setTotalCopies(Integer.parseInt(data.getOrDefault("totalCopies", "1")));
                book.setAvailableCopies(Integer.parseInt(data.getOrDefault("availableCopies", "1")));
                book.setShelfNo(data.get("shelfNo"));

                boolean updated = libraryService.updateBook(book);
                sendJsonResponse(exchange, 200, "{\"success\": " + updated + "}");
                return;
            }
        }

        if (path.startsWith("/api/books/") && "DELETE".equals(method)) {
            int bookId = Integer.parseInt(path.substring("/api/books/".length()));
            boolean deleted = libraryService.deleteBook(bookId);
            sendJsonResponse(exchange, 200, "{\"success\": " + deleted + "}");
            return;
        }

        if ("/api/categories".equals(path)) {
            if ("GET".equals(method)) {
                List<Category> categories = libraryService.getAllCategories();
                sendJsonResponse(exchange, 200, categoriesToJson(categories));
                return;
            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Category cat = new Category();
                cat.setCategoryName(data.get("categoryName"));
                cat.setDescription(data.get("description"));
                int catId = libraryService.addCategory(cat);
                sendJsonResponse(exchange, 201, "{\"message\": \"Category created\", \"categoryId\": " + catId + "}");
                return;
            }
        }

        if ("/api/students".equals(path)) {
            if ("GET".equals(method)) {
                List<Student> students = libraryService.getAllStudents();
                sendJsonResponse(exchange, 200, studentsToJson(students));
                return;
            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Student s = new Student();
                s.setRollNo(data.get("rollNo"));
                s.setFirstName(data.get("firstName"));
                s.setLastName(data.get("lastName"));
                s.setGender(data.get("gender"));
                s.setDepartment(data.get("department"));
                if (data.get("semester") != null && !data.get("semester").isEmpty()) {
                    s.setSemester(Integer.parseInt(data.get("semester")));
                }
                s.setPhone(data.get("phone"));
                s.setEmail(data.get("email"));
                s.setAddress(data.get("address"));
                s.setJoinDate(data.get("joinDate") != null ? LocalDate.parse(data.get("joinDate")) : LocalDate.now());

                int sId = libraryService.registerStudent(s);
                sendJsonResponse(exchange, 201, "{\"message\": \"Student registered\", \"studentId\": " + sId + "}");
                return;
            } else if ("PUT".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Student s = new Student();
                s.setStudentId(Integer.parseInt(data.get("studentId")));
                s.setRollNo(data.get("rollNo"));
                s.setFirstName(data.get("firstName"));
                s.setLastName(data.get("lastName"));
                s.setGender(data.get("gender"));
                s.setDepartment(data.get("department"));
                if (data.get("semester") != null && !data.get("semester").isEmpty()) {
                    s.setSemester(Integer.parseInt(data.get("semester")));
                }
                s.setPhone(data.get("phone"));
                s.setEmail(data.get("email"));
                s.setAddress(data.get("address"));
                s.setJoinDate(data.get("joinDate") != null ? LocalDate.parse(data.get("joinDate")) : LocalDate.now());

                boolean updated = libraryService.updateStudent(s);
                sendJsonResponse(exchange, 200, "{\"success\": " + updated + "}");
                return;
            }
        }

        if (path.startsWith("/api/students/") && "DELETE".equals(method)) {
            int studentId = Integer.parseInt(path.substring("/api/students/".length()));
            boolean deleted = libraryService.deleteStudent(studentId);
            sendJsonResponse(exchange, 200, "{\"success\": " + deleted + "}");
            return;
        }

        if ("/api/librarians".equals(path) && "GET".equals(method)) {
            List<Librarian> librarians = libraryService.getAllLibrarians();
            sendJsonResponse(exchange, 200, librariansToJson(librarians));
            return;
        }

        if ("/api/issues".equals(path)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, issuesToJson(libraryService.getAllIssues()));
                return;
            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                int studentId = Integer.parseInt(data.get("studentId"));
                int bookId = Integer.parseInt(data.get("bookId"));
                Integer librarianId = data.get("librarianId") != null && !data.get("librarianId").isEmpty() ?
                        Integer.parseInt(data.get("librarianId")) : null;
                LocalDate issueDate = data.get("issueDate") != null && !data.get("issueDate").isEmpty() ?
                        LocalDate.parse(data.get("issueDate")) : LocalDate.now();
                LocalDate dueDate = data.get("dueDate") != null && !data.get("dueDate").isEmpty() ?
                        LocalDate.parse(data.get("dueDate")) : issueDate.plusDays(14);

                int issueId = libraryService.issueBook(studentId, bookId, librarianId, issueDate, dueDate);
                sendJsonResponse(exchange, 201, "{\"message\": \"Book issued successfully\", \"issueId\": " + issueId + "}");
                return;
            }
        }

        if ("/api/issues/return".equals(path) && "POST".equals(method)) {
            String body = readRequestBody(exchange);
            Map<String, String> data = parseJsonSimple(body);
            int issueId = Integer.parseInt(data.get("issueId"));
            LocalDate returnDate = data.get("returnDate") != null && !data.get("returnDate").isEmpty() ?
                    LocalDate.parse(data.get("returnDate")) : LocalDate.now();
            String condition = data.getOrDefault("conditionOfBook", "Good");

            boolean success = libraryService.returnBook(issueId, returnDate, condition);
            sendJsonResponse(exchange, 200, "{\"success\": " + success + ", \"message\": \"Book returned\"}");
            return;
        }

        if ("/api/fines".equals(path)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, finesToJson(libraryService.getAllFines()));
                return;
            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Fine fine = new Fine();
                fine.setIssueId(Integer.parseInt(data.get("issueId")));
                fine.setFineAmount(Double.parseDouble(data.get("fineAmount")));
                fine.setPaidStatus(data.getOrDefault("paidStatus", "Pending"));
                int fineId = libraryService.createFine(fine);
                sendJsonResponse(exchange, 201, "{\"message\": \"Fine created\", \"fineId\": " + fineId + "}");
                return;
            }
        }

        if (path.startsWith("/api/fines/") && path.endsWith("/pay") && "POST".equals(method)) {
            String idStr = path.substring("/api/fines/".length(), path.indexOf("/pay"));
            int fineId = Integer.parseInt(idStr);
            boolean updated = libraryService.markFineAsPaid(fineId);
            sendJsonResponse(exchange, 200, "{\"success\": " + updated + "}");
            return;
        }

        if ("/api/reservations".equals(path)) {
            if ("GET".equals(method)) {
                sendJsonResponse(exchange, 200, reservationsToJson(libraryService.getAllReservations()));
                return;
            } else if ("POST".equals(method)) {
                String body = readRequestBody(exchange);
                Map<String, String> data = parseJsonSimple(body);
                Reservation r = new Reservation();
                r.setStudentId(Integer.parseInt(data.get("studentId")));
                r.setBookId(Integer.parseInt(data.get("bookId")));
                r.setReservationDate(data.get("reservationDate") != null && !data.get("reservationDate").isEmpty() ?
                        LocalDate.parse(data.get("reservationDate")) : LocalDate.now());
                r.setStatus(data.getOrDefault("status", "Reserved"));
                int rId = libraryService.reserveBook(r);
                sendJsonResponse(exchange, 201, "{\"message\": \"Book reserved\", \"reservationId\": " + rId + "}");
                return;
            }
        }

        if (path.startsWith("/api/reservations/") && "PUT".equals(method)) {
            int rId = Integer.parseInt(path.substring("/api/reservations/".length()));
            String body = readRequestBody(exchange);
            Map<String, String> data = parseJsonSimple(body);
            String status = data.get("status");
            boolean updated = libraryService.updateReservationStatus(rId, status);
            sendJsonResponse(exchange, 200, "{\"success\": " + updated + "}");
            return;
        }

        if ("/api/audit-logs".equals(path) && "GET".equals(method)) {
            sendJsonResponse(exchange, 200, listOfMapToJson(libraryService.getAuditLogs()));
            return;
        }

        if ("/api/reports/book-availability".equals(path) && "GET".equals(method)) {
            sendJsonResponse(exchange, 200, listOfMapToJson(libraryService.getBookAvailabilityView()));
            return;
        }

        if ("/api/reports/student-issue-details".equals(path) && "GET".equals(method)) {
            sendJsonResponse(exchange, 200, listOfMapToJson(libraryService.getStudentIssueDetailsView()));
            return;
        }

        if ("/api/reports/fine-report".equals(path) && "GET".equals(method)) {
            sendJsonResponse(exchange, 200, listOfMapToJson(libraryService.getFineReportView()));
            return;
        }

        if ("/api/reports/category-distribution".equals(path) && "GET".equals(method)) {
            sendJsonResponse(exchange, 200, listOfMapToJson(libraryService.getCategoryDistribution()));
            return;
        }

        sendJsonResponse(exchange, 404, "{\"error\": \"Endpoint not found\"}");
    }

    private void serveStaticFile(HttpExchange exchange, String path) throws IOException {
        if ("/".equals(path) || path.trim().isEmpty()) {
            path = "/index.html";
        }

        File file = new File(publicDirPath, path);
        if (!file.exists() || file.isDirectory()) {
            // Fallback to index.html for SPA or 404
            file = new File(publicDirPath, "index.html");
        }

        if (!file.exists()) {
            String resp = "<h1>404 Not Found</h1>";
            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, resp.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resp.getBytes());
            }
            return;
        }

        String mimeType = getMimeType(file.getName());
        exchange.getResponseHeaders().add("Content-Type", mimeType);

        byte[] fileBytes = readFileBytes(file);
        exchange.sendResponseHeaders(200, fileBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(fileBytes);
        }
    }

    private String getMimeType(String filename) {
        if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
        if (filename.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (filename.endsWith(".json")) return "application/json; charset=UTF-8";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".svg")) return "image/svg+xml";
        return "text/plain";
    }

    private byte[] readFileBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[8192];
            int read;
            while ((read = fis.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            return baos.toByteArray();
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int read;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2 && parts[0].equalsIgnoreCase(key)) {
                try {
                    return URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name());
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    // JSON Helper Methods
    private Map<String, String> parseJsonSimple(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        // Simple key-value parser for simple JSON payload
        StringBuilder keyBuf = new StringBuilder();
        StringBuilder valBuf = new StringBuilder();
        boolean inKey = false;
        boolean inVal = false;
        boolean readingKey = true;
        boolean escaped = false;

        String currentKey = "";
        for (int i = 0; i < clean.length(); i++) {
            char c = clean.charAt(i);
            if (escaped) {
                if (readingKey) keyBuf.append(c);
                else valBuf.append(c);
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                if (readingKey) {
                    inKey = !inKey;
                } else {
                    inVal = !inVal;
                }
                continue;
            }
            if (c == ':' && !inKey && readingKey) {
                readingKey = false;
                currentKey = keyBuf.toString().trim();
                keyBuf.setLength(0);
                continue;
            }
            if (c == ',' && !inVal && !inKey) {
                String val = valBuf.toString().trim();
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    val = val.substring(1, val.length() - 1);
                }
                map.put(currentKey, val);
                valBuf.setLength(0);
                readingKey = true;
                continue;
            }

            if (readingKey) {
                if (inKey) keyBuf.append(c);
            } else {
                valBuf.append(c);
            }
        }
        if (!currentKey.isEmpty()) {
            String val = valBuf.toString().trim();
            if (val.startsWith("\"") && val.endsWith("\"")) {
                val = val.substring(1, val.length() - 1);
            }
            map.put(currentKey, val);
        }
        return map;
    }

    private String toJsonString(String val) {
        if (val == null) return "null";
        return "\"" + val.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") + "\"";
    }

    private String booksToJson(List<Book> books) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            sb.append("{")
              .append("\"bookId\":").append(b.getBookId()).append(",")
              .append("\"isbn\":").append(toJsonString(b.getIsbn())).append(",")
              .append("\"title\":").append(toJsonString(b.getTitle())).append(",")
              .append("\"author\":").append(toJsonString(b.getAuthor())).append(",")
              .append("\"publisher\":").append(toJsonString(b.getPublisher())).append(",")
              .append("\"edition\":").append(toJsonString(b.getEdition())).append(",")
              .append("\"publishYear\":").append(b.getPublishYear()).append(",")
              .append("\"categoryId\":").append(b.getCategoryId()).append(",")
              .append("\"totalCopies\":").append(b.getTotalCopies()).append(",")
              .append("\"availableCopies\":").append(b.getAvailableCopies()).append(",")
              .append("\"shelfNo\":").append(toJsonString(b.getShelfNo()))
              .append("}");
            if (i < books.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String categoriesToJson(List<Category> categories) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < categories.size(); i++) {
            Category c = categories.get(i);
            sb.append("{")
              .append("\"categoryId\":").append(c.getCategoryId()).append(",")
              .append("\"categoryName\":").append(toJsonString(c.getCategoryName())).append(",")
              .append("\"description\":").append(toJsonString(c.getDescription()))
              .append("}");
            if (i < categories.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String studentsToJson(List<Student> students) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            sb.append("{")
              .append("\"studentId\":").append(s.getStudentId()).append(",")
              .append("\"rollNo\":").append(toJsonString(s.getRollNo())).append(",")
              .append("\"firstName\":").append(toJsonString(s.getFirstName())).append(",")
              .append("\"lastName\":").append(toJsonString(s.getLastName())).append(",")
              .append("\"gender\":").append(toJsonString(s.getGender())).append(",")
              .append("\"department\":").append(toJsonString(s.getDepartment())).append(",")
              .append("\"semester\":").append(s.getSemester()).append(",")
              .append("\"phone\":").append(toJsonString(s.getPhone())).append(",")
              .append("\"email\":").append(toJsonString(s.getEmail())).append(",")
              .append("\"address\":").append(toJsonString(s.getAddress())).append(",")
              .append("\"joinDate\":").append(s.getJoinDate() != null ? toJsonString(s.getJoinDate().toString()) : "null")
              .append("}");
            if (i < students.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String librariansToJson(List<Librarian> librarians) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < librarians.size(); i++) {
            Librarian l = librarians.get(i);
            sb.append("{")
              .append("\"librarianId\":").append(l.getLibrarianId()).append(",")
              .append("\"firstName\":").append(toJsonString(l.getFirstName())).append(",")
              .append("\"lastName\":").append(toJsonString(l.getLastName())).append(",")
              .append("\"phone\":").append(toJsonString(l.getPhone())).append(",")
              .append("\"email\":").append(toJsonString(l.getEmail())).append(",")
              .append("\"hireDate\":").append(l.getHireDate() != null ? toJsonString(l.getHireDate().toString()) : "null")
              .append("}");
            if (i < librarians.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String issuesToJson(List<model.BookIssue> issues) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < issues.size(); i++) {
            model.BookIssue issue = issues.get(i);
            sb.append("{")
              .append("\"issueId\":").append(issue.getIssueId()).append(",")
              .append("\"studentId\":").append(issue.getStudentId()).append(",")
              .append("\"bookId\":").append(issue.getBookId()).append(",")
              .append("\"librarianId\":").append(issue.getLibrarianId()).append(",")
              .append("\"issueDate\":").append(issue.getIssueDate() != null ? toJsonString(issue.getIssueDate().toString()) : "null").append(",")
              .append("\"dueDate\":").append(issue.getDueDate() != null ? toJsonString(issue.getDueDate().toString()) : "null").append(",")
              .append("\"status\":").append(toJsonString(issue.getStatus()))
              .append("}");
            if (i < issues.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String finesToJson(List<Fine> fines) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < fines.size(); i++) {
            Fine f = fines.get(i);
            sb.append("{")
              .append("\"fineId\":").append(f.getFineId()).append(",")
              .append("\"issueId\":").append(f.getIssueId()).append(",")
              .append("\"fineAmount\":").append(f.getFineAmount()).append(",")
              .append("\"paidStatus\":").append(toJsonString(f.getPaidStatus()))
              .append("}");
            if (i < fines.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String reservationsToJson(List<Reservation> reservations) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);
            sb.append("{")
              .append("\"reservationId\":").append(r.getReservationId()).append(",")
              .append("\"studentId\":").append(r.getStudentId()).append(",")
              .append("\"bookId\":").append(r.getBookId()).append(",")
              .append("\"reservationDate\":").append(r.getReservationDate() != null ? toJsonString(r.getReservationDate().toString()) : "null").append(",")
              .append("\"status\":").append(toJsonString(r.getStatus()))
              .append("}");
            if (i < reservations.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int count = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (count > 0) sb.append(",");
            sb.append(toJsonString(entry.getKey())).append(":");
            Object val = entry.getValue();
            if (val instanceof Number || val instanceof Boolean) {
                sb.append(val);
            } else if (val == null) {
                sb.append("null");
            } else {
                sb.append(toJsonString(val.toString()));
            }
            count++;
        }
        sb.append("}");
        return sb.toString();
    }

    private String listOfMapToJson(List<Map<String, Object>> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(mapToJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
