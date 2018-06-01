package com.rainbow.house.search.web.controller.house;

import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.rent.RentSearchCondition;
import com.rainbow.house.search.base.rent.RentValueBlock;
import com.rainbow.house.search.entity.HouseDO;
import com.rainbow.house.search.service.HouseService;
import com.rainbow.house.search.service.SupportAddressService;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * <p>功能描述</br>租赁控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/6/1 10:33
 */
@Controller
@RequestMapping("rent")
public class RentController {

  @Autowired
  private SupportAddressService supportAddressService;

  @Autowired
  private HouseService houseService;

  @GetMapping("rent/house")
  public String rentHomePage(@ModelAttribute RentSearchCondition rentSearchCondition,
                             Model model, HttpSession session,
                             RedirectAttributes redirectAttributes) {
    if (rentSearchCondition.getCityEnName() == null) {
      String cityEnNameInSession = (String) session.getAttribute("cityEnName");
      if (cityEnNameInSession == null) {
        redirectAttributes.addAttribute("msg", "must_choose_city");
        return "redirect:/index";
      } else {
        rentSearchCondition.setCityEnName(cityEnNameInSession);
      }
    } else {
      session.setAttribute("cityEnName", rentSearchCondition.getCityEnName());
    }

    ServiceResult<SupportAddressDTO> city = supportAddressService.findCity(rentSearchCondition.getCityEnName());
    if (!city.isSuccess()) {
      redirectAttributes.addAttribute("msg", "must_choose_city");
      return "redirect:/index";
    }

    ServiceMultiResult<SupportAddressDTO> regionsResult = supportAddressService.findAllRegionsByCityName(rentSearchCondition.getCityEnName());
    if (regionsResult.getResults() == null || regionsResult.getTotal() < 1) {
      redirectAttributes.addAttribute("msg", "must_choose_city");
      return "redirect:/index";
    }
    /** 查询房间信息 **/
    ServiceMultiResult<HouseDTO> houses = houseService.queryHouses(rentSearchCondition);

    model.addAttribute("total", houses.getTotal());
    model.addAttribute("houses", houses.getResults());

    if (rentSearchCondition.getRegionEnName() == null) {
      rentSearchCondition.setRegionEnName("*");
    }

    model.addAttribute("searchBody", rentSearchCondition);
    model.addAttribute("regions", regionsResult.getResults());

    model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
    model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);

    model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearchCondition.getPriceBlock()));
    model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentSearchCondition.getAreaBlock()));

    return "rent-list";
  }
}
