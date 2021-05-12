package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.CustomerDtoWithId;
import com.itechart.orderplanningproblem.dto.ItemDtoWithId;
import com.itechart.orderplanningproblem.dto.ItemDtoWithoutId;
import com.itechart.orderplanningproblem.dto.OrderDtoWithId;
import com.itechart.orderplanningproblem.dto.OrderDtoWithoutId;
import com.itechart.orderplanningproblem.dto.WarehouseDtoWithId;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.entity.Order;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.entity.WarehouseItem;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.CustomerRepository;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import com.itechart.orderplanningproblem.repository.ItemRepository;
import com.itechart.orderplanningproblem.repository.OrderRepository;
import com.itechart.orderplanningproblem.repository.WarehouseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private WarehouseRepository warehouseRepository;
    @Mock
    private DistanceRepository distanceRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private OrderService orderService;

    @Test
    void OrderId_FindByIdOrderThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long orderId = 12503L;
        // when
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> orderService.readById(orderId));

    }

    @Test
    void OrderId_FindByIdOrder_ReturnOrder() throws ResourceNotFoundException {
        // given
        Long orderId = 1L;
        Double amount = 30.0;
        String itemName = "Chocolate";
        Double distanceValue = 4432.186613415447;
        Item item = Item.builder()
                .id(1L)
                .name(itemName)
                .build();
        ItemDtoWithId itemDto = ItemDtoWithId.builder()
                .id(1L)
                .name(itemName)
                .build();
        Customer customer = Customer.builder()
                .id(1L)
                .name("Customer")
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        CustomerDtoWithId customerDto = CustomerDtoWithId.builder()
                .id(1L)
                .name("Customer")
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDtoWithId warehouseDto = WarehouseDtoWithId.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Order order = Order.builder()
                .id(orderId)
                .item(item)
                .amount(amount)
                .customer(customer)
                .warehouse(warehouse)
                .distance(distanceValue)
                .build();
        OrderDtoWithId orderDto = OrderDtoWithId.builder()
                .id(orderId)
                .item(itemDto)
                .amount(amount)
                .customer(customerDto)
                .warehouse(warehouseDto)
                .distance(distanceValue)
                .build();
        // when
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(objectMapper.convertValue(order, OrderDtoWithId.class))
                .thenReturn(orderDto);

        // then
        Assertions.assertEquals(orderDto, orderService.readById(orderId));

    }

    @Test
    void ReadPageOfOrders_ReturnPageOfOrders() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Long orderId = 1L;
        Double amount = 30.0;
        String itemName = "Chocolate";
        Double distanceValue = 4432.186613415447;
        Item item = Item.builder()
                .id(1L)
                .name(itemName)
                .build();
        ItemDtoWithId itemDto = ItemDtoWithId.builder()
                .id(1L)
                .name(itemName)
                .build();
        Customer customer = Customer.builder()
                .id(1L)
                .name("Customer")
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        CustomerDtoWithId customerDto = CustomerDtoWithId.builder()
                .id(1L)
                .name("Customer")
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseDtoWithId warehouseDto = WarehouseDtoWithId.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Order order = Order.builder()
                .id(orderId)
                .item(item)
                .amount(amount)
                .customer(customer)
                .warehouse(warehouse)
                .distance(distanceValue)
                .build();
        OrderDtoWithId orderDto = OrderDtoWithId.builder()
                .id(orderId)
                .item(itemDto)
                .amount(amount)
                .customer(customerDto)
                .warehouse(warehouseDto)
                .distance(distanceValue)
                .build();
        List<Order> orderList = Collections.singletonList(order);
        List<OrderDtoWithId> orderDtoWithIdList = Collections.singletonList(orderDto);
        Page<Order> orderPage = new PageImpl<>(orderList);
        Page<OrderDtoWithId> orderDtoWithIdPage = new PageImpl<>(orderDtoWithIdList);
        // when
        Mockito.when(orderRepository.findAll(pageRequest)).thenReturn(orderPage);
        Mockito.when(objectMapper.convertValue(order, OrderDtoWithId.class))
                .thenReturn(orderDto);
        // then
        Assertions.assertEquals(orderDtoWithIdPage, orderService.readPage(pageRequest));

    }

    @Test
    void OrderToCreate_CreateOrder_ReturnCreatedOrder() throws UnprocessableEntityException, ResourceNotFoundException {
        // given
        Long orderId = 1L;
        Long customerId = 1L;
        Double amount = 30.0;
        String itemName = "Chocolate";
        Double distanceValue = 4432.186613415447;
        Item item = Item.builder()
                .id(1L)
                .name(itemName)
                .build();
        ItemDtoWithId itemDto = ItemDtoWithId.builder()
                .id(1L)
                .name(itemName)
                .build();
        ItemDtoWithoutId itemDtoWithoutId = ItemDtoWithoutId.builder()
                .name(itemName)
                .build();
        Customer customer = Customer.builder()
                .id(customerId)
                .name("Customer")
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        CustomerDtoWithId customerDto = CustomerDtoWithId.builder()
                .id(customerId)
                .name("Customer")
                .latitude(55.0055)
                .longitude(24.454732)
                .build();
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Warehouse emptyWarehouse = Warehouse.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        WarehouseItem warehouseItem = WarehouseItem.builder()
                .id(1L)
                .item(item)
                .amount(amount)
                .warehouse(warehouse)
                .build();
        warehouse.getItems().add(warehouseItem);
        List<Warehouse> warehouseList = new ArrayList<>();
        warehouseList.add(warehouse);
        WarehouseDtoWithId warehouseDto = WarehouseDtoWithId.builder()
                .id(1L)
                .identifier("Warehouse")
                .latitude(22.12345)
                .longitude(54.6688)
                .items(new ArrayList<>())
                .build();
        Distance distance = Distance.builder()
                .id(1L)
                .distanceValue(distanceValue)
                .customer(customer)
                .warehouse(warehouse)
                .build();
        Order order = Order.builder()
                .item(item)
                .amount(amount)
                .customer(customer)
                .warehouse(warehouse)
                .distance(distanceValue)
                .build();
        Order createdOrder = Order.builder()
                .id(orderId)
                .item(item)
                .amount(amount)
                .customer(customer)
                .warehouse(warehouse)
                .distance(distanceValue)
                .build();
        OrderDtoWithoutId orderDtoToBeCreated = OrderDtoWithoutId.builder()
                .item(itemDtoWithoutId)
                .amount(amount)
                .customerId(customerId)
                .build();
        OrderDtoWithId createdOrderDto = OrderDtoWithId.builder()
                .id(orderId)
                .item(itemDto)
                .amount(amount)
                .customer(customerDto)
                .warehouse(warehouseDto)
                .distance(distanceValue)
                .build();
        // when
        Mockito.when(itemRepository.readByName(itemName)).thenReturn(Optional.of(item));
        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(warehouseRepository.findWarehouseForCustomer(customerId)).thenReturn(warehouseList);
        Mockito.when(warehouseRepository.save(emptyWarehouse)).thenReturn(emptyWarehouse);
        Mockito.when(distanceRepository.findDistanceByCustomerAndWarehouse(
                order.getCustomer().getId(), warehouse.getId())).thenReturn(distance);
        Mockito.when(orderRepository.save(order)).thenReturn(createdOrder);
        Mockito.when(objectMapper.convertValue(createdOrder, OrderDtoWithId.class))
                .thenReturn(createdOrderDto);

        // then
        Assertions.assertEquals(createdOrderDto, orderService.create(orderDtoToBeCreated));

    }
}