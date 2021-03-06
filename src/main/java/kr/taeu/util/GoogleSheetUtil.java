package kr.taeu.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

/**
 * GoogleSheetUtil
 * Load a data from Google Sheet with Credential File
 * and Convert a Object data from sheet to List (including row that is Map)
 * @author Taeu(Lee Tae Woo)
 * @since 2020-05-11
 */
public class GoogleSheetUtil {
  private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  private final String credentialsFilePath;
  
  public GoogleSheetUtil(final String credentialsFilePath) {
    this.credentialsFilePath = credentialsFilePath;
  }
  
  private GoogleCredentials getGoogleCredentials() throws IOException, GeneralSecurityException {
    final InputStream is = getClass().getClassLoader().getResourceAsStream(credentialsFilePath);
    final GoogleCredentials credentials = GoogleCredentials
        .fromStream(is)
        .createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS_READONLY));
    
    return credentials;
  }
  
  public Sheets createSheetsService() throws IOException, GeneralSecurityException {
    final HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(getGoogleCredentials());
    
    final Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
        .setApplicationName("Google Sheets API")
        .build();
    
    return sheets;
  }
  
  public List<Map<String, String>> loadData(final String SheetId, final Sheets sheets, final String sheetName) throws IOException {
    final ValueRange response = sheets.spreadsheets().values()
        .get(SheetId, sheetName)
        .setValueRenderOption("FORMULA")
        .execute();
    
    final ObjectMapper objectMapper = new ObjectMapper();
    final Map<String, Object> map = objectMapper.readValue(response.toPrettyString()
        , new TypeReference<Map<String, Object>>(){});

    final ArrayList<ArrayList<Object>> rawDatas = (ArrayList<ArrayList<Object>>) map.get("values");
    
    if (rawDatas == null || rawDatas.isEmpty()) {
      throw new RuntimeException(sheetName + ": No Data.");
    }

    final List<Map<String, String>> dataList = new ArrayList<>();
    final ArrayList<Object> headers = rawDatas.get(0);
    
    for (int i = 1; i < rawDatas.size(); i++) {
      dataList.add(combine(headers, rawDatas.get(i)));
    }
    
    return dataList;
  }
  
  private Map<String, String> combine(final ArrayList<Object> headers, final ArrayList<Object> row) {
    final Map<String, String> result = new HashMap<>();
    
    for(int i = 0; i < headers.size(); i++) {
      final String rowData = (row.size() > i) ? String.valueOf(row.get(i)) : null; 
      
      result.put(String.valueOf(headers.get(i)), rowData);
    }
    
    return result;
  }
}
