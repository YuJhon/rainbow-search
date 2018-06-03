package com.rainbow.house.search.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.rent.RentSearchCondition;
import com.rainbow.house.search.base.rent.RentValueBlock;
import com.rainbow.house.search.base.search.HouseIndexKey;
import com.rainbow.house.search.base.search.HouseIndexMessage;
import com.rainbow.house.search.base.search.HouseIndexTemplate;
import com.rainbow.house.search.base.search.HouseSuggest;
import com.rainbow.house.search.entity.HouseDO;
import com.rainbow.house.search.entity.HouseDetailDO;
import com.rainbow.house.search.entity.HouseTagDO;
import com.rainbow.house.search.repository.HouseDetailRepository;
import com.rainbow.house.search.repository.HouseRepository;
import com.rainbow.house.search.repository.HouseTagRepository;
import com.rainbow.house.search.service.EsSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>功能描述</br>Es搜索服务的业务逻辑接口实现</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 13:55
 */
@Service
@Slf4j
public class EsSearchServiceImpl implements EsSearchService {

  private static final String INDEX_NAME = "rainbow-search";

  private static final String INDEX_TYPE = "house";

  private static final String INDEX_TOPIC = "house-build";

  private static final int MAX_RETRY = 3;

  @Autowired
  private HouseRepository houseRepository;

  @Autowired
  private HouseDetailRepository houseDetailRepository;

