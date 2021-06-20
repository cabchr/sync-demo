package utils;

import model.Fixture;
import model.FixtureStatistics;
import model.Pair;
import model.TeamStats;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PostUtils {

    public static void postMatch(Pair<Fixture, FixtureStatistics> match) throws IOException {
        postMatch(match, false);
    }
    public static void postMatch(Pair<Fixture, FixtureStatistics> match, boolean debug) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://localhost:8080/matches/report").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String json = matchToJson(match);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            if (debug) {
                System.out.println("step1");
            }
        } catch (Exception e) {
            System.out.println(json);
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            if (debug) {
                System.out.println("step2");
            }
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            if (debug) {
                System.out.println("step3");
            }
            if (debug) {
                System.out.println(response);
            }
        } catch (Exception e) {
            System.out.println(json);
            e.printStackTrace();
        }


        connection.disconnect();
    }

    public static void trust() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        SSLContext tls = SSLContext.getInstance("TLS");
        tls.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static String matchToJson(Pair<Fixture, FixtureStatistics> match) {
        TeamStats team1Stats = match.getR().getTeam1();
        TeamStats team2Stats = match.getR().getTeam2();
        return "{\n" +
                "  \"matchid\": \"" + String.format("%s_%s_%s",
                                        match.getL().getTeam1Name(), match.getL().getTeam2Name(),
                                        LocalDateTime.now().minusMinutes(match.getL().getTime().getMinutes()).toLocalDate()) + "\",\n" +
                "  \"team1name\": \"" + match.getL().getTeam1Name() + "\",\n" +
                "  \"team2name\": \"" + match.getL().getTeam2Name() + "\",\n" +
                "  \"team1attacks\": " + team1Stats.getAngreb() + ",\n" +
                "  \"team2attacks\": " + team2Stats.getAngreb() + ",\n" +
                "  \"team1dangerousattacks\": " + team1Stats.getFarligeAngreb() + ",\n" +
                "  \"team2dangerousattacks\": " + team2Stats.getFarligeAngreb() + ",\n" +
                "  \"team1possession\": " + team1Stats.getBesiddelse() + ",\n" +
                "  \"team2possession\": " + team2Stats.getBesiddelse() + ",\n" +
                "  \"team1shotsontarget\": " + team1Stats.getSkudPaaMaal() + ",\n" +
                "  \"team2shotsontarget\": " + team2Stats.getSkudPaaMaal() + ",\n" +
                "  \"team1shotsawayfromtarget\": " + team1Stats.getSkudVedSidenAfMaal() + ",\n" +
                "  \"team2shotsawayfromtarget\": " + team2Stats.getSkudVedSidenAfMaal() + ",\n" +
                "  \"team1goals\": " + team1Stats.getMaal() + ",\n" +
                "  \"team2goals\": " + team2Stats.getMaal() + ",\n" +
                "  \"team1winodds\": " + match.getL().getTeam1winOdds() + ",\n" +
                "  \"team2winodds\": " + match.getL().getTeam2winOdds() + ",\n" +
                "  \"drawodds\": " + match.getL().getDrawOdds() + ",\n" +
                "  \"minutesplayed\": " + match.getL().getTime().getMinutes() + ",\n" +
                "  \"secondsplayed\": " + match.getL().getTime().getSeconds() + "\n" +
                "}";
    }
}
