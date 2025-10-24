import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import exception.ApiException;
import model.Position;
import model.Worker;
import service.APIService;

public class TestApiServiceMock {

    private HttpClient mockClient;
    private APIService apiService;

    @BeforeEach
    void setup() {
        mockClient = Mockito.mock(HttpClient.class);
        apiService = new APIService(mockClient); 
    }

    @Test
    void shouldParseSingleWorker_WhenResponse200AndValidJson() throws Exception {
        String fakeJson = """
            [
                {"name": "Jan Kowalski", "email": "jan@example.com", "company": {"name": "ABC"}}
            ]
        """;

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(fakeJson);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        List<Worker> workers = apiService.fetchEmployeesFromAPI();

        assertNotNull(workers, "Lista nie powinna być null");
        assertEquals(1, workers.size(), "Powinna być jedna pozycja w liście");

        Worker w = workers.get(0);
        assertEquals("Jan", w.getName(), "Imię powinno się zgadzać");
        assertEquals("Kowalski", w.getSurname(), "Nazwisko powinno się zgadzać");
        assertEquals("jan@example.com", w.getEmail(), "Email powinien się zgadzać");
        assertEquals(Position.PROGRAMISTA, w.getPosition(), "Pozycja powinna być PROGRAMISTA");

        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldReturnEmptyList_WhenApiReturnsEmptyArray() throws Exception {
        // Given
        String emptyJson = "[]";
        
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(emptyJson);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        
        // When
        List<Worker> workers = apiService.fetchEmployeesFromAPI();
        
        // Then
        assertNotNull(workers, "Lista nie powinna być null");
        assertTrue(workers.isEmpty(), "Lista powinna być pusta");
        assertEquals(0, workers.size(), "Rozmiar listy powinien być 0");
        
        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }


    @Test
    void shouldParseMultipleWorkers_WhenApiReturnsMultipleEntries() throws Exception {
        String json = """
            [
                {"name": "Jan Kowalski", "email": "jan@example.com", "company": {"name": "ABC"}},
                {"name": "Anna Nowak", "email": "anna@example.com", "company": {"name": "XYZ"}},
                {"name": "Piotr Wiśniewski", "email": "piotr@example.com", "company": {"name": "DEF"}}
            ]
        """;
        
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(json);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        
        List<Worker> workers = apiService.fetchEmployeesFromAPI();
        
        assertNotNull(workers, "Lista nie powinna być null");
        assertEquals(3, workers.size(), "Powinno być 3 pracowników");
        
        assertEquals("Jan", workers.get(0).getName(), "Pierwszy pracownik - imię");
        assertEquals("Kowalski", workers.get(0).getSurname(), "Pierwszy pracownik - nazwisko");
        
        assertEquals("Anna", workers.get(1).getName(), "Drugi pracownik - imię");
        assertEquals("Nowak", workers.get(1).getSurname(), "Drugi pracownik - nazwisko");
        
        assertEquals("Piotr", workers.get(2).getName(), "Trzeci pracownik - imię");
        assertEquals("Wiśniewski", workers.get(2).getSurname(), "Trzeci pracownik - nazwisko");
        
        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
    

    @Test
    void shouldThrowApiException_WhenHttpStatusIsNot200() throws Exception {
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        ApiException ex = assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromAPI());
        assertTrue(ex.getMessage().contains("HTTP error"), "Wiadomość powinna informować o błędzie HTTP");

        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldThrowApiException_WhenHttpStatusIs404() throws Exception {
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn("Not Found");
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        ApiException ex = assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromAPI());
        assertTrue(ex.getMessage().contains("HTTP error"), "Powinien zostać rzucony ApiException dla 404");

        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldThrowApiException_WhenJsonIsMalformed() throws Exception {
        String invalidJson = "{ invalid json ]";

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(invalidJson);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromAPI());

        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldThrowApiException_WhenHttpClientThrowsIOException() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network is down"));

        ApiException ex = assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromAPI());
        assertTrue(ex.getMessage().toLowerCase().contains("błąd połączenia") || ex.getMessage().toLowerCase().contains("connection"),
                "ApiException powinien zawierać informację o błędzie połączenia");

        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void shouldThrowApiException_WhenHttpClientIsInterrupted() throws Exception {
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Interrupted"));

        ApiException ex = assertThrows(ApiException.class, () -> apiService.fetchEmployeesFromAPI());
        assertTrue(ex.getMessage().toLowerCase().contains("błąd połączenia") || ex.getMessage().toLowerCase().contains("interrupted"),
                "ApiException powinien obsłużyć InterruptedException");

        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}
