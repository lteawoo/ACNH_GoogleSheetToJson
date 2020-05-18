package kr.taeu.acnh.datasheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.CaseUtils;

import com.google.api.services.sheets.v4.Sheets;

import kr.taeu.acnh.datasheet.valueformatter.ValueFormatter;
import kr.taeu.acnh.datasheet.valueformatter.ValueFormatters;
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
  
//  private final String[] NOOK_MILE_SHEETS = {
//      "NookMiles",
//  };

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
  
  private final String[] NULL_VALUES = {
      "None",
      "NA",
      "Does not play music",
      "No lighting",
      "",
  };
  
  public void parseData() {
    try {
      GoogleSheetUtil googleSheetUtil = new GoogleSheetUtil(CREDENTIALS_FILE_PATH);
      Sheets sheets = googleSheetUtil.createSheetsService();
      Map<String, List<List<Map<String, Object>>>> retMap = new HashMap<>();
      Map<String, String[]> workSet = getWorkSet();
      
      for (String key : workSet.keySet()) {
        System.out.printf("workSet: %s\n", key);
        List<List<Map<String, Object>>> dataList = new ArrayList<>();
        
        for (String sheetName : workSet.get(key)) {
          System.out.printf("sheetName: %s\n", sheetName);
          
          List<Map<String, Object>> sheetData = normalizeData(googleSheetUtil.loadData(SHEET_ID, sheets, sheetName), sheetName);
          
          dataList.add(sheetData);
        }
        retMap.put(key, dataList);
      }
    } catch (IOException | GeneralSecurityException e) {
      e.printStackTrace();
    }
  }
  
  private List<Map<String, Object>> normalizeData(List<Map<String, String>> sheetData, String sheetName) {
    for(Map<String, String> row : sheetData) {
      Map<String, Object> normalizedRow = new HashMap<>();
      // 1.Normalize keys
      for(final String originalKey : row.keySet()) {
        String key = "";
        
        // Need to convert # to "num" because toCamelCase converts it to an empty string
        if(originalKey.equals("#")) {
          key = "num";
        } else {
          key = CaseUtils.toCamelCase(originalKey, false);
        }
        
        // 2.Normalize values
        Object value = row.get(originalKey);
        
        if(value != null) {
          value = String.valueOf(value).trim();
          
          ValueFormatter<?> valueFormatter = getValueFormatter(key);
          
          if (valueFormatter != null) {
            value = valueFormatter.format(String.valueOf(value));
          }
          
          if (Arrays.stream(NULL_VALUES).anyMatch(value::equals)) {
            value = null;
          } else if (value.equals("Yes")) {
            value = "true";
          } else if (value.equals("No")) {
            value = "false";
          } else if (value.equals("NFS")) { // Not for sale
            value = "-1";
          }
        } else {
          value = null;
        }
        
        normalizedRow.put(key, value);
      }
      
      if (sheetName.equals("items")) {
        if(normalizedRow.get("color1") == null) {
          
        }
        Object[] colors = { normalizedRow.get("color1"), normalizedRow.get("color2") };
        colors = nullToFalse(colors);
        normalizedRow.put("colors", colors);
        
        Object[] themes = { normalizedRow.get("hhaConcept1"), normalizedRow.get("hhaConcept2") };
        themes = nullToFalse(themes);
        normalizedRow.put("themes", themes);
        
        Object[] labelThemes = String.valueOf(normalizedRow.get("labelThemes"));
      }
    }
    
    // TODO nomalizeData
    List<Map<String, Object>> nomalized = new ArrayList<>();
    
    return nomalized;
  }
  
  private Object[] nullToFalse(Object[] arr) {
    return Arrays.stream(arr)
        .map(item -> item == null ? "false" : item)
        .toArray();
  }
  
  private ValueFormatter<?> getValueFormatter(String key) {
    ValueFormatter<?> ret = null;
    
    switch(key) {
    case "image": ret = ValueFormatters.extractImageUrl(); break;
    case "house": ret = ValueFormatters.extractImageUrl(); break;
    case "furnitureImage": ret = ValueFormatters.extractImageUrl(); break;
    case "critterpediaImage": ret = ValueFormatters.extractImageUrl(); break;
    case "closetImage": ret = ValueFormatters.extractImageUrl(); break;
    case "storageImage": ret = ValueFormatters.extractImageUrl(); break;
    case "albumImage": ret = ValueFormatters.extractImageUrl(); break;
    case "framedImage": ret = ValueFormatters.extractImageUrl(); break;
    case "iconImage": ret = ValueFormatters.extractImageUrl(); break;
    case "houseImage": ret = ValueFormatters.extractImageUrl(); break;
    case "inventoryImage": ret = ValueFormatters.extractImageUrl(); break;
    case "uses": ret = ValueFormatters.normalizeUse(); break;
    case "source": ret = ValueFormatters.normalizeSource(); break;
    case "birthday": ret = ValueFormatters.normalizeBirthday(); break;
    };
    
    return ret;
  }
  
  private Map<String, String[]> getWorkSet() {
    Map<String, String[]> map = new HashMap<>();
    
    map.put("items", ITEM_SHEETS);
    map.put("creatures", CREATURE_SHEETS);
    // map.put("nookMiles", NOOK_MILE_SHEETS);
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
