package kr.taeu.acnh.datasheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.Sheets;

import kr.taeu.util.GoogleSheetUtil;

public class SheetParser {
  private final String CREDENTIALS_FILE_PATH = "json/nookshelper.json";
  private final String SHEET_ID = "1mo7myqHry5r_TKvakvIhHbcEAEQpSiNoNQoIS8sMpvM";

  private final String[] ITEM_SHEETS = {
      "Housewares",
      "Miscellaneous",
      "Wall-mounted",
      "Wallpaper",
      "Floors",
      "Rugs",
      "Fencing",
      "Photos",
      "Posters",
      "Tools",
      "Tops",
      "Bottoms",
      "Dress-Up",
      "Headwear",
      "Accessories",
      "Socks",
      "Shoes",
      "Bags",
      "Umbrellas",
      "Music",
      "Fossils",
      "Other",
      "Art",
  };
  private final String[] CREATURE_SHEETS = {
      "Fish",
      "Insects",
  };
  private final String[] NOOK_MILE_SHEETS = {
      "NookMiles",
  };

  private final String[] RECIPE_SHEETS = {
      "Recipes",
  };

  private final String[] VILLAGERS_SHEETS = {
      "Villagers",
  };

  private final String[] CONSTRUCTION_SHEETS = {
      "Construction",
  };

  private final String[] ACHIEVEMENTS_SHEETS = {
      "Achievements",
  };

  private final String[] REACTIONS_SHEETS = {
      "Reactions",
  };
  
  public void parseData() {
    try {
      GoogleSheetUtil googleSheetUtil = new GoogleSheetUtil(CREDENTIALS_FILE_PATH);
      Sheets sheets = googleSheetUtil.createSheetsService();
      Map<String, List<List<Map<String,String>>>> retMap = new HashMap<>();
      Map<String, String[]> workSet = getWorkSet();
      
      for (String key : workSet.keySet()) {
        System.out.printf("workSet: %s\n", key);
        List<List<Map<String, String>>> dataList = new ArrayList<>();
        
        for (String sheetName : workSet.get(key)) {
          System.out.printf("sheetName: %s\n", sheetName);
          
          List<Map<String, String>> sheetData = googleSheetUtil.loadData(SHEET_ID, sheets, sheetName);
          dataList.add(sheetData);
        }
        retMap.put(key, dataList);
      }
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }
  }
  
  private void normalizeData(List<String> sheetData) {
    
  }
  
  private Map<String, String[]> getWorkSet() {
    Map<String, String[]> map = new HashMap<>();
    
    map.put("items", ITEM_SHEETS);
    map.put("creatures", CREATURE_SHEETS);
    map.put("nookMiles", NOOK_MILE_SHEETS);
    map.put("recipes", RECIPE_SHEETS);
    map.put("villagers", VILLAGERS_SHEETS);
    map.put("construction", CONSTRUCTION_SHEETS);
    map.put("achievements", ACHIEVEMENTS_SHEETS);
    map.put("reactions", REACTIONS_SHEETS);
    
    return map;
  }
  
  public static void main(String[] args) {
    SheetParser sheetParser = new SheetParser();
    
    sheetParser.parseData();
  }
}
