package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.OrderDto;
import com.itechart.orderplanningproblem.dto.CreateOrderDto;
import com.itechart.orderplanningproblem.entity.Customer;
import com.itechart.orderplanningproblem.entity.Distance;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.entity.Order;
import com.itechart.orderplanningproblem.entity.Warehouse;
import com.itechart.orderplanningproblem.entity.WarehouseItem;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.CustomerRepository;
import com.itechart.orderplanningproblem.repository.DistanceRepository;
import com.itechart.orderplanningproblem.repository.ItemRepository;
import com.itechart.orderplanningproblem.repository.OrderRepository;
import com.itechart.orderplanningproblem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final DistanceRepository distanceRepository;
    private final ObjectMapper objectMapper;

    private static final String NO_SUCH_ITEM_LITERAL = "There is no item with name ";
    private static final String NO_SUCH_CUSTOMER_LITERAL = "There is no customer with id ";

    @Transactional
    public OrderDto create(final CreateOrderDto createOrderDto)
            throws UnprocessableEntityException, ResourceNotFoundException {
        Order orderToCreate = validateOrder(createOrderDto);
        orderItemFromWarehouse(orderToCreate);
        Order createdOrder = orderRepository.save(orderToCreate);
        return objectMapper.convertValue(createdOrder, OrderDto.class);
    }

    public OrderDto readById(final Long id) throws ResourceNotFoundException {
        return orderRepository.findById(id).map(
                order -> objectMapper.convertValue(order, OrderDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Order with id = " + id + " doesn't exist"));
    }

    public Page<OrderDto> readPage(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(order -> objectMapper.convertValue(order, OrderDto.class));
    }

    @Transactional
    public void deleteById(final Long id) {
        if (orderRepository.findById(id).isEmpty()) {
            return;
        }
        orderRepository.deleteById(id);
    }

    private Order validateOrder(final CreateOrderDto createOrderDto) throws ResourceNotFoundException {
        String itemName = createOrderDto.getItem().getName();
        Optional<Item> itemFromDbByName = itemRepository.readByName(itemName);
        if (itemFromDbByName.isEmpty()) {
            throw new ResourceNotFoundException(NO_SUCH_ITEM_LITERAL + itemName);
        }
        Optional<Customer> customerFromDbById = customerRepository.findById(createOrderDto.getCustomerId());
        if (customerFromDbById.isEmpty()) {
            throw new ResourceNotFoundException(NO_SUCH_CUSTOMER_LITERAL + createOrderDto.getCustomerId());
        }
        return new Order(null, createOrderDto.getAmount(), null,
                itemFromDbByName.get(), customerFromDbById.get(), null);
    }

    private void orderItemFromWarehouse(Order order) throws UnprocessableEntityException {
        List<Warehouse> sortedWarehouses = warehouseRepository.findWarehouseForCustomer(order.getCustomer().getId());
        boolean processed = false;
        for (Warehouse warehouse: sortedWarehouses) {
            for (WarehouseItem item: warehouse.getItems()) {
                if (item.getItem().equals(order.getItem()) && item.getAmount() >= order.getAmount()) {
                    item.setAmount(item.getAmount() - order.getAmount());
                    if (item.getAmount() == 0) {
                        warehouse.getItems().remove(item);
                    }
                    warehouseRepository.save(warehouse);
                    order.setWarehouse(warehouse);
                    Distance distance = distanceRepository
                            .findDistanceByCustomerAndWarehouse(order.getCustomer().getId(), warehouse.getId());
                    order.setDistance(distance.getDistanceValue());
                    processed = true;
                    break;
                }
            }
            if (processed) {
                break;
            }
        }
        if (!processed) {
            throw new UnprocessableEntityException("There is not any warehouse" +
                    " that contains that item with such amount!");
        }
    }

}
