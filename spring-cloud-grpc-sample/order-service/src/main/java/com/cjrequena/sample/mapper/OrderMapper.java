package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.OrderEntity;
import com.cjrequena.sample.dto.OrderDTO;
import com.cjrequena.sample.proto.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {

  OrderEntity toEntity(OrderDTO dto);

  OrderDTO toDTO(OrderEntity entity);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "accountId", target = "accountId")
  @Mapping(source = "total", target = "total")
  OrderEntity toEntity(Order order);

  Order toOrder(OrderEntity entity);

}
