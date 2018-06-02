package com.rainbow.house.search.service.search;

import com.rainbow.house.search.RainbowSearchApplicationTests;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.rent.RentSearchCondition;
import com.rainbow.house.search.service.EsSearchService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>功能描述</br>Es的测试类</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/2 14:46
 */
public class EsServiceTest extends RainbowSearchApplicationTests {

  @Autowired
  private EsSearchService esSearchService;

  @Test
  public void indexTest() {
    Long houseId = 16L;
    esSearchService.indexVersionOne(houseId);
  }

  @Test
  public void removeIndexTest() {
    Long houseId = 16L;
    esSearchService.removeVersionOne(houseId);
  }

  @Test
  public void query() {
    RentSearchCondition rentSearchCondition = new RentSearchCondition();
    rentSearchCondition.setCityEnName("bj");
    rentSearchCondition.setStart(0);
    rentSearchCondition.setSize(10);
    ServiceMultiResult<Long> houseIds = esSearchService.query(rentSearchCondition);
    System.out.print(houseIds.getResults());
    Assert.assertEquals(2, houseIds.getTotal());
  }
}
