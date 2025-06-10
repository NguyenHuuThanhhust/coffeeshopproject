package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.OrderDetailRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.OrderDetailResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Customer;
import com.hust.coffeeshop.coffeeshopproject.entity.CustomerOrder;
import com.hust.coffeeshop.coffeeshopproject.entity.Employee;
import com.hust.coffeeshop.coffeeshopproject.entity.EventPromotion;
import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;
import com.hust.coffeeshop.coffeeshopproject.entity.OrderDetail;
import com.hust.coffeeshop.coffeeshopproject.repository.CustomerOrderRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.CustomerRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.EmployeeRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.EventPromotionRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.MenuItemRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.OrderDetailRepository;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerOrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal; // Đảm bảo đã import BigDecimal

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);

    @Autowired
    private CustomerOrderRepository customerOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EventPromotionRepository promotionRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;


    @Override
    @Transactional
    public CustomerOrderResponseDTO createCustomerOrder(CustomerOrderRequestDTO requestDTO) {
        logger.info("Received CustomerOrderRequestDTO: {}", requestDTO);

        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + requestDTO.getCustomerId() + " not found."));
        logger.info("Found Customer: {} (ID: {})", customer.getCustomerName(), customer.getCustomerId());

        Employee employee = null;
        if (requestDTO.getEmployeeId() != null) {
            employee = employeeRepository.findById(requestDTO.getEmployeeId())
                    .orElseThrow(() -> new EntityNotFoundException("Employee with ID " + requestDTO.getEmployeeId() + " not found."));
            logger.info("Found Employee: {} (ID: {})", employee.getEmployeeName(), employee.getEmployeeId());
        }

        EventPromotion promotion = null;
        if (requestDTO.getPromotionId() != null) {
            promotion = promotionRepository.findById(requestDTO.getPromotionId())
                    .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + requestDTO.getPromotionId() + " not found."));
            // Sửa lỗi: getPromotionID() -> getPromotionId()
            logger.info("Found Promotion: {} (ID: {}), StartDate: {}, EndDate: {}, MinOrderAmount: {}",
                    promotion.getPromotionName(), promotion.getPromotionId(), promotion.getStartDate(), promotion.getEndDate(), promotion.getMinOrderAmount());
        }

        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setCustomer(customer);
        customerOrder.setEmployee(employee);
        customerOrder.setEventPromotion(promotion);

        // Các trường này sẽ được database hoặc logic tính toán
        customerOrder.setOrderTime(LocalDateTime.now());
        customerOrder.setExpectedPickupTime(requestDTO.getExpectedPickupTime());
        // Sửa lỗi: setTotalAmount cần BigDecimal, và không lấy từ DTO khi tạo mới
        customerOrder.setTotalAmount(BigDecimal.ZERO); // Mặc định 0, sẽ được trigger cập nhật
        customerOrder.setTotalAmountUsd(BigDecimal.ZERO); // Mặc định 0, sẽ được trigger cập nhật
        customerOrder.setExchangeRate(new BigDecimal("25000.00")); // Tỷ giá mặc định hoặc cấu hình
        customerOrder.setRankDiscount(BigDecimal.ZERO); // Mặc định 0, sẽ được trigger cập nhật
        customerOrder.setPromotionDiscount(BigDecimal.ZERO); // Mặc định 0, sẽ được trigger cập nhật

        customerOrder.setOrderStatus(requestDTO.getOrderStatus() != null ? requestDTO.getOrderStatus() : "PENDING"); // Sử dụng giá trị từ DTO hoặc mặc định
        customerOrder.setPaymentMethod(requestDTO.getPaymentMethod());
        customerOrder.setIsPaid(requestDTO.getIsPaid() != null ? requestDTO.getIsPaid() : false); // Sử dụng giá trị từ DTO hoặc mặc định
        customerOrder.setNotes(requestDTO.getNotes());

        customerOrder.setOrderDetails(new ArrayList<>()); // RẤT QUAN TRỌNG: Khởi tạo danh sách OrderDetails rỗng

        CustomerOrder savedOrder = customerOrderRepository.save(customerOrder);
        logger.info("Initial CustomerOrder saved with ID: {}", savedOrder.getOrderId());

        // Xử lý OrderDetails
        if (requestDTO.getOrderDetails() != null && !requestDTO.getOrderDetails().isEmpty()) {
            logger.info("Processing {} order details from request.", requestDTO.getOrderDetails().size());
            List<OrderDetail> newOrderDetails = new ArrayList<>();
            for (OrderDetailRequestDTO detailDTO : requestDTO.getOrderDetails()) {
                MenuItem menuItem = menuItemRepository.findById(detailDTO.getMenuItemId())
                        .orElseThrow(() -> new EntityNotFoundException("Menu Item with ID " + detailDTO.getMenuItemId() + " not found."));
                // Sửa lỗi: getMenuItemID() -> getMenuItemId()
                logger.info("  - Found MenuItem: {} (ID: {}), Quantity: {}", menuItem.getItemName(), menuItem.getMenuItemId(), detailDTO.getQuantity());

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setCustomerOrder(savedOrder);
                orderDetail.setMenuItem(menuItem);
                orderDetail.setQuantity(detailDTO.getQuantity());
                // Sửa lỗi: Lấy giá từ MenuItem, không phải từ DTO, tránh incompatible types nếu DTO là Double
                orderDetail.setUnitPrice(BigDecimal.valueOf(menuItem.getPrice()));
                newOrderDetails.add(orderDetail);
            }
            orderDetailRepository.saveAll(newOrderDetails);
            logger.info("Saved {} new order details.", newOrderDetails.size());

            // Cập nhật danh sách details trong entity đã lưu (quan trọng cho response)
            savedOrder.getOrderDetails().addAll(newOrderDetails);

        } else {
            logger.warn("No order details provided in the requestDTO. TotalAmount and Discounts will be 0.");
        }

        customerOrderRepository.flush();
        Optional<CustomerOrder> reloadedOrderOptional = customerOrderRepository.findById(savedOrder.getOrderId());
        CustomerOrder reloadedOrder = reloadedOrderOptional
                .orElseThrow(() -> new RuntimeException("Failed to reload CustomerOrder after detail save."));

        logger.info("CustomerOrder reloaded after details save: OrderID={}, TotalAmount={}, PromotionDiscount={}, RankDiscount={}, TotalAmountUsd={}",
                reloadedOrder.getOrderId(), reloadedOrder.getTotalAmount(), reloadedOrder.getPromotionDiscount(),
                reloadedOrder.getRankDiscount(), reloadedOrder.getTotalAmountUsd());

        return convertToDto(reloadedOrder);
    }

    @Override
    public List<CustomerOrderResponseDTO> getAllCustomerOrders() {
        return customerOrderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CustomerOrderResponseDTO> getCustomerOrderById(Integer id) {
        return customerOrderRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CustomerOrderResponseDTO updateCustomerOrder(Integer id, CustomerOrderRequestDTO requestDTO) {
        logger.info("Updating CustomerOrder with ID: {}, Request DTO: {}", id, requestDTO);
        return customerOrderRepository.findById(id).map(existingOrder -> {
            existingOrder.setExpectedPickupTime(requestDTO.getExpectedPickupTime() != null ? requestDTO.getExpectedPickupTime() : existingOrder.getExpectedPickupTime());
            existingOrder.setOrderStatus(requestDTO.getOrderStatus() != null ? requestDTO.getOrderStatus() : existingOrder.getOrderStatus());
            existingOrder.setPaymentMethod(requestDTO.getPaymentMethod() != null ? requestDTO.getPaymentMethod() : existingOrder.getPaymentMethod());
            existingOrder.setIsPaid(requestDTO.getIsPaid() != null ? requestDTO.getIsPaid() : existingOrder.getIsPaid());
            existingOrder.setNotes(requestDTO.getNotes() != null ? requestDTO.getNotes() : existingOrder.getNotes());

            if (requestDTO.getCustomerId() != null && (existingOrder.getCustomer() == null || !requestDTO.getCustomerId().equals(existingOrder.getCustomer().getCustomerId()))) {
                Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                        .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + requestDTO.getCustomerId() + " not found."));
                existingOrder.setCustomer(customer);
                logger.info("Updated customer for order {} to ID {}", id, customer.getCustomerId());
            }

            if (requestDTO.getEmployeeId() != null && (existingOrder.getEmployee() == null || !requestDTO.getEmployeeId().equals(existingOrder.getEmployee().getEmployeeId()))) {
                Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
                        .orElseThrow(() -> new EntityNotFoundException("Employee with ID " + requestDTO.getEmployeeId() + " not found."));
                existingOrder.setEmployee(employee);
                logger.info("Updated employee for order {} to ID {}", id, employee.getEmployeeId());
            } else if (requestDTO.getEmployeeId() == null && existingOrder.getEmployee() != null) {
                existingOrder.setEmployee(null);
                logger.info("Removed employee from order {}", id);
            }

            if (requestDTO.getPromotionId() != null) {
                EventPromotion promotion = promotionRepository.findById(requestDTO.getPromotionId())
                        .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + requestDTO.getPromotionId() + " not found."));
                // Sửa lỗi: getPromotionID() -> getPromotionId()
                if (existingOrder.getEventPromotion() == null || !requestDTO.getPromotionId().equals(existingOrder.getEventPromotion().getPromotionId())) {
                    existingOrder.setEventPromotion(promotion);
                    logger.info("Updated promotion for order {} to ID {}", id, promotion.getPromotionId());
                }
            } else if (existingOrder.getEventPromotion() != null) {
                existingOrder.setEventPromotion(null);
                logger.info("Removed promotion from order {}", id);
            }

            if (requestDTO.getOrderDetails() != null) {
                if (existingOrder.getOrderDetails() != null && !existingOrder.getOrderDetails().isEmpty()) {
                    logger.info("Deleting {} existing order details for order ID {}", existingOrder.getOrderDetails().size(), id);
                    orderDetailRepository.deleteAll(existingOrder.getOrderDetails());
                    existingOrder.getOrderDetails().clear();
                }

                if (!requestDTO.getOrderDetails().isEmpty()) {
                    List<OrderDetail> newOrderDetails = new ArrayList<>();
                    for (OrderDetailRequestDTO detailDTO : requestDTO.getOrderDetails()) {
                        MenuItem menuItem = menuItemRepository.findById(detailDTO.getMenuItemId())
                                .orElseThrow(() -> new EntityNotFoundException("Menu Item with ID " + detailDTO.getMenuItemId() + " not found."));

                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setCustomerOrder(existingOrder);
                        orderDetail.setMenuItem(menuItem);
                        orderDetail.setQuantity(detailDTO.getQuantity());
                        // Sửa lỗi: Lấy giá từ MenuItem, tránh incompatible types nếu DTO là Double
                        orderDetail.setUnitPrice(BigDecimal.valueOf(menuItem.getPrice()));
                        newOrderDetails.add(orderDetail);
                    }
                    orderDetailRepository.saveAll(newOrderDetails);
                    existingOrder.getOrderDetails().addAll(newOrderDetails);
                    logger.info("Added {} new order details for order ID {}", newOrderDetails.size(), id);
                } else {
                    logger.info("Request DTO provided an empty details list, all existing details for order ID {} have been removed.", id);
                }
            }

            CustomerOrder updatedOrder = customerOrderRepository.save(existingOrder);
            logger.info("CustomerOrder updated with ID: {}", updatedOrder.getOrderId());

            customerOrderRepository.flush();
            Optional<CustomerOrder> reloadedOrderOptional = customerOrderRepository.findById(updatedOrder.getOrderId());
            CustomerOrder reloadedOrder = reloadedOrderOptional
                    .orElseThrow(() -> new RuntimeException("Failed to reload CustomerOrder after update."));

            logger.info("CustomerOrder reloaded after update: OrderID={}, TotalAmount={}, PromotionDiscount={}, RankDiscount={}, TotalAmountUsd={}",
                    reloadedOrder.getOrderId(), reloadedOrder.getTotalAmount(), reloadedOrder.getPromotionDiscount(),
                    reloadedOrder.getRankDiscount(), reloadedOrder.getTotalAmountUsd());

            return convertToDto(reloadedOrder);

        }).orElseThrow(() -> new EntityNotFoundException("Customer Order with ID " + id + " not found."));
    }

    @Override
    @Transactional
    public void deleteCustomerOrder(Integer id) {
        logger.info("Attempting to delete CustomerOrder with ID: {}", id);
        if (!customerOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer Order with ID " + id + " not found.");
        }
        customerOrderRepository.deleteById(id);
        logger.info("CustomerOrder with ID {} deleted successfully.", id);
    }

    private CustomerOrderResponseDTO convertToDto(CustomerOrder customerOrder) {
        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setOrderId(customerOrder.getOrderId());
        dto.setCustomerId(customerOrder.getCustomer() != null ? customerOrder.getCustomer().getCustomerId() : null);
        dto.setCustomerName(customerOrder.getCustomer() != null ? customerOrder.getCustomer().getCustomerName() : null);
        dto.setEmployeeId(customerOrder.getEmployee() != null ? customerOrder.getEmployee().getEmployeeId() : null);
        dto.setEmployeeName(customerOrder.getEmployee() != null ? customerOrder.getEmployee().getEmployeeName() : null);
        // Sửa lỗi: getPromotionID() -> getPromotionId()
        dto.setPromotionId(customerOrder.getEventPromotion() != null ? customerOrder.getEventPromotion().getPromotionId() : null);
        dto.setPromotionName(customerOrder.getEventPromotion() != null ? customerOrder.getEventPromotion().getPromotionName() : null);

        dto.setOrderTime(customerOrder.getOrderTime());
        dto.setExpectedPickupTime(customerOrder.getExpectedPickupTime());
        dto.setTotalAmount(customerOrder.getTotalAmountUsd());
        dto.setExchangeRate(customerOrder.getExchangeRate());
        dto.setRankDiscount(customerOrder.getRankDiscount());
        dto.setPromotionDiscount(customerOrder.getPromotionDiscount());
        dto.setTotalAmount(customerOrder.getTotalAmount()); // Đảm bảo có trường TotalAmount trong DTO
        dto.setOrderStatus(customerOrder.getOrderStatus());
        dto.setPaymentMethod(customerOrder.getPaymentMethod());
        dto.setIsPaid(customerOrder.getIsPaid());
        dto.setNotes(customerOrder.getNotes());

        if (customerOrder.getOrderDetails() != null) {
            List<OrderDetailResponseDTO> detailDTOs = customerOrder.getOrderDetails().stream()
                    .map(detail -> {
                        OrderDetailResponseDTO detailDto = new OrderDetailResponseDTO();
                        detailDto.setOrderDetailId(detail.getOrderDetailId());
                        detailDto.setOrderId(detail.getCustomerOrder() != null ? detail.getCustomerOrder().getOrderId() : null);
                        // Sửa lỗi: getMenuItemID() -> getMenuItemId()
                        detailDto.setMenuItemId(detail.getMenuItem() != null ? detail.getMenuItem().getMenuItemId() : null);
                        detailDto.setMenuItemName(detail.getMenuItem() != null ? detail.getMenuItem().getItemName() : null);
                        detailDto.setQuantity(detail.getQuantity());
                        detailDto.setPriceAtOrder(detail.getUnitPrice());
                        return detailDto;
                    })
                    .collect(Collectors.toList());
            dto.setOrderDetails(detailDTOs);
        } else {
            dto.setOrderDetails(new ArrayList<>());
        }

        return dto;
    }
}