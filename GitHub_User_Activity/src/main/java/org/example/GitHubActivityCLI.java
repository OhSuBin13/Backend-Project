package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class GitHubActivityCLI {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java GitHubActivityCLI <username>");
            return;
        }
        GitHubActivityCLI cli = new GitHubActivityCLI();
        cli.fetchGitHubActivity(args[0]);
    }

    private void fetchGitHubActivity(String username) {
        String GITHUB_API_URL = "https://api.github.com/users/" + username + "/events";
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(GITHUB_API_URL))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 404) {
                System.out.println("User not found. Please check the username.");
                return;
            }
            if (response.statusCode() == 200) {
                JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                displayActivity(jsonArray);
            } else {
                System.out.println("Error:" + response.statusCode());
            }
        } catch (URISyntaxException uriSyntaxException) {
            uriSyntaxException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private void displayActivity(JsonArray events) {
        for (JsonElement element : events) {
            JsonObject event = element.getAsJsonObject();
            String type = event.get("type").getAsString();
            String repoName = event.get("repo").getAsJsonObject().get("name").getAsString();
            String action;
            switch (type){
                case "PushEvent":
                    int commitCount = event.get("payload").getAsJsonObject().get("commits").getAsJsonArray().size();
                    action = "Pushed " + commitCount + " commit(s) to " + repoName;
                    break;
                case "IssuesEvent":
                    String issueAction = event.get("payload").getAsJsonObject().get("action").getAsString();
                    String formattedIssueAction = issueAction.substring(0, 1).toUpperCase() + issueAction.substring(1);
                    action = formattedIssueAction + " an issue in " + repoName;
                    break;
                case "WatchEvent":
                    action = "Starred " + repoName;
                    break;
                case "ForkEvent":
                    action = "Forked " + repoName;
                    break;
                case "CreateEvent":
                    action = "Created " + event.get("payload").getAsJsonObject().get("ref_type").getAsString()
                            + " in " + repoName;
                    break;
                default:
                    action = event.get("type").getAsString().replace("Event", "")
                            + " in " + repoName;
                    break;
            }
            System.out.println(action);
        }
    }
}
