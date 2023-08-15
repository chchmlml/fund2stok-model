package io.haicheng.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiSeasonStockData {
    String id;
    String code;
    String name;
    String rate;
    String hold;
    String amount;
}
