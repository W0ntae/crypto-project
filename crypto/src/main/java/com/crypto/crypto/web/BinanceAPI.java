package com.crypto.crypto.web;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.crypto.crypto.dto.BinanceCoinDataDTO;
import com.crypto.crypto.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BinanceAPI {
  
  private final BinanceService binanceService;

  public String getData(String coin, String date) throws IOException, InterruptedException {
    //호출할 코인 URL 빌드
    StringBuilder sb = new StringBuilder();
    sb.append("https://api.binance.com/api/v3/klines?symbol=")
      .append(coin)
      .append("BUSD&interval=1d")
//      .append("&startTime=1676159999999")
      .append("&endTime=")
      .append(date)
      .append("&limit=1000");


    //업비트 API 요청, JSON 형식으로 데이터 반환
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(sb.toString()))
      .header("accept", "application/json")
      .method("GET", HttpRequest.BodyPublishers.noBody())
      .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("response.statusCode() = " + response.statusCode());

    System.out.println(date);

    System.out.println("Binance " + coin + " 요청");
    return response.body();
  }
  
  public void buildHistory(String[] coins) throws  Exception{
    LocalDateTime localDateTime = LocalDateTime.parse("2023-02-10T00:00:00");
    String date = String.valueOf(localDateTime.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
  
    for(int i = 0; i< coins.length; i++){
      while(true){
        String data = getData(coins[i], date);
    
    
        data = data.replace("[[", "[");
        data = data.replace("]]", "]");
    
        String[] coinData = data.split("],");
    
        if(date.equals(coinData[0].split(",")[0].replace("[", "")) || data.length() < 3 ){
          System.out.println("done");
          break;
        }
    
        date = coinData[0].split(",")[0].replace("[", "");
    
        for (int j = 0; j < coinData.length; j++) {
          coinData[j] = coinData[j].replace("[", "");
          coinData[j] = coinData[j].replace("]", "");
          coinData[j] = coinData[j].replace("\"", "");
          String[] dailyData = coinData[j].split(",");
          BinanceCoinDataDTO binanceCoinDataDTO = binanceCoinDataDtoFromStringArray(dailyData, coins[i]);
      
          binanceService.addData(binanceCoinDataDTO);
        }
      }
    }
  }
  
  public LocalDateTime milliToLocalDateTime(String milli) {
    String result = String.format("%.0f", Double.parseDouble(milli));
    
    return Instant.ofEpochMilli(Long.parseLong(result))
            .atZone(ZoneId.of("UTC"))
            .toLocalDateTime();
  }
  
  public BinanceCoinDataDTO binanceCoinDataDtoFromStringArray(String[] dailyData, String coinName) {
    LocalDateTime openTime = milliToLocalDateTime(dailyData[0]);
    LocalDateTime closeTime = milliToLocalDateTime(dailyData[6]);
    
    BinanceCoinDataDTO binanceCoinDataDTO = BinanceCoinDataDTO.builder()
            .coin(coinName)
            .openTime(openTime)
            .openingPrice(dailyData[1])
            .highPrice(dailyData[2])
            .lowPrice(dailyData[3])
            .closePrice(dailyData[4])
            .build();
    
    return binanceCoinDataDTO;
  }
}
