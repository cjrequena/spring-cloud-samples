package com.cjrequena.sample.mapper;

import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.persistence.entity.OrderEntity;
import com.cjrequena.sample.proto.Order;
import org.mapstruct.*;

import java.util.List;

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

  Order toOrderProto(OrderEntity entity);

  // ========================================
  // Domain <-> Entity Mappings
  // ========================================
  com.cjrequena.sample.domain.model.Order toOrderDomain(OrderEntity entity);

  OrderEntity toEntity(com.cjrequena.sample.domain.model.Order order);

  // ========================================
  // DTO <-> Domain Mappings
  // ========================================
  com.cjrequena.sample.domain.model.Order toOrderDomain(OrderDTO dto);

  OrderDTO toDTO(com.cjrequena.sample.domain.model.Order domain);

  List<OrderDTO> toDTOList(List<com.cjrequena.sample.domain.model.Order> orders);



  // ========================================
  // DTO <-> Entity Mappings
  // ========================================
  OrderDTO toDTO(OrderEntity entity);

  // ========================================
  // Update Mappings
  // ========================================
  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromOrderProto(Order order, @MappingTarget OrderEntity entity);

  @Mapping(target = "version", source = "version", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromOrderDomain(com.cjrequena.sample.domain.model.Order order, @MappingTarget OrderEntity entity);
}
