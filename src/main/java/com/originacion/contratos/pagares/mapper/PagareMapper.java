package com.originacion.contratos.pagares.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.originacion.contratos.pagares.dto.PagareCreateDto;
import com.originacion.contratos.pagares.dto.PagareUpdateDto;
import com.originacion.contratos.pagares.dto.PagareDto;
import com.originacion.contratos.pagares.model.Pagare;

//Mapper para convertir entre Pagare y sus DTOs, ignorando campos no manejados por el cliente.
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)

public interface PagareMapper {
    // Creación: sólo seteas idSolicitud, numeroCuota y rutaArchivo
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaGenerado", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "idSolicitud", expression = "java(12345L)")
    Pagare toEntity(PagareCreateDto dto);

    // Actualización: sólo modificas numeroCuota y/o rutaArchivo
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idSolicitud", ignore = true)
    @Mapping(target = "fechaGenerado", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntity(@MappingTarget Pagare entity, PagareUpdateDto dto);

    //Mapea la entidad a DTO de respuesta.

    PagareDto toDto(Pagare entity);

    //Mapea una lista de entidades a una lista de DTOs.
    List<PagareDto> toDtoList(List<Pagare> entities);
}