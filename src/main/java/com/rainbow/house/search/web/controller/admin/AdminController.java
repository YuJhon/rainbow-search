package com.rainbow.house.search.web.controller.admin;

import com.rainbow.house.search.base.DataTableResponse;
import com.rainbow.house.search.base.RainbowApiResponse;
import com.rainbow.house.search.base.ServiceMultiResult;
import com.rainbow.house.search.base.ServiceResult;
import com.rainbow.house.search.entity.SupportAddressDO;
import com.rainbow.house.search.service.HouseService;
import com.rainbow.house.search.service.SupportAddressService;
import com.rainbow.house.search.web.dto.HouseDTO;
import com.rainbow.house.search.web.dto.SupportAddressDTO;
import com.rainbow.house.search.web.form.DataTableSearch;
import com.rainbow.house.search.web.form.HouseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
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

  @Autowired
  private HouseService houseService;

  @Autowired
  private SupportAddressService addressService;

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
  public RainbowApiResponse addHouse(@Valid @ModelAttribute("form-add-house") HouseForm houseForm,
                                     BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return new RainbowApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
    }
    if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
      return RainbowApiResponse.message(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
    }
    Map<SupportAddressDO.Level, SupportAddressDTO> addressMap = addressService.findByCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
    if (addressMap.keySet().size() != 2) {
      return RainbowApiResponse.status(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
    }
    /** 保存房产信息 **/
    ServiceResult<HouseDTO> result = houseService.save(houseForm);
    if (result.isSuccess()){
      return RainbowApiResponse.success(RainbowApiResponse.RespStatus.SUCCESS);
    }
    return RainbowApiResponse.success(RainbowApiResponse.RespStatus.NOT_VALID_PARAM);
  }
}
