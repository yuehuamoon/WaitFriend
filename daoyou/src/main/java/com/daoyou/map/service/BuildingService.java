package com.daoyou.map.service;

import com.daoyou.common.BizException;
import com.daoyou.common.ErrorCodes;
import com.daoyou.map.dto.BuildingRequest;
import com.daoyou.map.dto.BuildingView;
import com.daoyou.map.entity.Building;
import com.daoyou.map.mapper.ArticleBuildingMapper;
import com.daoyou.map.mapper.BuildingMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuildingService {

 private final BuildingMapper buildingMapper;
 private final ArticleBuildingMapper articleBuildingMapper;

 public List<BuildingView> listOpen() {
 return buildingMapper.selectOpen().stream().map(BuildingView::from).toList();
 }

 public BuildingView get(Long id) {
 Building building = buildingMapper.selectOpenById(id);
 if (building == null) {
 throw new BizException(ErrorCodes.BUILDING_NOT_FOUND, "板块不存在");
 }
 return BuildingView.from(building);
 }

 public Building requireActive(Long id) {
 Building building = buildingMapper.selectOpenById(id);
 if (building == null) {
 throw new BizException(ErrorCodes.BUILDING_NOT_FOUND, "板块不存在");
 }
 return building;
 }

 @Transactional
 public BuildingView create(BuildingRequest request) {
 if (buildingMapper.countByName(request.name(), null) >0) {
 throw new BizException(ErrorCodes.BUILDING_NAME_EXISTS, "板块名称已存在");
 }
 Building building = toEntity(request);
 building.setStatus(1);
 building.setIsDeleted(0);
 buildingMapper.insert(building);
 return BuildingView.from(building);
 }

 @Transactional
 public BuildingView update(Long id, BuildingRequest request) {
 Building building = requireActive(id);
 if (buildingMapper.countByName(request.name(), id) >0) {
 throw new BizException(ErrorCodes.BUILDING_NAME_EXISTS, "板块名称已存在");
 }
 building.setName(request.name());
 building.setRegion(request.region());
 building.setDescription(request.description());
 building.setCoverUrl(request.coverUrl());
 building.setSortOrder(request.sortOrder());
 buildingMapper.updateById(building);
 return BuildingView.from(building);
 }

 @Transactional
 public void delete(Long id) {
 requireActive(id);
 if (articleBuildingMapper.countByBuildingId(id) >0) {
 throw new BizException(ErrorCodes.BUILDING_HAS_ARTICLES, "板块下还有文章，不能删除");
 }
 if (buildingMapper.logicalDelete(id) ==0) {
 throw new BizException(ErrorCodes.BUILDING_NOT_FOUND, "板块不存在或已删除");
 }
 }

 private Building toEntity(BuildingRequest request) {
 Building building = new Building();
 building.setName(request.name());
 building.setRegion(request.region());
 building.setDescription(request.description());
 building.setCoverUrl(request.coverUrl());
 building.setSortOrder(request.sortOrder() == null ?0 : request.sortOrder());
 return building;
 }
}
