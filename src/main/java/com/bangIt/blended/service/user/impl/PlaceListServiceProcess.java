package com.bangIt.blended.service.user.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.bangIt.blended.common.util.WeatherUtil;
import com.bangIt.blended.domain.dto.place.PlaceDTO;
import com.bangIt.blended.domain.dto.placesList.ScrapePlaceDTO;
import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;
import com.bangIt.blended.domain.dto.placesList.WeatherDTO;
import com.bangIt.blended.domain.enums.Region;
import com.bangIt.blended.domain.mapper.PlaceMapper;
import com.bangIt.blended.service.user.PlaceListService;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Service
public class PlaceListServiceProcess implements PlaceListService {
	
	private final PlaceMapper placeMapper;
	private final WeatherUtil weatherUtil;

	
	//일정 검색 서비스 
    @Override
    public void findPlaceProcess(SearchPlaceDTO dto, Model model) throws IOException {
    	    
    	   System.out.println("serchdto: " + dto);
    	    
    	    List<PlaceDTO> places = placeMapper.findprocess(dto);
    	    
    	    LocalDate checkinDate =dto.getCheckinDate();
    	    LocalDate checkoutDate =dto.getCheckoutDate();
    	    Region region = dto.getRegion();
    	    
    	    List<WeatherDTO> weather = weatherUtil.getWeatherForPeriod(checkinDate, checkoutDate, region);
    	    
    	  
    	    model.addAttribute("placeList", places);
    	    model.addAttribute("searchDto", dto);
    	    model.addAttribute("weather", weather);
    }
    
    

    
    
    //크롤링 작업
    public ScrapePlaceDTO scrapeProcess(String placeName) {
        try {
            Document yeogi = Jsoup.connect("https://www.yeogi.com/domestic-accommodations?searchType=KEYWORD&keyword=" + URLEncoder.encode(placeName, StandardCharsets.UTF_8))
                                  .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                  .get();

            Element scriptElement = yeogi.select("script#__NEXT_DATA__").first();
            if (scriptElement != null) {
                String jsonData = scriptElement.html();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject pageProps = jsonObject.getJSONObject("props").getJSONObject("pageProps");
                JSONArray accommodations = pageProps.getJSONArray("accommodationsData");

                if (accommodations.length() > 0) {
                    JSONObject firstAccommodation = accommodations.getJSONObject(0);
                    JSONObject meta = firstAccommodation.getJSONObject("meta");
                    JSONObject room = firstAccommodation.getJSONObject("room");
                    JSONObject stay = room.getJSONObject("stay");
                    JSONObject price = stay.getJSONObject("price");

                    return ScrapePlaceDTO.builder()
                        .name(meta.optString("name", ""))
                        .grade(meta.optString("grade", ""))
                        .imageUrl(meta.getJSONArray("images").optString(0, ""))
                        .location(meta.getJSONObject("address").optString("traffic", ""))
                        .price(String.valueOf(price.optInt("discountPrice", 0)))
                        .url("https://www.yeogi.com/domestic-accommodations/" + meta.optString("id", ""))
                        .build();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred: " + e.getMessage());
        }
        return null;
    }

	

	
	

}