  @Autowired
  private HouseTagRepository houseTagRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private TransportClient esClient;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @KafkaListener(topics = INDEX_TOPIC)
  private void handleMessage(String content) {
    try {
      HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);
      switch (message.getOperation()) {
        case HouseIndexMessage.INDEX:
          this.createOrUpdateIndex(message);
          break;
        case HouseIndexMessage.REMOVE:
          this.removeVersionTwo(message);
          break;
        default:
          log.warn("Not Support Message Content:{}", content);
          break;
      }
    } catch (IOException e) {
      log.error("Cannot parse json for " + content, e);
    }
  }


  /**
   * <pre>创建或者更新索引</pre>
   *
   * @param message 消息队列消息
   */
  private void createOrUpdateIndex(HouseIndexMessage message) {

    Long houseId = message.getHouseId();

    HouseDO house = houseRepository.findOne(houseId);
    if (house == null) {
      log.error("Index house {} dose not exist!", houseId);
      this.indexVersionTwo(houseId, message.getRetry() + 1);
      return;
    }
    /** 基础信息 **/
    HouseIndexTemplate indexTemplate = modelMapper.map(house, HouseIndexTemplate.class);

    /** 详细信息获取 **/
    HouseDetailDO houseDetail = houseDetailRepository.findByHouseId(houseId);
    if (houseDetail == null) {
      // TODO Exception Process
    }
    modelMapper.map(houseDetail, indexTemplate);

    /** 标签信息 **/
    List<HouseTagDO> houseTagList = houseTagRepository.findAllByHouseId(houseId);
    if (houseTagList != null && houseTagList.size() > 0) {
      List<String> tags = new ArrayList<>();
      houseTagList.forEach(houseTagDO -> {
        tags.add(houseTagDO.getName());
      });
      indexTemplate.setTags(tags);
    }

    /** es中先查询一下  **/
    SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));

    log.debug("Search House {} Index,{}", houseId, builder);
    SearchResponse response = builder.get();

    boolean success;
    long totalHits = response.getHits().totalHits;
    if (totalHits == 0) {
      success = this.create(indexTemplate);
    } else if (totalHits == 1) {
      String esId = response.getHits().getAt(0).getId();
      success = this.update(esId, indexTemplate);
    } else {
      success = deleteAndCreate(totalHits, indexTemplate);
    }

    if (success) {
      log.debug("Index Success With House {}", houseId);
    }

  }

  @Override
  public void indexVersionOne(Long houseId) {
    indexVersionTwo(houseId, 0);
  }

  @Override
  public void indexVersionTwo(Long houseId, int retry) {
    if (retry > MAX_RETRY) {
      log.error("Retry Index Times Over Three For House:{}", houseId);
      return;
    }
    HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
    try {
      kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      log.error("Json Encode Error For:{}", message);
    }
  }

  @Override
  public void removeVersionOne(Long houseId) {
    this.remove(houseId, 0);
  }

  @Override
  public ServiceMultiResult<Long> query(RentSearchCondition rentSearchCondition) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    /** 城市查询 **/
    boolQuery.filter(
            QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearchCondition.getCityEnName())
    );
    /** 区域的查询 **/
    if (rentSearchCondition.getRegionEnName() != null && !"*".equals(rentSearchCondition.getRegionEnName())) {
      boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearchCondition.getRegionEnName()));
    }
    /** 面积查询 **/
    RentValueBlock area = RentValueBlock.matchArea(rentSearchCondition.getAreaBlock());
    if (!RentValueBlock.ALL.equals(area)) {
      RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
      if (area.getMax() > 0) {
        rangeQueryBuilder.lte(area.getMax());
      }
      if (area.getMin() > 0) {
        rangeQueryBuilder.gte(area.getMin());
      }
      boolQuery.filter(rangeQueryBuilder);
    }

    /** 价格查询 **/
    RentValueBlock price = RentValueBlock.matchPrice(rentSearchCondition.getPriceBlock());
    if (!RentValueBlock.ALL.equals(price)) {
      RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
      if (price.getMax() > 0) {
        rangeQuery.lte(price.getMax());
      }
      if (price.getMin() > 0) {
        rangeQuery.gte(price.getMin());
      }
      boolQuery.filter(rangeQuery);
    }
    /** 房屋朝向 **/
    if (rentSearchCondition.getDirection() > 0) {
      boolQuery.filter(
              QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearchCondition.getDirection())
      );
    }
    /** 房租租赁方式 **/
    if (rentSearchCondition.getRentWay() > -1) {
      boolQuery.filter(
              QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearchCondition.getRentWay())
      );
    }

    /** 提升权重(title) [must/should]**/
    boolQuery.should(
            QueryBuilders.matchQuery(HouseIndexKey.TITLE, rentSearchCondition.getKeywords())
                    .boost(2.0f)
    );

    /** 关键词搜素 **/
    boolQuery.should(
            QueryBuilders.multiMatchQuery(rentSearchCondition.getKeywords(),
                    HouseIndexKey.TRAFFIC,
                    HouseIndexKey.DISTRICT,
                    HouseIndexKey.ROUND_SERVICE,
                    HouseIndexKey.SUBWAY_LINE_NAME,
                    HouseIndexKey.SUBWAY_STATION_NAME
            ));

    /** 执行索引 **/
    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(boolQuery)
            .setFrom(rentSearchCondition.getStart())
            .setSize(rentSearchCondition.getSize())
            /** 只查询固定的houseId字段 **/
            .setFetchSource(HouseIndexKey.HOUSE_ID, null);

    log.debug(requestBuilder.toString());

    List<Long> houseIds = new ArrayList<>();
    SearchResponse response = requestBuilder.get();
    if (response.status() != RestStatus.OK) {
      log.warn("Search Status Is Not Ok For {}", requestBuilder);
      return new ServiceMultiResult<>(0, houseIds);
    }

    for (SearchHit hit : response.getHits()) {
      houseIds.add(Longs.tryParse(String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))));
    }

    return new ServiceMultiResult<>(response.getHits().totalHits, houseIds);
  }

  @Override
  public ServiceResult<List<String>> suggest(String prefix) {
    CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);
    SuggestBuilder suggestBuilder = new SuggestBuilder();
    suggestBuilder.addSuggestion("autocomplete", suggestion);

    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .suggest(suggestBuilder);
    log.debug(requestBuilder.toString());
    SearchResponse response = requestBuilder.get();
    Suggest suggest = response.getSuggest();
    if (suggest == null) {
      return ServiceResult.result(new ArrayList<>());
    }

    Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

    int maxSuggest = 0;
    Set<String> suggestSet = new HashSet<>();
    for (Object term : result.getEntries()) {
      if (term instanceof CompletionSuggestion.Entry) {
        CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
        if (item.getOptions().isEmpty()) {
          continue;
        }

        for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
          String tip = option.getText().string();
          if (suggestSet.contains(tip)) {
            continue;
          }
          suggestSet.add(tip);
          maxSuggest++;
        }
      }
      if (maxSuggest > 5) {
        break;
      }
    }
    List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
    return ServiceResult.result(suggests);
  }

  @Override
  public ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
    /** 构建查询条件 **/
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
            .filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName))
            .filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, regionEnName))
            .filter(QueryBuilders.termQuery(HouseIndexKey.DISTRICT, district));
    /** 组织查询 **/
    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(boolQuery)
            .addAggregation(
                    AggregationBuilders.terms(HouseIndexKey.AGG_DISTRICT).field(HouseIndexKey.DISTRICT)
            ).setSize(0);

    log.debug(requestBuilder.toString());
    /** 获取结果 **/
    SearchResponse response = requestBuilder.get();
    if (response.status() != RestStatus.OK) {
      log.warn("Failed To Aggregate For:{}", HouseIndexKey.AGG_DISTRICT);
    } else {
      Terms terms = response.getAggregations().get(HouseIndexKey.AGG_DISTRICT);
      if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
        return ServiceResult.result(terms.getBucketByKey(district).getDocCount());
      }
    }
    return ServiceResult.result(0L);
  }

  /**
   * <pre>更新搜索建议</pre>
   *
   * @param indexTemplate
   * @return
   */
  private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
    AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(
            this.esClient, AnalyzeAction.INSTANCE, INDEX_NAME, indexTemplate.getTitle(),
            indexTemplate.getLayoutDesc(), indexTemplate.getRoundService(),
            indexTemplate.getDescription(), indexTemplate.getSubwayLineName(),
            indexTemplate.getSubwayStationName());

    requestBuilder.setAnalyzer("ik_smart");

    AnalyzeResponse response = requestBuilder.get();
    List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
    if (tokens == null) {
      log.warn("Can Not Analyze Token For House: " + indexTemplate.getHouseId());
      return false;
    }

    List<HouseSuggest> suggests = new ArrayList<>();
    for (AnalyzeResponse.AnalyzeToken token : tokens) {
      /** 排序数字类型 & 小于2个字符的分词结果 **/
      if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
        continue;
      }

      HouseSuggest suggest = new HouseSuggest();
      suggest.setInput(token.getTerm());
      suggests.add(suggest);
    }

    /** 定制化小区自动补全 **/
    HouseSuggest suggest = new HouseSuggest();
    suggest.setInput(indexTemplate.getDistrict());
    suggests.add(suggest);

    indexTemplate.setSuggest(suggests);
    return true;
  }

  /**
   * <pre>移除索引（消息队列的方式）</pre>
   *
   * @param houseId 房产Id
   * @param retry   重试次数
   */
  private void remove(Long houseId, int retry) {
    if (retry > HouseIndexMessage.MAX_RETRY) {
      log.error("Retry remove times over 3 for house: " + houseId + " Please check it!");
      return;
    }

    HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
    try {
      this.kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      log.error("Cannot Encode Json For " + message, e);
    }
  }

  /**
   * <pre>移除索引</pre>
   *
   * @param message 消息队列消息
   */
  private void removeVersionTwo(HouseIndexMessage message) {
    long houseId = message.getHouseId();
    DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
            .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
            .source(INDEX_NAME);
    log.debug("Delete query for house {}", builder);
    BulkByScrollResponse response = builder.get();
    long deleted = response.getDeleted();
    log.debug("Remove With Index House {}-个数{}", houseId, deleted);
    if (deleted <= 0) {
      this.remove(houseId, message.getRetry() + 1);
    }
  }


  /**
   * <pre>创建索引</pre>
   *
   * @param indexTemplate 索引对应的结构
   * @return
   */
  private boolean create(HouseIndexTemplate indexTemplate) {
    /** 更新搜索建议 **/
    if (!updateSuggest(indexTemplate)) {
      return false;
    }
    try {
      IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
              .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON)
              .get();

      log.debug("Create index with house {}", indexTemplate.getHouseId());

      if (response.status() == RestStatus.CREATED) {
        return true;
      } else {
        return false;
      }
    } catch (JsonProcessingException e) {
      log.error("Error to Index house {}-{}", indexTemplate.getHouseId(), e);
      return false;
    }
  }

  /**
   * <pre>更新索引</pre>
   *
   * @param esId          索引的唯一标识
   * @param indexTemplate 索引对应的内容
   * @return
   */
  private boolean update(String esId, HouseIndexTemplate indexTemplate) {
    /** 更新搜索建议 **/
    if (!updateSuggest(indexTemplate)) {
      return false;
    }
    try {
      UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
              .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON)
              .get();

      log.debug("Update index with house {}", indexTemplate.getHouseId());

      if (response.status() == RestStatus.OK) {
        return true;
      } else {
        return false;
      }
    } catch (JsonProcessingException e) {
      log.error("Error to Index house {}-{}", indexTemplate.getHouseId(), e);
      return false;
    }
  }

  /**
   * <pre>删除索引</pre>
   *
   * @param totalHit      需要删除的个数
   * @param indexTemplate 索引内容
   * @return
   */
  private boolean deleteAndCreate(Long totalHit, HouseIndexTemplate indexTemplate) {
    DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
            .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId()))
            .source(INDEX_NAME);
    log.debug("Delete query for house {}", builder);
    BulkByScrollResponse response = builder.get();
    long deleted = response.getDeleted();
    if (deleted != totalHit) {
      log.warn("Need delete {}, but {} was deleted.", totalHit, deleted);
      return false;
    } else {
      return create(indexTemplate);
    }
  }
}
