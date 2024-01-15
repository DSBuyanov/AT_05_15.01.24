package org.max.home.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class WeatherForecastTest extends AbstractTest {

    @Test
    void get_shouldReturn200() throws IOException, InterruptedException {
        // Определим заглушку WireMock для имитации успешного ответа от внешнего API
        wireMockServer.stubFor(get(urlEqualTo("/forecasts/v1/daily/5day"))
                .willReturn(aResponse().withStatus(200)));

        // Используем Mockito для имитации успешного выполнения запроса
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);
        given(objectMapperMock.readValue(any(InputStream.class), any(Class.class))).willReturn(new Object());

        // Выполним GET-запрос к API для получения прогноза погоды на 5 дней
        makeGetRequest(getBaseUrl() + "/forecasts/v1/daily/5day");


        TimeUnit.SECONDS.sleep(1);


        wireMockServer.verify(getRequestedFor(urlEqualTo("/forecasts/v1/daily/5day")));


    }

    private void makeGetRequest(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        assertEquals(200, response.getStatusLine().getStatusCode());


    }
}