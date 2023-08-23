package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.OrderEntity;
import com.cjrequena.sample.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {

  OrderEntity toEntity(OrderDTO dto);

  OrderDTO toDTO(OrderEntity entity);
}
