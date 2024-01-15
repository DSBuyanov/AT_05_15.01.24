package org.max.seminar.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.max.seminar.accu.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetLocationTest extends AbstractTest {

    private static final Logger logger
            = LoggerFactory.getLogger(GetLocationTest.class);

    @Test
    void get_shouldReturn200() throws IOException, URISyntaxException {
        logger.info("Тест код ответ 200 запущен");
        //given
        ObjectMapper mapper = new ObjectMapper();
        Location bodyOk = new Location();
        bodyOk.setKey("OK");

        Location bodyError = new Location();
        bodyError.setKey("Error");

        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("q", equalTo("Samara"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(bodyOk))));

        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("q", equalTo("error"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(bodyError))));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http клиент создан");
        //when

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/autocomplete");
        URI uriOk = new URIBuilder(request.getURI())
                .addParameter("q", "Samara")
                .build();
        request.setURI(uriOk);
        HttpResponse responseOk = httpClient.execute(request);

        URI uriError = new URIBuilder(request.getURI())
                .addParameter("q", "error")
                .build();
        request.setURI(uriError);

        HttpResponse responseError = httpClient.execute(request);

        //then

        verify(2, getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete")));
        assertEquals(200, responseOk.getStatusLine().getStatusCode());
        assertEquals(200, responseError.getStatusLine().getStatusCode());
        assertEquals("OK", mapper.readValue(responseOk.getEntity().getContent(), Location.class).getKey());
        assertEquals("Error", mapper.readValue(responseError.getEntity().getContent(), Location.class).getKey());


    }

    @Test
    void get_shouldReturn401() throws IOException, URISyntaxException {
        logger.info("Тест код ответ 401 запущен");
        //given
        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apiKey", notMatching("82c9229354f849e78efe010d94150807"))
                .willReturn(aResponse()
                        .withStatus(401).withBody("ERROR")));

        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apiKey", matching("82c9229354f849e78efe010d94150807"))
                .willReturn(aResponse().withStatus(200).withBody("Success")));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(getBaseUrl() + "/locations/v1/cities/autocomplete");

        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "A_82c9229354f849e78efe010d94150807")
                .build();
        request.setURI(uri);

        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);

        URI success = new URIBuilder(request.getURI()
        ).addParameter("apiKey", "82c9229354f849e78efe010d94150807")
                .build();
        request.setURI(success);
        HttpResponse response1 = httpClient.execute(request);
        //then


        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete")));
        assertEquals(401, response.getStatusLine().getStatusCode());
        assertEquals("ERROR", convertResponseToString(response));
        assertEquals(200, response1.getStatusLine().getStatusCode());
        assertEquals("Success", convertResponseToString(response1));

    }
    @Test
    @DisplayName("With Header Test")
    void getHeaderInResponseWithStub() throws IOException {
        logger.info("Тест код ответ 200 запущен");
        //given
        logger.debug("Формирование мока для POST /locations/v1/cities/autocomplete");

        stubFor(post(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withHeader("Practice", equalTo("application/x-www-form-urlencoded"))
                .willReturn(aResponse().withStatus(200).withBody("Seminar number 5")));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost request = new HttpPost(getBaseUrl() + "/locations/v1/cities/autocomplete");
        request.addHeader("Practice", "application/x-www-form-urlencoded");
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(postRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withHeader("Practice", equalTo("application/x-www-form-urlencoded")));
//        Arrays.stream(response.getAllHeaders()).forEach(header -> {
//            System.out.println(header.getName());
//            System.out.println(header.getValue());});
        //assertEquals("Practice", response.getAllHeaders());
        assertEquals("Seminar number 5",convertResponseToString(response));

    }
}
