package dev.thiennguyen.duckat;

public class friendsObject {
    private String id;
    private String name;

    public friendsObject(){}

    public friendsObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
