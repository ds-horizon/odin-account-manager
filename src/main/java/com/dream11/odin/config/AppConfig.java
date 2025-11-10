package com.dream11.odin.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppConfig {
  Map<String, Object> mysql = new HashMap<>();
}
