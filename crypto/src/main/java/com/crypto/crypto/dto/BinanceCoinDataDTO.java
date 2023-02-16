package com.crypto.crypto.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BinanceCoinDataDTO {

  private String coin;
  private LocalDateTime openTime;
  private String openingPrice;
  private String highPrice;
  private String lowPrice;
  private String closePrice;

}
