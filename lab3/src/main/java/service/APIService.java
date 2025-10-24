package service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import com.google.gson.*;
import model.Worker;
import java.util.*;
import model.Position;
import exception.ApiException;





public class APIService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/users";
    private final HttpClient client;

    public APIService(HttpClient client) {
        this.client = client;
    }

    public APIService() {
        this(HttpClient.newHttpClient());
    }

    public List<Worker> fetchEmployeesFromAPI() throws ApiException{
        try {
            String content = sendRequest();

            Gson gson = new Gson();

            JsonArray workerData = gson.fromJson(content, JsonArray.class);
            List<Worker> workers = new ArrayList<>();

            for (JsonElement elem : workerData) {
                try {
                    JsonObject workerJson = elem.getAsJsonObject();

                    Worker worker = parseWorker(workerJson);
                    workers.add(worker);
                } catch (Exception e){
                    throw new ApiException("Parsing worker error " + e.getMessage(), e); 
                }
        
            }

            return workers;
        
        } catch (JsonSyntaxException e) {
            throw new ApiException("Błąd parsowania JSON: " + e.getMessage());
        }


    }



    private String sendRequest() throws ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();
        
        try {
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiException("HTTP error: " + response.statusCode());
            }
            return response.body();
        }catch (IOException | InterruptedException e) {
            throw new ApiException("Błąd połączenia z API: " + e.getMessage());
        }
    }

    private Worker parseWorker(JsonObject workerJson) throws ApiException {
        String[] fullName = workerJson.get("name").getAsString().split(" ");
        String firstName = fullName[0];
        String lastName = fullName[1]; // tu nie wiem czy zakladac ze zawsze sa 2 czlony


        String email = workerJson.get("email").getAsString();

        String companyName = workerJson.getAsJsonObject("company").get("name").getAsString();

        Position position = Position.PROGRAMISTA;
        
        return new Worker(firstName, lastName, email, companyName, position);
    }
    
}
