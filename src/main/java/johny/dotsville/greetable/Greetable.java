package johny.dotsville.greetable;

public abstract class Greetable {
    private String greet;

    public Greetable(String greet) {
        this.greet = greet;
    }

    public String buildResponse(String username) {
        return greet + ", " + username;
    };
}
