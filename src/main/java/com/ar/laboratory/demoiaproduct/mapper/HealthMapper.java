package com.ar.laboratory.demoiaproduct.mapper;

import com.ar.laboratory.demoiaproduct.dto.HealthInfo;
import com.ar.laboratory.demoiaproduct.dto.HealthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper usando MapStruct para convertir entre HealthInfo y HealthResponse
 * MapStruct genera automáticamente la implementación en tiempo de compilación
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HealthMapper {
    
    /**
     * Convierte HealthInfo a HealthResponse
     * @Mapping mapea campos con nombres diferentes
     */
    @Mapping(source = "applicationStatus", target = "status")
    @Mapping(source = "statusMessage", target = "message")
    @Mapping(source = "applicationVersion", target = "version")
    @Mapping(source = "checkTime", target = "timestamp")
    HealthResponse toHealthResponse(HealthInfo healthInfo);
    
    /**
     * Convierte HealthResponse a HealthInfo
     */
    @Mapping(source = "status", target = "applicationStatus")
    @Mapping(source = "message", target = "statusMessage")
    @Mapping(source = "version", target = "applicationVersion")
    @Mapping(source = "timestamp", target = "checkTime")
    HealthInfo toHealthInfo(HealthResponse healthResponse);
}
