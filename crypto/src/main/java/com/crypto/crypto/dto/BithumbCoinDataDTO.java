package com.crypto.crypto.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BithumbCoinDataDTO {   //Daily

  private String coin;
  private LocalDateTime candleDateTime;
  private String highPrice;
  private String lowPrice;

}
