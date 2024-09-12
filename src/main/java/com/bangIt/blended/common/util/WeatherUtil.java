package com.bangIt.blended.common.util;

import com.bangIt.blended.domain.dto.placesList.WeatherDTO;
import com.bangIt.blended.domain.enums.Region;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class WeatherUtil {
    
    @Value("${weather.api.key}")
    private String apiKey;

    private static final String SHORT_TERM_ENDPOINT = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final String MID_LAND_FCST_ENDPOINT = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
    private static final String MID_T_ENDPOINT = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";

    private final ObjectMapper objectMapper;

    // 주어진 기간의 날씨 정보를 조회하는 메인 메소드
    public List<WeatherDTO> getWeatherForPeriod(LocalDate checkinDate, LocalDate checkoutDate, Region region) throws IOException {
        List<WeatherDTO> weatherList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate shortTermEndDate = currentDate.plusDays(2);
        LocalDate midTermEndDate = currentDate.plusDays(10);

        System.out.println("체크인 날짜: " + checkinDate);
        System.out.println("체크아웃 날짜: " + checkoutDate);
        System.out.println("단기 예보 종료 날짜: " + shortTermEndDate);
        System.out.println("중기 예보 종료 날짜: " + midTermEndDate);

        if (checkinDate.isAfter(midTermEndDate)) {
            return null; // 10일 이후의 날짜는 예보 불가
        }

        // 단기 예보 조회 (3일까지)
        if (!checkinDate.isAfter(shortTermEndDate)) {
            LocalDate endDate = checkoutDate.isAfter(shortTermEndDate) ? shortTermEndDate : checkoutDate;
            weatherList.addAll(getShortTermWeather(checkinDate, endDate, region));
        }

        // 중기 예보 조회 (3일 이후부터 10일까지)
        if (checkoutDate.isAfter(shortTermEndDate)) {
            LocalDate startDate = checkinDate.isAfter(shortTermEndDate) ? checkinDate : shortTermEndDate.plusDays(1);
            weatherList.addAll(getMidTermWeather(startDate, checkoutDate, region));
        }

        return weatherList;
    }

    // 단기 날씨 정보 조회
    private List<WeatherDTO> getShortTermWeather(LocalDate startDate, LocalDate endDate, Region region) {
        List<WeatherDTO> shortTermList = new ArrayList<>();
        String jsonResponse = callShortTermApi(LocalDate.now(), region);

        if (jsonResponse != null) {
            try {
                JsonNode items = objectMapper.readTree(jsonResponse)
                    .path("response").path("body").path("items").path("item");

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    WeatherDTO weatherDTO = extractWeatherData(items, date);
                    if (weatherDTO != null) {
                        shortTermList.add(weatherDTO);
                    }
                }
            } catch (Exception e) {
                System.err.println("단기 예보 JSON 파싱 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return shortTermList;
    }

    // JSON 데이터에서 날씨 정보 추출
    private WeatherDTO extractWeatherData(JsonNode items, LocalDate date) {
        String dateString = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String weather = "";
        double temperature = 0;

        for (JsonNode item : items) {
            if (item.path("fcstDate").asText().equals(dateString)) {
                String category = item.path("category").asText();
                if ("SKY".equals(category)) {
                    weather = convertSkyCodeToWeather(item.path("fcstValue").asInt());
                } else if ("TMP".equals(category)) {
                    temperature = item.path("fcstValue").asDouble();
                }
            }
        }

        return weather.isEmpty() ? null : WeatherDTO.builder()
                .date(date)
                .weather(weather)
                .temperature(temperature)
                .forecastType("단기")
                .build();
    }

    // 중기 날씨 정보 조회
    private List<WeatherDTO> getMidTermWeather(LocalDate startDate, LocalDate endDate, Region region) throws IOException {
        String stnRegion = getRegionStnId(region);
        String landFcstRegion = getRegionLandFcstId(region);
        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";

        String midLandFcstResponse = callMidTermApi(MID_LAND_FCST_ENDPOINT, landFcstRegion, baseDate);
        String midTaResponse = callMidTermApi(MID_T_ENDPOINT, stnRegion, baseDate);

        return parseMidTermResponses(midLandFcstResponse, midTaResponse, startDate, endDate);
    }

    // 단기 예보 API 호출
    private String callShortTermApi(LocalDate date, Region region) {
        try {
            StringBuilder urlBuilder = new StringBuilder(SHORT_TERM_ENDPOINT);
            urlBuilder.append("?serviceKey=").append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
            urlBuilder.append("&pageNo=1");
            urlBuilder.append("&numOfRows=1000");
            urlBuilder.append("&dataType=JSON");
            urlBuilder.append("&base_date=").append(date.format(DateTimeFormatter.BASIC_ISO_DATE));
            urlBuilder.append("&base_time=0500");
            urlBuilder.append("&nx=").append(getRegionNx(region));
            urlBuilder.append("&ny=").append(getRegionNy(region));

            return makeApiCall(urlBuilder.toString());
        } catch (Exception e) {
            System.err.println("단기 예보 API 호출 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 중기 예보 API 호출
    private String callMidTermApi(String endPoint, String regionId, String baseDate) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(endPoint);
        urlBuilder.append("?serviceKey=").append(URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&numOfRows=10");
        urlBuilder.append("&dataType=JSON");
        urlBuilder.append("&regId=").append(regionId);
        urlBuilder.append("&tmFc=").append(baseDate);

        return makeApiCall(urlBuilder.toString());
    }

    // API 호출 실행 및 응답 반환
    private String makeApiCall(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        System.out.println("Request URL: " + url);
        System.out.println("Response code: " + conn.getResponseCode());

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300 ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8))) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("API Response: " + sb.toString());
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    // 중기 예보 응답 파싱
    private List<WeatherDTO> parseMidTermResponses(String landFcstResponse, String taResponse, LocalDate startDate, LocalDate endDate) {
        List<WeatherDTO> weatherList = new ArrayList<>();
        try {
            JsonNode landItems = objectMapper.readTree(landFcstResponse)
                .path("response").path("body").path("items").path("item").get(0);
            JsonNode taItems = objectMapper.readTree(taResponse)
                .path("response").path("body").path("items").path("item").get(0);

            int dayOffset = getDayOffset(startDate);
            for (LocalDate date = startDate; !date.isAfter(endDate) && dayOffset <= 10; date = date.plusDays(1), dayOffset++) {
                String weatherKey = dayOffset <= 7 ? "wf" + dayOffset + "Am" : "wf" + dayOffset;
                String weather = landItems.path(weatherKey).asText();
                if (weather.isEmpty() && dayOffset <= 7) {
                    weather = landItems.path("wf" + dayOffset + "Pm").asText();
                }

                WeatherDTO weatherDTO = WeatherDTO.builder()
                        .date(date)
                        .weather(weather)
                        .maxTemperature(taItems.path("taMax" + dayOffset).asDouble())
                        .minTemperature(taItems.path("taMin" + dayOffset).asDouble())
                        .forecastType("중기")
                        .build();
                weatherList.add(weatherDTO);
            }
        } catch (Exception e) {
            System.err.println("중기 예보 JSON 파싱 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return weatherList;
    }

    // 날짜 오프셋 계산
    private int getDayOffset(LocalDate date) {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), date) + 3;
    }

    // 하늘 상태 코드를 날씨 설명으로 변환
    private String convertSkyCodeToWeather(int skyCode) {
        switch (skyCode) {
            case 1: return "맑음";
            case 3: return "구름많음";
            case 4: return "흐림";
            default: return "알 수 없음";
        }
    }
   
    // 지역별 x 좌표 반환
    private int getRegionNx(Region region) {
        switch (region) {
            case SEOUL: return 60;
            case GYEONGGI: return 64;
            case GANGWON: return 73;
            case CHUNGCHEONG: return 67;
            case GYEONGSANG: return 87;
            case JEOLLA: return 98;
            case JEJU: return 52;
            case DOKDO: return 144;
            default: throw new IllegalArgumentException("Invalid region");
        }
    }

    // 지역별 y 좌표 반환
    private int getRegionNy(Region region) {
        switch (region) {
            case SEOUL: return 127;
            case GYEONGGI: return 128;
            case GANGWON: return 134;
            case CHUNGCHEONG: return 100;
            case GYEONGSANG: return 68;
            case JEOLLA: return 76;
            case JEJU: return 38;
            case DOKDO: return 123;
            default: throw new IllegalArgumentException("Invalid region");
        }
    }

    // 지역별 중기 예보 지점 ID 반환
    private String getRegionStnId(Region region) {
        switch (region) {
            case SEOUL: return "11B10101";
            case GYEONGGI: return "11B20502";
            case GANGWON: return "11D20403";
            case CHUNGCHEONG: return "11C20401";
            case GYEONGSANG: return "11H20401";
            case JEOLLA: return "11F20401";
            case JEJU: return "11G00201";
            case DOKDO: return "11E00102";
            default: throw new IllegalArgumentException("Invalid region");
        }
    }
    
    // 지역별 중기 육상 예보 ID 반환
    private String getRegionLandFcstId(Region region) {
        switch (region) {
            case SEOUL: return "11B00000";
            case GYEONGGI: return "11B00000";
            case GANGWON: return "11D10000";
            case CHUNGCHEONG: return "11C20000";
            case GYEONGSANG: return "11H20000";
            case JEOLLA: return "11F20000";
            case JEJU: return "11G00000";
            case DOKDO: return "11H10000";
            default: throw new IllegalArgumentException("Invalid region");
        }
    }
}