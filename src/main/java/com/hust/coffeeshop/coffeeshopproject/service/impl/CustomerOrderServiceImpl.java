package com.hust.coffeeshop.coffeeshopproject.service.impl;

import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerOrderResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.OrderDetailRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.OrderDetailResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerRequestDTO;
import com.hust.coffeeshop.coffeeshopproject.dto.CustomerResponseDTO;
import com.hust.coffeeshop.coffeeshopproject.entity.Customer;
import com.hust.coffeeshop.coffeeshopproject.entity.CustomerOrder;
import com.hust.coffeeshop.coffeeshopproject.entity.EventPromotion;
import com.hust.coffeeshop.coffeeshopproject.entity.MenuItem;
import com.hust.coffeeshop.coffeeshopproject.entity.OrderDetail;
import com.hust.coffeeshop.coffeeshopproject.repository.CustomerOrderRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.CustomerRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.EventPromotionRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.MenuItemRepository;
import com.hust.coffeeshop.coffeeshopproject.repository.OrderDetailRepository;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerOrderService;
import com.hust.coffeeshop.coffeeshopproject.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

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
    private EventPromotionRepository promotionRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CustomerService customerService;

    @Override
    @Transactional
    public CustomerOrderResponseDTO createCustomerOrder(CustomerOrderRequestDTO requestDTO) {
        logger.info("Received CustomerOrderRequestDTO: {}", requestDTO);

        Customer customer;
        try {
            if (requestDTO.getCustomerId() != null) {
                customer = customerRepository.findById(requestDTO.getCustomerId())
                        .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + requestDTO.getCustomerId() + " not found."));
                logger.info("Found Customer: {} (ID: {})", customer.getCustomerName(), customer.getCustomerId());
            } else if (requestDTO.getPhoneNumber() != null && !requestDTO.getPhoneNumber().trim().isEmpty()) {
                Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(requestDTO.getPhoneNumber().trim());
                if (existingCustomer.isPresent()) {
                    customer = existingCustomer.get();
                    logger.info("Found existing customer by phone number: {} (ID: {})", customer.getPhoneNumber(), customer.getCustomerId());
                } else {
                    if (requestDTO.getCustomerName() == null || requestDTO.getCustomerName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Customer name is required to create a new customer for phone number: " + requestDTO.getPhoneNumber());
                    }
                    CustomerRequestDTO newCustomerRequest = new CustomerRequestDTO();
                    newCustomerRequest.setPhoneNumber(requestDTO.getPhoneNumber().trim());
                    newCustomerRequest.setCustomerName(requestDTO.getCustomerName().trim());
                    newCustomerRequest.setEmail(requestDTO.getEmail());

                    CustomerResponseDTO createdCustomerDTO = customerService.createCustomer(newCustomerRequest);
                    customer = customerRepository.findById(createdCustomerDTO.getCustomerId())
                            .orElseThrow(() -> new RuntimeException("Failed to retrieve new customer entity after creation."));
                    logger.info("Created new customer: {} (ID: {})", customer.getCustomerName(), customer.getCustomerId());
                }
            } else {
                throw new IllegalArgumentException("Customer ID or Phone Number must be provided for order creation.");
            }

            EventPromotion promotion = null;
            if (requestDTO.getPromotionId() != null) {
                promotion = promotionRepository.findById(requestDTO.getPromotionId())
                        .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + requestDTO.getPromotionId() + " not found."));
                logger.info("Found Promotion: {} (ID: {}), StartDate: {}, EndDate: {}, MinOrderAmount: {}",
                        promotion.getPromotionName(), promotion.getPromotionId(), promotion.getStartDate(), promotion.getEndDate(), promotion.getMinOrderAmount());
            }

            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setCustomer(customer);
            customerOrder.setEmployee(null);
            customerOrder.setEventPromotion(promotion);

            customerOrder.setOrderTime(LocalDateTime.now());
            customerOrder.setExpectedPickupTime(requestDTO.getExpectedPickupTime());
            customerOrder.setTotalAmount(BigDecimal.ZERO);
            customerOrder.setTotalAmountUsd(BigDecimal.ZERO);
            customerOrder.setExchangeRate(new BigDecimal("25000.00"));
            customerOrder.setRankDiscount(BigDecimal.ZERO);
            customerOrder.setPromotionDiscount(BigDecimal.ZERO);

            customerOrder.setOrderStatus("Completed");
            customerOrder.setPaymentMethod(requestDTO.getPaymentMethod());
            customerOrder.setIsPaid(requestDTO.getIsPaid() != null ? requestDTO.getIsPaid() : false);
            customerOrder.setNotes(requestDTO.getNotes());

            customerOrder.setOrderDetails(new ArrayList<>());

            CustomerOrder savedOrder = customerOrderRepository.save(customerOrder);
            logger.info("Initial CustomerOrder saved with ID: {}", savedOrder.getOrderId());

            if (requestDTO.getOrderDetails() != null && !requestDTO.getOrderDetails().isEmpty()) {
                logger.info("Processing {} order details from request.", requestDTO.getOrderDetails().size());
                List<OrderDetail> newOrderDetails = new ArrayList<>();
                for (OrderDetailRequestDTO detailDTO : requestDTO.getOrderDetails()) {
                    MenuItem menuItem = menuItemRepository.findById(detailDTO.getMenuItemId())
                            .orElseThrow(() -> new EntityNotFoundException("Menu Item with ID " + detailDTO.getMenuItemId() + " not found."));

                    if (detailDTO.getQuantity() == null || detailDTO.getQuantity() <= 0) {
                        throw new IllegalArgumentException("Quantity for Menu Item ID " + detailDTO.getMenuItemId() + " must be a positive number.");
                    }

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setCustomerOrder(savedOrder);
                    orderDetail.setMenuItem(menuItem);
                    orderDetail.setQuantity(detailDTO.getQuantity());
                    orderDetail.setUnitPrice(menuItem.getPrice());
                    newOrderDetails.add(orderDetail);
                }
                orderDetailRepository.saveAll(newOrderDetails);
                logger.info("Saved {} new order details.", newOrderDetails.size());

                savedOrder.getOrderDetails().addAll(newOrderDetails);

            } else {
                throw new IllegalArgumentException("Order must contain at least one item.");
            }

            customerOrderRepository.flush();
            Optional<CustomerOrder> reloadedOrderOptional = customerOrderRepository.findById(savedOrder.getOrderId());
            CustomerOrder reloadedOrder = reloadedOrderOptional
                    .orElseThrow(() -> new RuntimeException("Failed to reload CustomerOrder after detail save."));

            // Tính tổng tiền gốc
            BigDecimal totalAmount = savedOrder.getOrderDetails().stream()
                    .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Áp dụng khuyến mãi cố định nếu có
            BigDecimal discount = BigDecimal.ZERO;
            if (promotion != null) {
                discount = promotion.getValue() != null ? promotion.getValue() : BigDecimal.ZERO;
                totalAmount = totalAmount.subtract(discount);
                savedOrder.setPromotionDiscount(discount); // Lưu vào đơn
                // Trừ số lần sử dụng còn lại
                if (promotion.getRemainingUses() != null && promotion.getRemainingUses() > 0) {
                    promotion.setRemainingUses(promotion.getRemainingUses() - 1);
                    promotionRepository.save(promotion);
                }
            }


            // Tính tổng sau giảm
            BigDecimal grossTotal = reloadedOrder.getOrderDetails().stream()
                    .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal netTotal = grossTotal.subtract(discount);

            // Cập nhật đơn hàng
            reloadedOrder.setPromotionDiscount(discount);
            reloadedOrder.setTotalAmount(netTotal);
            customerOrderRepository.save(reloadedOrder);

            // Cộng vào tổng chi tiêu khách hàng
            BigDecimal spent = customer.getTotalSpent() != null ? customer.getTotalSpent() : BigDecimal.ZERO;
            customer.setTotalSpent(spent.add(netTotal));
            customerRepository.save(customer);

            logger.info("CustomerOrder reloaded after details save: OrderID={}, TotalAmount={}, PromotionDiscount={}, RankDiscount={}, TotalAmountUsd={}",
                    reloadedOrder.getOrderId(), reloadedOrder.getTotalAmount(), reloadedOrder.getPromotionDiscount(),
                    reloadedOrder.getRankDiscount(), reloadedOrder.getTotalAmountUsd());

            return convertToDto(reloadedOrder);

        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during order creation: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during order creation: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during order creation: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during order creation: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing order: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CustomerOrderResponseDTO> getAllCustomerOrders() {
        return customerOrderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CustomerOrderResponseDTO> getCustomerOrderById(Long id) {
        return customerOrderRepository.findById(id).map(this::convertToDto);
    }

    @Override
    @Transactional
    public CustomerOrderResponseDTO updateCustomerOrder(Long id, CustomerOrderRequestDTO requestDTO) {
        logger.info("Updating CustomerOrder with ID: {}, Request DTO: {}", id, requestDTO);
        try {
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
                } else if (requestDTO.getCustomerId() == null && existingOrder.getCustomer() != null) {
                    existingOrder.setCustomer(null);
                    logger.info("Removed customer from order {}", id);
                }


                if (requestDTO.getPromotionId() != null) {
                    EventPromotion promotion = promotionRepository.findById(requestDTO.getPromotionId())
                            .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + requestDTO.getPromotionId() + " not found."));
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

                            if (detailDTO.getQuantity() == null || detailDTO.getQuantity() <= 0) {
                                throw new IllegalArgumentException("Quantity for Menu Item ID " + detailDTO.getMenuItemId() + " must be a positive number.");
                            }

                            OrderDetail orderDetail = new OrderDetail();
                            orderDetail.setCustomerOrder(existingOrder);
                            orderDetail.setMenuItem(menuItem);
                            orderDetail.setQuantity(detailDTO.getQuantity());
                            orderDetail.setUnitPrice(menuItem.getPrice());
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
        } catch (EntityNotFoundException e) {
            logger.error("Entity not found during order update: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument during order update: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation during order update: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during order update: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing order update: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteCustomerOrder(Long id) {
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
        dto.setPromotionId(customerOrder.getEventPromotion() != null ? customerOrder.getEventPromotion().getPromotionId() : null);
        dto.setPromotionName(customerOrder.getEventPromotion() != null ? customerOrder.getEventPromotion().getPromotionName() : null);

        dto.setOrderTime(customerOrder.getOrderTime());
        dto.setExpectedPickupTime(customerOrder.getExpectedPickupTime());
        dto.setTotalAmount(customerOrder.getTotalAmount());
        dto.setTotalAmountUsd(customerOrder.getTotalAmountUsd());
        dto.setExchangeRate(customerOrder.getExchangeRate());
        dto.setRankDiscount(customerOrder.getRankDiscount());
        dto.setPromotionDiscount(customerOrder.getPromotionDiscount());
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