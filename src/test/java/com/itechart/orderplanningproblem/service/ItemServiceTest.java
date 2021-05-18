package com.itechart.orderplanningproblem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itechart.orderplanningproblem.dto.ItemDto;
import com.itechart.orderplanningproblem.entity.Item;
import com.itechart.orderplanningproblem.error.exception.ResourceNotFoundException;
import com.itechart.orderplanningproblem.error.exception.UnprocessableEntityException;
import com.itechart.orderplanningproblem.repository.ItemRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ItemService itemService;

    @Test
    void ItemNameToEdit_EditItemWithIdThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long itemId = 12503L;
        String itemNewName = "New name";
        // when
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.updateName(itemId, itemNewName));

    }

    @Test
    void ItemNameToEdit_EditItemWithExistentName_ThrowUnprocessableEntityException() {
        // given
        Long itemId = 1L;
        String itemNewName = "New name";
        Item itemInDbById = Item.builder()
                .id(itemId)
                .name("Old item name")
                .build();
        Item itemInDbByName = Item.builder()
                .id(2L)
                .name(itemNewName)
                .build();
        // when
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemInDbById));
        Mockito.when(itemRepository.readByName(itemNewName)).thenReturn(Optional.of(itemInDbByName));
        // then
        Assertions.assertThrows(UnprocessableEntityException.class, () -> itemService.updateName(itemId, itemNewName));

    }

    @Test
    void ItemNameToEdit_EditItem_ReturnEditedItem() throws ResourceNotFoundException, UnprocessableEntityException {
        // given
        Long itemId = 1L;
        String itemNewName = "New name";
        Item itemInDbById = Item.builder()
                .id(itemId)
                .name("Old item name")
                .build();
        Item itemInDbAfterNameWasChanged = Item.builder()
                .id(itemId)
                .name(itemNewName)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemNewName)
                .build();
        // when
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemInDbById));
        Mockito.when(itemRepository.readByName(itemNewName)).thenReturn(Optional.empty());
        Mockito.when(itemRepository.save(itemInDbById)).thenReturn(itemInDbAfterNameWasChanged);
        Mockito.when(objectMapper.convertValue(itemInDbAfterNameWasChanged, ItemDto.class))
                .thenReturn(itemDto);
        // then
        Assertions.assertEquals(itemDto, itemService.updateName(itemId, itemNewName));

    }

    @Test
    void ItemId_FindByIdItemThatDoesNotExist_ThrowResourceNotFoundException() {
        // given
        Long itemId = 12503L;
        // when
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.readById(itemId));

    }

    @Test
    void ItemId_FindByIdItem_ReturnItem() throws ResourceNotFoundException {
        // given
        Long itemId = 1L;
        String itemName = "Chocolate";
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .build();
        // when
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(objectMapper.convertValue(item, ItemDto.class))
                .thenReturn(itemDto);

        // then
        Assertions.assertEquals(itemDto, itemService.readById(itemId));

    }

    @Test
    void ReadPageOfItems_ReturnPageOfItems() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Long itemId = 1L;
        String itemName = "Chocolate";
        Item item = Item.builder()
                .id(itemId)
                .name(itemName)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .build();
        List<Item> itemList = Collections.singletonList(item);
        List<ItemDto> itemDtoList = Collections.singletonList(itemDto);
        Page<Item> itemPage = new PageImpl<>(itemList);
        Page<ItemDto> itemDtoWithIdPage = new PageImpl<>(itemDtoList);
        // when
        Mockito.when(itemRepository.findAll(pageRequest)).thenReturn(itemPage);
        Mockito.when(objectMapper.convertValue(item, ItemDto.class))
                .thenReturn(itemDto);
        // then
        Assertions.assertEquals(itemDtoWithIdPage, itemService.readPage(pageRequest));

    }

    @Test
    void ItemToCreate_CreateItem_ReturnCreatedItem() throws UnprocessableEntityException {
        // given
        Long itemId = 1L;
        String itemName = "Chocolate";
        Item item = Item.builder()
                .name(itemName)
                .build();
        Item createdItem = Item.builder()
                .id(itemId)
                .name(itemName)
                .build();
        ItemDto itemDtoToBeCreated = ItemDto.builder()
                .name(itemName)
                .build();
        ItemDto createdItemDto = ItemDto.builder()
                .id(itemId)
                .name(itemName)
                .build();
        // when
        Mockito.when(itemRepository.readByName(itemName)).thenReturn(Optional.empty());
        Mockito.when(objectMapper.convertValue(itemDtoToBeCreated, Item.class))
                .thenReturn(item);
        Mockito.when(itemRepository.save(item)).thenReturn(createdItem);
        Mockito.when(objectMapper.convertValue(createdItem, ItemDto.class))
                .thenReturn(createdItemDto);

        // then
        Assertions.assertEquals(createdItemDto, itemService.create(itemDtoToBeCreated));

    }
}
