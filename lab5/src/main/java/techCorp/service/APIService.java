package techCorp.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import com.google.gson.*;

import techCorp.exception.ApiException;
import techCorp.model.Position;
import techCorp.model.Worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;




@Service
public class APIService {

    private final String apiUrl;
    private final HttpClient client;
    private final Gson gson;

    public APIService(
            @Value("${app.api.url}") String apiUrl,
            HttpClient client,
            Gson gson) {
        this.apiUrl = apiUrl;
        this.client = client;
        this.gson = gson;
    }

    public List<Worker> fetchEmployeesFromAPI() throws ApiException{
        try {
            String content = sendRequest();

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
                    .uri(URI.create(apiUrl))
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
