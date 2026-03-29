package com.store.order_service.service;

import com.store.order_service.dto.InventoryResponse;
import com.store.order_service.dto.OrderLineItemsDto;
import com.store.order_service.dto.OrderRequest;
import com.store.order_service.event.OrderPlacedEvent;
import com.store.order_service.model.Order;
import com.store.order_service.model.OrderLineItems;
import com.store.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    @LoadBalanced
    WebClient.Builder webClientBuilder;

    @Autowired
    KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItemsDto> orderLineItemsDtoList = orderRequest.getOrderLineItemsDtoList();
        List<OrderLineItems> orderLineItemsList = new ArrayList<>();
        BigDecimal totalOrderValue = BigDecimal.ZERO;
        for(OrderLineItemsDto orderLineItemsDto : orderLineItemsDtoList){
            OrderLineItems orderLineItems = new OrderLineItems();
            //orderLineItems.setId(orderLineItemsDto.getId());
            orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
            orderLineItems.setPrice(orderLineItemsDto.getPrice());
            orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
            orderLineItems.setOrder(order);
            orderLineItemsList.add(orderLineItems);
            totalOrderValue = totalOrderValue.add(orderLineItemsDto.getPrice());
        }
        order.setOrderLineItemsList(orderLineItemsList);
        order.setOrderValue(totalOrderValue);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();
        //call inventory service and place order if product is available in stock
        // url will be like this http://localhost:8082/api/inventory?skuCode=iPhone17&skuCode=iPhone16

        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::getIsInStock);
        if(allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        }else{
            throw new IllegalArgumentException("product is not in stock!, please try again later..");
        }
    }
}
