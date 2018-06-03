package com.rainbow.house.search.base.baidu.map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbow.house.search.base.ServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>功能描述</br>百度地图辅助类【虎鲸数据平台】</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/3 12:28
 */
@Component
@Slf4j
public class BaiduMapHelper {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String BAIDU_MAP_KEY = "6QtSF673D1pYl3eQkEXfwp8ZgsQpB77U";

  private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";

  /**
   * POI数据管理接口
   */
  private static final String LBS_CREATE_API = "http://api.map.baidu.com/geodata/v3/poi/create";

  private static final String LBS_QUERY_API = "http://api.map.baidu.com/geodata/v3/poi/list?";

  private static final String LBS_UPDATE_API = "http://api.map.baidu.com/geodata/v3/poi/update";

  private static final String LBS_DELETE_API = "http://api.map.baidu.com/geodata/v3/poi/delete";


  /**
   * <pre>获取百度地图位置</pre>
   *
   * @param city    城市
   * @param address 详细地址
   * @return
   */
  public ServiceResult<BaiduMapLocation> getBaiduMapLocation(String city, String address) {
    String encodeAddress;
    String encodeCity;

    try {
      encodeAddress = URLEncoder.encode(address, "UTF-8");
      encodeCity = URLEncoder.encode(city, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      log.error("Error to encode house address", e);
      return new ServiceResult<>(false, "Error to encode hosue address");
    }

    HttpClient httpClient = HttpClients.createDefault();
    StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API);
    sb.append("address=").append(encodeAddress).append("&")
            .append("city=").append(encodeCity).append("&")
            .append("output=json&")
            .append("ak=").append(BAIDU_MAP_KEY);

    HttpGet get = new HttpGet(sb.toString());
    try {
      HttpResponse response = httpClient.execute(get);
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        return new ServiceResult<>(false, "Can not get baidu map location");
      }

      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      JsonNode jsonNode = objectMapper.readTree(result);
      int status = jsonNode.get("status").asInt();
      if (status != 0) {
        return new ServiceResult<BaiduMapLocation>(false, "Error to get map location for status: " + status);
      }
      {
        BaiduMapLocation location = new BaiduMapLocation();
        JsonNode jsonLocation = jsonNode.get("result").get("location");
        location.setLongitude(jsonLocation.get("lng").asDouble());
        location.setLatitude(jsonLocation.get("lat").asDouble());
        return ServiceResult.result(location);
      }

    } catch (IOException e) {
      log.error("Error To Fetch Baidu Map Api", e);
      return new ServiceResult<>(false, "Error to fetch baidumap api");
    }
  }

  /**
   * <pre>删除lbs</pre>
   *
   * @param location 位置
   * @param title    标题
   * @param address  详细地址
   * @param houseId  房源Id
   * @param price    价格
   * @param area     区域
   * @return
   */
  public ServiceResult lbsUpload(BaiduMapLocation location, String title, String address, long houseId, int price, int area) {

    HttpClient httpClient = HttpClients.createDefault();

    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
    params.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())));
    /**  百度坐标系 **/
    params.add(new BasicNameValuePair("coord_type", "3"));
    /** 到虎鲸数据平台注册一个应用:http://lbsyun.baidu.com/data/mydata#/?_k=isek28
     * http://lbsyun.baidu.com/index.php?title=lbscloud
     * API文档：http://lbsyun.baidu.com/index.php?title=lbscloud/api/geodata**/
    params.add(new BasicNameValuePair("geotable_id", "175730"));
    params.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
    params.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));
    params.add(new BasicNameValuePair("price", String.valueOf(price)));
    params.add(new BasicNameValuePair("area", String.valueOf(area)));
    params.add(new BasicNameValuePair("title", title));
    params.add(new BasicNameValuePair("address", address));

    HttpPost post;
    if (isLbsDataExists(houseId)) {
      post = new HttpPost(LBS_UPDATE_API);
    } else {
      post = new HttpPost(LBS_CREATE_API);
    }

    try {
      post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
      HttpResponse response = httpClient.execute(post);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        log.error("Can not upload lbs data for response: " + result);
        return new ServiceResult(false, "Can not upload baidu lbs data");
      } else {
        JsonNode jsonNode = objectMapper.readTree(result);
        int status = jsonNode.get("status").asInt();
        if (status != 0) {
          String message = jsonNode.get("message").asText();
          log.error("Error to upload lbs data for status: {}, and message: {}", status, message);
          return new ServiceResult(false, "Error to upload lbs data");
        } else {
          return ServiceResult.success();
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ServiceResult(false);
  }

  /**
   * <pre>判断lbs数据是否存在</pre>
   *
   * @param houseId 房产Id
   * @return
   */
  private boolean isLbsDataExists(Long houseId) {
    HttpClient httpClient = HttpClients.createDefault();
    StringBuilder sb = new StringBuilder(LBS_QUERY_API);
    sb.append("geotable_id=").append("175730").append("&")
            .append("ak=").append(BAIDU_MAP_KEY).append("&")
            .append("houseId=").append(houseId).append(",").append(houseId);
    HttpGet get = new HttpGet(sb.toString());
    try {
      HttpResponse response = httpClient.execute(get);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        log.error("Can not get lbs data for response: " + result);
        return false;
      }

      JsonNode jsonNode = objectMapper.readTree(result);
      int status = jsonNode.get("status").asInt();
      if (status != 0) {
        log.error("Error to get lbs data for status: " + status);
        return false;
      } else {
        long size = jsonNode.get("size").asLong();
        if (size > 0) {
          return true;
        } else {
          return false;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * <pre>移除lbs数据</pre>
   *
   * @param houseId
   * @return
   */
  public ServiceResult removeLbs(Long houseId) {
    HttpClient httpClient = HttpClients.createDefault();
    List<NameValuePair> nvps = new ArrayList<>();
    nvps.add(new BasicNameValuePair("geotable_id", "175730"));
    nvps.add(new BasicNameValuePair("ak", BAIDU_MAP_KEY));
    nvps.add(new BasicNameValuePair("houseId", String.valueOf(houseId)));

    HttpPost delete = new HttpPost(LBS_DELETE_API);
    try {
      delete.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
      HttpResponse response = httpClient.execute(delete);
      String result = EntityUtils.toString(response.getEntity(), "UTF-8");
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        log.error("Error to delete lbs data for response: " + result);
        return new ServiceResult(false);
      }

      JsonNode jsonNode = objectMapper.readTree(result);
      int status = jsonNode.get("status").asInt();
      if (status != 0) {
        String message = jsonNode.get("message").asText();
        log.error("Error to delete lbs data for message: " + message);
        return new ServiceResult(false, "Error to delete lbs data for: " + message);
      }
      return ServiceResult.success();
    } catch (IOException e) {
      log.error("Error to delete lbs data.", e);
      return new ServiceResult(false);
    }
  }

}
