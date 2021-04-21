package ejsp.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Post {
    private String timestamp = "";
    private String title = "";
    private String content = "";
    private String author = "";
    private String author_id = "";

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void generateTimestamp(String datePattern) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(datePattern);
        LocalDateTime currentTime = LocalDateTime.now();
        timestamp = timeFormatter.format(currentTime);
    }

    public void generateTimestamp() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        timestamp = timeFormatter.format(currentTime);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
}
