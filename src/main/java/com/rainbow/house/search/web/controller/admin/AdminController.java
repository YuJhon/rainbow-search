package com.rainbow.house.search.web.controller.admin;

import com.rainbow.house.search.base.DataTableResponse;
import com.rainbow.house.search.base.RainbowApiResponse;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.base.constants.HouseOperationConstants;
import com.rainbow.house.search.base.enums.HouseStatusEnum;
import com.rainbow.house.search.entity.SupportAddressDO;
import com.rainbow.house.search.service.HouseService;
import com.rainbow.house.search.service.SubwayService;
import com.rainbow.house.search.service.SupportAddressService;
import com.rainbow.house.search.service.UserService;
import com.rainbow.house.search.web.dto.*;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * <p>功能描述</br>管理员控制器</p>
 *
 * @author jiangy19
 * @version v1.0
 * @projectName rainbow-search
 * @date 2018/5/30 22:26
 */
@Controller
@RequestMapping("admin")
public class AdminController {

  public static final int ADDRESS_SIZE = 2;

  @Autowired
  private HouseService houseService;

  @Autowired
  private SupportAddressService addressService;

  @Autowired
  private SubwayService subwayService;

  @Autowired
  private UserService userService;

  /**
   * <pre>后台管理中心</pre>
   *
   * @return
   */
  @GetMapping("/center")
  public String adminCenterPage() {
    return "admin/center";
  }

  /**
   * <pre>欢迎页面</pre>
   *
   * @return
   */
  @GetMapping("/welcome")
  public String welcomePage() {
    return "admin/welcome";
  }

  /**
   * <pre>管理员登录页面</pre>
   *
   * @return
   */
  @GetMapping("/login")
  public String adminLoginPage() {
    return "admin/login";
  }

  /**
   * <pre>房源列表</pre>
   *
   * @return
   */
  @GetMapping("/house/list")
  public String houseListPage() {
    return "admin/house-list";
  }

  /**
   * <pre>新增房源功能页</pre>
   *
   * @return
   */
  @GetMapping("/add/house")
  public String addHousePage() {
    return "admin/house-add";
  }

