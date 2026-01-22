package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.FooDTO;
import com.cjrequena.sample.domain.model.vo.FooVO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface FooMapper {

  FooVO toFooVO(FooDTO dto);
  FooDTO toFooDTO(FooVO vo);

}
