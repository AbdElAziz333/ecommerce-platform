package com.aziz.gateway.service;

import com.aziz.gateway.dto.request.CreateAddressRequest;
import com.aziz.gateway.dto.request.UpdateAddressRequest;
import com.aziz.gateway.dto.response.AddressDto;
import com.aziz.gateway.mapper.AddressMapper;
import com.aziz.gateway.model.Address;
import com.aziz.gateway.model.User;
import com.aziz.gateway.repository.AddressRepository;
import com.aziz.gateway.util.exceptions.BadRequestException;
import com.aziz.gateway.util.exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository repository;
    private final AddressMapper mapper;
    private final EntityManager entityManager;

    private static final int MAX_ADDRESSES = 10;

    @Transactional(readOnly = true)
    public List<AddressDto> getAddresses(Long userId) {
        return repository.findAllByUserIdOrderByDefaultShippingDescCreatedAtAsc(userId)
                .stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AddressDto getAddressById(Long userId, Long id) {
        return mapper.toDto(findOwned(userId, id));
    }

    @Transactional
    public AddressDto createAddress(Long userId, CreateAddressRequest request) {
        long count = repository.countByUserId(userId);
        if (count >= MAX_ADDRESSES) {
            throw new BadRequestException("Maximum of " + MAX_ADDRESSES + " addresses reached");
        }

        // the first address is always the default
        boolean makeDefault = request.defaultShipping() || count == 0;
        if (makeDefault) {
            repository.clearDefaultShipping(userId);
        }

        Address address = mapper.toEntity(request);
        address.setUser(entityManager.getReference(User.class, userId)); // no DB query
        address.setDefaultShipping(makeDefault);
        repository.save(address);

        log.info("Address {} created for user {}", address.getId(), userId);
        return mapper.toDto(address);
    }

    @Transactional
    public AddressDto updateAddress(Long userId, Long id, UpdateAddressRequest request) {
        Address address = findOwned(userId, id);
        mapper.updateEntity(address, request);

        // PATCH can only make an address the default; to change the default, set another one to true
        if (Boolean.TRUE.equals(request.defaultShipping())) {
            repository.clearOtherDefaults(userId, id);
            address.setDefaultShipping(true);
        }
        return mapper.toDto(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long id) {
        Address address = findOwned(userId, id);
        boolean wasDefault = address.getDefaultShipping();
        repository.delete(address);

        // promote the oldest remaining address so the user still has a default
        if (wasDefault) {
            repository.findFirstByUserIdAndIdNotOrderByCreatedAtAsc(userId, id)
                    .ifPresent(a -> a.setDefaultShipping(true));
        }
        log.info("Address {} deleted for user {}", id, userId);
    }

    private Address findOwned(Long userId, Long id) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Address not found: " + id));
    }
}