  /**
   * <pre>查询房间列表</pre>
   *
   * @param searchBody 查询条件
   * @return
   */
  @PostMapping("/houses")
  @ResponseBody
  public DataTableResponse houses(@ModelAttribute DataTableSearch searchBody) {
    /** 查询结果 **/
    ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);
    /** 结果的封装 **/
    DataTableResponse response = new DataTableResponse(RainbowApiResponse.RespStatus.SUCCESS);
    response.setData(result.getResults());
    response.setRecordsFiltered(result.getTotal());
    response.setRecordsTotal(result.getTotal());
    response.setDraw(searchBody.getDraw());
    return response;
  }

  /**
   * <pre>上传图片</pre>
   *
   * @param file 文件资源
   * @return
   */
  @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseBody
  public RainbowApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
    }
    /** 获取文件的名称 **/
    String fileName = file.getOriginalFilename();
    File targetFile = new File("E:\\IdeaWork\\rainbow-search\\tmp\\" + fileName);
    try {
      file.transferTo(targetFile);
    } catch (IOException e) {
      e.printStackTrace();
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.INTERNAL_SERVER_ERROR);
    }
    return RainbowApiResponse.success(null);
  }

  /**
   * <pre>新增房产</pre>
   *
   * @param houseForm     房产的表单数据
   * @param bindingResult 绑定结果
   * @return
   */
  @PostMapping("/add/house")
  @ResponseBody
  public RainbowApiResponse addHouse(@Valid @ModelAttribute("form-add-house") HouseForm houseForm,
                                     BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return new RainbowApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
    }
    if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
    }
    Map<SupportAddressDO.Level, SupportAddressDTO> addressMap = addressService.findByCityAndRegion(houseForm.getCityEnName(),
            houseForm.getRegionEnName());
    if (addressMap.keySet().size() != ADDRESS_SIZE) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
    }
    /** 保存房产信息 **/
    ServiceResult<HouseDTO> result = houseService.save(houseForm);
    if (result.isSuccess()) {
      return RainbowApiResponse.success(RainbowApiResponse.RespStatus.SUCCESS);
    }
    return RainbowApiResponse.success(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
  }

  /**
   * <pre>房产编辑页面</pre>
   *
   * @param id    房产ID
   * @param model 模型
   * @return
   */
  @GetMapping("/house/edit")
  public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {
    if (id == null || id < 1) {
      return "404";
    }
    ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(id);
    if (!serviceResult.isSuccess()) {
      return "500";
    }
    HouseDTO result = serviceResult.getResult();
    model.addAttribute("house", result);
    Map<SupportAddressDO.Level, SupportAddressDTO> addressMap = addressService.findByCityAndRegion(result.getCityEnName(),
            result.getRegionEnName());
    model.addAttribute("city", addressMap.get(SupportAddressDO.Level.CITY));
    model.addAttribute("region", addressMap.get(SupportAddressDO.Level.REGION));

    HouseDetailDTO houseDetailDTO = result.getHouseDetail();
    ServiceResult<SubwayDTO> subwayDTOServiceResult = subwayService.findSubway(houseDetailDTO.getSubwayLineId());
    if (subwayDTOServiceResult.isSuccess()) {
      model.addAttribute("subway", subwayDTOServiceResult.getResult());
    }
    ServiceResult<SubwayStationDTO> subwayStationsResult = subwayService.findSubwayStation(houseDetailDTO.getSubwayStationId());
    if (subwayStationsResult.isSuccess()) {
      model.addAttribute("station", subwayStationsResult.getResult());
    }
    return "admin/house-edit";
  }

  /**
   * <pre>编辑房产信息</pre>
   *
   * @param houseForm 房产信息封装对象
   * @return
   */
  @PostMapping("/house/edit")
  @ResponseBody
  public RainbowApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm,
                                      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage());
    }
    Map<SupportAddressDO.Level, SupportAddressDTO> addressMap = addressService.findByCityAndRegion(houseForm.getCityEnName(),
            houseForm.getRegionEnName());
    if (addressMap.keySet().size() < ADDRESS_SIZE) {
      return RainbowApiResponse.success(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
    }
    ServiceResult<?> result = houseService.update(houseForm);
    if (result.isSuccess()) {
      return RainbowApiResponse.success(null);
    }
    return RainbowApiResponse.message(RainbowApiResponse.RespStatus.BAD_REQUEST.getCode(), result.getMessage());
  }

  /**
   * <pre>移除房产图片</pre>
   *
   * @param id 图片ID
   * @return
   */
  @DeleteMapping("admin/house/delete")
  @ResponseBody
  public RainbowApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
    ServiceResult result = houseService.removePhoto(id);

    if (result.isSuccess()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.SUCCESS);
    } else {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), result.getMessage());
    }
  }

  /**
   * <pre>修改封面接口</pre>
   *
   * @param coverId  封面Id
   * @param targetId 要修改的ID
   * @return
   */
  @PostMapping("admin/house/cover")
  @ResponseBody
  public RainbowApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
                                        @RequestParam(value = "target_id") Long targetId) {
    ServiceResult result = houseService.updateCover(coverId, targetId);
    if (result.isSuccess()) {
      return RainbowApiResponse.success(null);
    }
    return RainbowApiResponse.message(RainbowApiResponse.RespStatus.BAD_REQUEST.getCode(), result.getMessage());
  }

  /**
   * <pre>增加标签</pre>
   *
   * @param houseId 房产Id
   * @param tag     标签
   * @return
   */
  @PostMapping("admin/house/tag")
  @ResponseBody
  public RainbowApiResponse addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                        @RequestParam(value = "tag") String tag) {
    if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.BAD_REQUEST);
    }
    ServiceResult result = houseService.addTag(houseId, tag);
    if (result.isSuccess()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.SUCCESS);
    } else {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), result.getMessage());
    }
  }

  /**
   * <pre>移除标签</pre>
   *
   * @param houseId 房产ID
   * @param tag     标签
   * @return
   */
  @DeleteMapping("admin/house/tag")
  @ResponseBody
  public RainbowApiResponse removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                           @RequestParam(value = "tag") String tag) {
    if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.BAD_REQUEST);
    }
    ServiceResult result = houseService.removeTag(houseId, tag);
    if (result.isSuccess()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.SUCCESS);
    } else {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), result.getMessage());
    }
  }

  /**
   * <pre>房产审核</pre>
   *
   * @param id        房产Id
   * @param operation 操作
   * @return
   */
  @PutMapping("admin/house/operate/{id}/{operation}")
  @ResponseBody
  public RainbowApiResponse operateHouse(@PathVariable(value = "id") Long id,
                                         @PathVariable(value = "operation") int operation) {
    if (id <= 0) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
    }
    ServiceResult result;

    switch (operation) {
      case HouseOperationConstants.PASS:
        result = this.houseService.updateStatus(id, HouseStatusEnum.PASSES.getValue());
        break;
      case HouseOperationConstants.PULL_OUT:
        result = this.houseService.updateStatus(id, HouseStatusEnum.NOT_AUDITED.getValue());
        break;
      case HouseOperationConstants.DELETE:
        result = this.houseService.updateStatus(id, HouseStatusEnum.DELETED.getValue());
        break;
      case HouseOperationConstants.RENT:
        result = this.houseService.updateStatus(id, HouseStatusEnum.RENTED.getValue());
        break;
      default:
        return RainbowApiResponse.status(RainbowApiResponse.RespStatus.BAD_REQUEST);
    }

    if (result.isSuccess()) {
      return RainbowApiResponse.success(null);
    }
    return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), result.getMessage());
  }

  /**
   * <pre>房产订阅</pre>
   *
   * @return
   */
  @GetMapping("admin/house/subscribe")
  public String houseSubscribe() {
    return "admin/subscribe";
  }

  @GetMapping("admin/house/subscribe/list")
  @ResponseBody
  public RainbowApiResponse subscribeList(@RequestParam(value = "draw") int draw,
                                          @RequestParam(value = "start") int start,
                                          @RequestParam(value = "length") int size) {
    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> result = houseService.findSubscribeList(start, size);

    DataTableResponse response = new DataTableResponse(RainbowApiResponse.RespStatus.SUCCESS);
    response.setData(result.getResults());
    response.setDraw(draw);
    response.setRecordsFiltered(result.getTotal());
    response.setRecordsTotal(result.getTotal());
    return response;
  }

  @GetMapping("admin/user/{userId}")
  @ResponseBody
  public RainbowApiResponse getUserInfo(@PathVariable(value = "userId") Long userId) {
    if (userId == null || userId < 1) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.BAD_REQUEST);
    }

    ServiceResult<UserDTO> serviceResult = userService.findById(userId);
    if (!serviceResult.isSuccess()) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_FOUND);
    } else {
      return RainbowApiResponse.success(serviceResult.getResult());
    }
  }

  @PostMapping("admin/finish/subscribe")
  @ResponseBody
  public RainbowApiResponse finishSubscribe(@RequestParam(value = "house_id") Long houseId) {
    if (houseId < 1) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.BAD_REQUEST);
    }

    ServiceResult serviceResult = houseService.finishSubscribe(houseId);
    if (serviceResult.isSuccess()) {
      return RainbowApiResponse.success("");
    } else {
      return RainbowApiResponse.message(RainbowApiResponse.RespStatus.BAD_REQUEST.getCode(), serviceResult.getMessage());
    }
  }

}
