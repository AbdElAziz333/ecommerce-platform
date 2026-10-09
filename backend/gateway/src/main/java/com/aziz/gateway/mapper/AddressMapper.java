package com.aziz.gateway.mapper;

import com.aziz.gateway.dto.request.CreateAddressRequest;
import com.aziz.gateway.dto.request.UpdateAddressRequest;
import com.aziz.gateway.dto.response.AddressDto;
import com.aziz.gateway.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressDto toDto(Address a) {
        return new AddressDto(a.getId(), a.getLabel(), a.getStreetLine(), a.getCity(),
                a.getState(), a.getPostalCode(), a.getDefaultShipping());
    }

    // user and defaultShipping are set by the service
    public Address toEntity(CreateAddressRequest r) {
        return Address.builder()
                .label(r.label())
                .streetLine(r.streetLine())
                .city(r.city())
                .state(r.state())
                .postalCode(r.postalCode())
                .build();
    }

    // PATCH: only apply fields that were sent
    public void updateEntity(Address a, UpdateAddressRequest r) {
        if (r.label() != null) {
            a.setLabel(r.label());
        }

        if (r.streetLine() != null) {
            a.setStreetLine(r.streetLine());
        }

        if (r.city() != null) {
            a.setCity(r.city());
        }

        if (r.state() != null) {
            a.setState(r.state());
        }

        if (r.postalCode() != null) {
            a.setPostalCode(r.postalCode());
        }
    }
}