package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.ItemDto;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ObjectMapper objectMapper;

    private static final String ITEM_NAME_SHOULD_BE_UNIQUE_LITERAL = "Item with such name already exists. " +
            "Item name should be unique!";

    @Transactional
    public ItemDto create(final ItemDto itemDto) throws UnprocessableEntityException {
        checkInDbByName(itemDto.getName());
        Item itemFromDto = objectMapper.convertValue(itemDto, Item.class);
        Item createdItem = itemRepository.save(itemFromDto);
        return objectMapper.convertValue(createdItem, ItemDto.class);
    }

    @Transactional
    public ItemDto updateName(final Long id, final String newName)
            throws ResourceNotFoundException, UnprocessableEntityException {
        Optional<Item> fromDbById = itemRepository.findById(id);
        if (fromDbById.isEmpty()) {
            throw new ResourceNotFoundException("Item with id = " + id + " doesn't exist");
        }
        checkInDbByName(newName);
        Item item = fromDbById.get();
        item.setName(newName);
        Item savedItem = itemRepository.save(item);
        return objectMapper.convertValue(savedItem, ItemDto.class);
    }

    public ItemDto readById(final Long id) throws ResourceNotFoundException {
        return itemRepository.findById(id).map(item -> objectMapper.convertValue(item, ItemDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Item with id = " + id + " doesn't exist"));
    }

    public Page<ItemDto> readPage(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(item -> objectMapper.convertValue(item, ItemDto.class));
    }

    @Transactional
    public void deleteById(final Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            return;
        }
        itemRepository.deleteById(id);
    }

    @Transactional
    public void deleteByName(final String name) {
        if (itemRepository.readByName(name).isEmpty()) {
            return;
        }
        itemRepository.deleteByName(name);
    }

    private void checkInDbByName(final String itemName) throws UnprocessableEntityException {
        Optional<Item> fromDbByName = itemRepository.readByName(itemName);
        if (fromDbByName.isPresent()) {
            throw new UnprocessableEntityException(ITEM_NAME_SHOULD_BE_UNIQUE_LITERAL);
        }
    }

}
