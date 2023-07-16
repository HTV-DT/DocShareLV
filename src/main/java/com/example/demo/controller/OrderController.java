
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.StatisticsResponse;
import com.example.demo.model.Access;
import com.example.demo.model.File;
import com.example.demo.model.Order;
import com.example.demo.service.AccessService;
import com.example.demo.service.OrderService;
import com.example.demo.service.OrderdetailService;
import com.example.demo.service.impl.DownloadServiceImpl;
import com.example.demo.service.impl.FileServiceImpl;
import com.example.demo.service.impl.LikeServiceImpl;
import com.example.demo.service.impl.OrderServiceImpl;
import com.example.demo.service.impl.OrderdetailServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.utils.Views;
import com.fasterxml.jackson.annotation.JsonView;

@CrossOrigin(origins = "*")
@RequestMapping("/order")
@RestController
public class OrderController {

  @Autowired
  FileServiceImpl fileService;

  @Autowired
  UserServiceImpl userService;

  @Autowired
  private OrderServiceImpl orderService;

  @Autowired
  private AccessService accessService;

  @Autowired
  private OrderdetailServiceImpl orderDetailService;

  
@Autowired
  private DownloadServiceImpl downloadServiceImpl;

  @Autowired
  private LikeServiceImpl likeServiceImpl;
  @GetMapping("/userAbout")
  @JsonView(Views.OrderInfoView.class)
  public ResponseEntity<List<Order>> getOrdersByUserIdAndStatusTrue(@RequestParam("user_id") Long userId) {
    List<Order> listOrders = orderService.getOrdersByUserIdAndStatusTrue(userId);
    return ResponseEntity.ok(listOrders);
  }

  @GetMapping("/list")
  @JsonView(Views.OrderInfoView.class)
  public ResponseEntity<List<Order>> getListOrders() {
    List<Order> listOrders = orderService.getAllOrder();
    return ResponseEntity.ok(listOrders);
  }

  @GetMapping("/access/list")
  public ResponseEntity<List<Access>> getAccessByUserId(@RequestParam("user_id") Long userId) {
    List<Access> accesses = accessService.getAccessByFileId(userId);
    return ResponseEntity.ok(accesses);
  }

  @GetMapping("/statistics")
  public ResponseEntity<StatisticsResponse> getStatistics() {
    StatisticsResponse response = new StatisticsResponse();
    response.setTotal_user(userService.getUserCount());
    response.setTotal_view(fileService.sumView());
    response.setTotal_order(orderService.countByOrderStatusTrue());
    response.setTotal_price(orderDetailService.sumPrice());
    response.setTotal_dowload(downloadServiceImpl.getTotalDownloadsForSystem());
    response.setTotal_like(likeServiceImpl.countTotalLikes());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/MonthPrice")
  public ResponseEntity<List<Object[]>> gettoatlPrice() {
    List<Object[]> list = orderDetailService.findMonthlyTotalPrices();
    return ResponseEntity.ok(list);
  }

}