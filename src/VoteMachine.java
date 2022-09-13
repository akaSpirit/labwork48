import com.sun.net.httpserver.HttpExchange;
import server.BasicServer;
import server.ContentType;
import server.Cookie;
import server.Utils;
import service.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class VoteMachine extends BasicServer {
    private final CandidateService service;
    private final UserModel userModel = new UserModel();
    private final Map<String, Boolean> isLogin = new HashMap<>();
    private User enteredUser;
    public VoteMachine(String host, int port) throws IOException {
        super(host, port);
        isLogin.put("status", true);
        service = new CandidateService();
        registerGet("/", this::mainHandler);
        registerPost("/",this::logoutHandler);
        registerPost("/vote", this::voteHandler);
        registerGet("/votes", this::votesHandler);
        registerGet("/candidate", this::candidateHandler);
        registerGet("/thankyou", this::thanksHandler);

        registerGet("/register", this::registerFormGet);
        registerPost("/register", this::registerFormPost);

        registerGet("/login", e -> {
            renderTemplate(e, "login.html", isLogin);
        });
        registerPost("/login", this::loginPost);
    }

    private void logoutHandler(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> map = Utils.parseUrlEncoded(raw, "&");
        String cookie = getCookie(exchange);
        if (map.containsKey("logout")) {
            exchange.getResponseHeaders().add("Set-Cookie", Cookie.make("isVoted", "", 600, true).toString());
            enteredUser = null;
            redirect303(exchange, "/");
            return;
        }
    }

    private void thanksHandler(HttpExchange exchange) {
        String query = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(query, "&");
        int id = Integer.parseInt(params.get("id"));
        Candidate candidate = service.getCandidate(id);
        renderTemplate(exchange, "thankyou.html", getSingleCandidate(candidate));
    }

    private void mainHandler(HttpExchange exchange) {
        renderTemplate(exchange, "candidates.html", getModel());
    }

    private void voteHandler(HttpExchange exchange) {
        String cookieRow = getCookie(exchange);
        Map<String, String> cookies = Utils.parseUrlEncoded(cookieRow, "; ");
        if (cookies.containsKey("isVoted")) {
            redirect303(exchange,"/voted.html");
        } else {
            String raw = getBody(exchange);
            Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");
            int id = Integer.parseInt(parsed.get("candidateId"));
            Candidate candidate = service.getCandidate(id);
            candidate.setVote(candidate.getVote() + 1);
            double voteSum = service.getAllCandidates().stream().mapToInt(Candidate::getVote).sum();
            service.getAllCandidates().forEach(e -> e.setVotePercent((e.getVote() / voteSum) * 100));
            setCookie(exchange, Cookie.make("isVoted", enteredUser.getUsername(), 600, true));
            FileService.writeCandidates(service.getAllCandidates());
            redirect303(exchange, "/candidate?id=" + id);       //if id > 6 redirect notfound.html
        }
    }

    private void votesHandler(HttpExchange exchange) {
        Map<String, Object> votes = new HashMap<>();
        List<Candidate> candidates = service.getAllCandidates().stream()
                .sorted(Comparator.comparing(Candidate::getVote)).toList();
        votes.put("candidates", candidates);
        renderTemplate(exchange, "votes.html", votes);

    }

    private void candidateHandler(HttpExchange exchange) {
        String query = getQueryParams(exchange);
        Map<String, String> params = Utils.parseUrlEncoded(query, "&");
        int id = Integer.parseInt(params.get("id"));
        Optional<Candidate> candidate = service.getAllCandidates().stream()
                .filter(e -> id == e.getId()).findFirst();
        if (candidate.isPresent()) {
            redirect303(exchange, "/thankyou?id=" + id);
            return;
        }
        redirect303(exchange, "notfound.html");
    }

    private CandidateDataModel getModel() {
        return new CandidateDataModel(service.getAllCandidates());
    }

    private SingleCandidateDataModel getSingleCandidate(Candidate candidate) {
        return new SingleCandidateDataModel(candidate);
    }

    public void loginPost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> map = Utils.parseUrlEncoded(raw, "&");
        String username = map.get("username");
        String password = map.get("password");

        if (userModel.getUsers().containsKey(username)) {
            if (userModel.getUsers().get(username).getPassword().equals(password)) {
                enteredUser = userModel.getUsers().get(username);
//                setCookie(exchange, Cookie.make("username", username, 600, true));
                redirect303(exchange, "/");
            } else {
                enteredUser = null;
                isLogin.put("status", false);
                redirect303(exchange, "/login");
            }
        } else {
            enteredUser = null;
            isLogin.put("status", false);
            redirect303(exchange, "/login");
        }
    }

    public void registerFormGet(HttpExchange exchange) {
        Path path = makeFilePath("register.html");
        sendFile(exchange, path, ContentType.TEXT_HTML);
    }

    public void registerFormPost(HttpExchange exchange) {
        String raw = getBody(exchange);

        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        String username = parsed.get("username");
        String password = parsed.get("password");

        if (userModel.getUsers().containsKey(username)) {
            redirect303(exchange, "/register");
            return;
        }
        userModel.getUsers().put(username, new User(username, password));
        FileService.writeUsers(userModel.getUsers());
        redirect303(exchange, "/login");
    }
}
