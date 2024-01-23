package com.example.aidan;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.*;

@RestController
public class Example {
    String logFilePath = "/Users/aidan.bordan/Downloads/aidan/logs/application.log";
    @RequestMapping("/")
    public String hello() {
        return "Go to localhost:8080/spotify for the actual website!";
    }

    @RequestMapping("/spotify/logs")
    public String aidan() {
        try {
            return readFileToString(logFilePath);
        } catch (Exception e) {
            return "Error retrieving logs.";
        }
    }

    private static String readFileToString(String filePath) throws IOException {
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<h1>Logs of site API calls</h1>");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("<br>");
            }
        }
        return stringBuilder.toString();
    }

    @RequestMapping("/spotify")
    public String spotify() {
        String returnString = "<h1>Random Shoegaze Song Finder</h1><br><p>";
        Track randomShoegazeTrack = ShoegazeTrack.getRandomShoegaze();
        try {
            returnString += randomShoegazeTrack.getName() + ", by ";
            returnString += randomShoegazeTrack.getArtists()[0].getName() + "<br>";
            returnString += "<iframe src=\"" + randomShoegazeTrack.getPreviewUrl() + "\"  frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\"></iframe>";
            returnString += "</a></p>";
        } catch (Exception e) {
            System.out.println("error");
        }
        return returnString;
    }
    @RestController
    @RequestMapping("/metrics")
    class PrometheusMetricsController {

        @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.ALL_VALUE)
        public void metrics(HttpServletResponse response) throws IOException {
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setHeader("Accept", MediaType.TEXT_PLAIN_VALUE);

            PrintWriter writer = response.getWriter();
            TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples());
            writer.flush();
        }

        @Bean
        public io.prometheus.client.CollectorRegistry collectorRegistry() {
            return io.prometheus.client.CollectorRegistry.defaultRegistry;
        }
    }

}


