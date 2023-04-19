package com.fastcampus.loan.service;

import com.fastcampus.loan.dto.EntryDTO;

public interface EntryService {
    EntryDTO.Response create(Long applicationId, EntryDTO.Request request);

    EntryDTO.Response get(Long applicationId);

    EntryDTO.UpdateResponse update(Long entryId, EntryDTO.Request request);
}
