package entities;

public class Token {
    private long time = System.currentTimeMillis();
    private String content;

    public Token(String content, long createTime) {
        this.time = createTime;
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public Token setTime(long time) {
        this.time = time;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Token setContent(String content) {
        this.content = content;
        return this;
    }
}
