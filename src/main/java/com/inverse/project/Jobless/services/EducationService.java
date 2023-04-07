package com.inverse.project.Jobless.services;

import com.inverse.project.Jobless.dto.EducationDto;

public interface EducationService {
    EducationDto create(EducationDto educationDto, Integer resumeId);
    EducationDto update (EducationDto educationDto, Integer resumeId, Integer id);
    void delete(Integer resumeId, Integer id);
}
