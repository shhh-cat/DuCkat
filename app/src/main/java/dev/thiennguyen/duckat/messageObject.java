package dev.thiennguyen.duckat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class messageObject {
    private String content;
    private String from;
    private Timestamp time;

    public messageObject() { }

    public messageObject(String content, String from) {
        this.content = content;
        this.from = from;

        this.time = Timestamp.now();
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @ServerTimestamp
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
