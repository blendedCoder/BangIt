package com.bangIt.blended.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bangIt.blended.domain.dto.place.PlaceDTO;
import com.bangIt.blended.domain.dto.placesList.SearchPlaceDTO;

@Mapper
public interface PlaceMapper {

	List<PlaceDTO> findprocess(SearchPlaceDTO dto);

}
