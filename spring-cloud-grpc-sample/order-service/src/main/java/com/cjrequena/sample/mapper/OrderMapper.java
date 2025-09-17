package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.OrderEntity;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.proto.Order;
import org.mapstruct.*;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {

  // ========================================
  // Order Proto <-> Entity Mappings
  // ========================================

  OrderEntity toEntity(OrderDTO dto);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "accountId", target = "accountId")
  @Mapping(source = "total", target = "total")
  OrderEntity toEntity(Order order);

  Order toOrder(OrderEntity entity);

  // ========================================
  // DTO <-> Entity Mappings
  // ========================================
  OrderDTO toDTO(OrderEntity entity);

  // ========================================
  // Update Mappings
  // ========================================
  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromOrder(Order order, @MappingTarget OrderEntity entity);
}
