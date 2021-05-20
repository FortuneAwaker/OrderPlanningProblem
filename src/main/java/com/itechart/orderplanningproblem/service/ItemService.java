package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.ItemDto;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id = " + id + " doesn't exist"));
        checkInDbByName(newName);
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
        itemRepository.findById(id).ifPresent(item -> itemRepository.deleteById(id));
    }

    private void checkInDbByName(final String itemName) throws UnprocessableEntityException {
        itemRepository.readByName(itemName).ifPresent(item -> {
            throw new UnprocessableEntityException(ITEM_NAME_SHOULD_BE_UNIQUE_LITERAL);
        });
    }

}
