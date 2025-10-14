package com.f1.fastone.util;

import com.f1.fastone.menu.entity.Menu;
import com.f1.fastone.store.entity.Store;
import java.util.UUID;

public class TestMenuFactory {
    public static Menu createMenu(Store store) {
        return Menu.builder()
                .id(UUID.randomUUID())
                .store(store)
                .name("짱짱 맛있는 햄버거")
                .price(5500)
                .imageUrl("img.jpg")
                .build();
    }
}
