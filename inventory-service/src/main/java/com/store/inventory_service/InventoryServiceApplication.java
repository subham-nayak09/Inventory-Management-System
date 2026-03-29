package com.store.inventory_service;

import com.store.inventory_service.model.Inventory;
import com.store.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("SMIPH16");
			inventory.setQuantity(5);
			inventoryRepository.save(inventory);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("SMIPH17PRO");
			inventory1.setQuantity(5);
			inventoryRepository.save(inventory1);

			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("BUDS2");
			inventory2.setQuantity(5);
			inventoryRepository.save(inventory2);

			Inventory inventory3 = new Inventory();
			inventory3.setSkuCode("BUDS2PRO");
			inventory3.setQuantity(5);
			inventoryRepository.save(inventory3);
		};
	}

}
