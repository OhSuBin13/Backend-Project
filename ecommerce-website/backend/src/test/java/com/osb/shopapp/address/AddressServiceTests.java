package com.osb.shopapp.address;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.common.AppConstant;
import com.osb.shopapp.common.PageResponse;
import com.osb.shopapp.exception.OperationNotPermittedException;
import com.osb.shopapp.exception.ResourceNotFoundException;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.user.User;
import com.osb.shopapp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTests {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private Address addressA;
    private Address addressB;
    private AddressRequest addressRequestA;
    private AddressResponse addressResponseA;
    private AddressResponse addressResponseB;
    private User userA;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        Role adminRole = new Role(1, "ADMIN");
        Role userRole = new Role(2, "USER");
        userA = TestDataUtils.createUserA(Set.of(adminRole));
        User userB = TestDataUtils.createUserB(Set.of(userRole));

        addressA = TestDataUtils.createAddressA(userA);
        addressB = TestDataUtils.createAddressB(userB);
        addressRequestA = TestDataUtils.createAddressRequestA();
        addressResponseA = TestDataUtils.createAddressResponseA(userA.getId());
        addressResponseB = TestDataUtils.createAddressResponseB(userB.getId());
        authentication = new UsernamePasswordAuthenticationToken(
                userA,
                userA.getPassword(),
                userA.getAuthorities()
        );
    }

    @Test
    public void shouldSaveAddress() {
        when(userRepository.findById(userA.getId())).thenReturn(Optional.of(userA));
        when(addressMapper.toAddress(addressRequestA)).thenReturn(addressA);
        when(addressRepository.findAllByUserId(userA.getId())).thenReturn(List.of());
        when(addressRepository.save(addressA)).thenReturn(addressA);
        when(addressMapper.toAddressResponse(addressA)).thenReturn(addressResponseA);

        AddressResponse addressResponse = addressService.save(addressRequestA, authentication);

        assertThat(addressResponse).isEqualTo(addressResponseA);
        assertThat(addressA.getIsMain()).isTrue();
    }

    @Test
    public void shouldFindAllAddress() {
        Sort sort = AppConstant.SORT_DIR.equalsIgnoreCase("asc") ?
                Sort.by(AppConstant.SORT_ADDRESSES_BY).ascending() : Sort.by(AppConstant.SORT_ADDRESSES_BY).descending();
        Pageable pageable = PageRequest.of(AppConstant.PAGE_NUMBER_INT, AppConstant.PAGE_SIZE_INT, sort);
        Page<Address> page = new PageImpl<>(List.of(addressA, addressB));

        when(addressRepository.findAll(pageable)).thenReturn(page);
        when(addressMapper.toAddressResponse(addressA)).thenReturn(addressResponseA);
        when(addressMapper.toAddressResponse(addressB)).thenReturn(addressResponseB);

        PageResponse<AddressResponse> pageResponse =
                addressService.findAll(AppConstant.PAGE_NUMBER_INT, AppConstant.PAGE_SIZE_INT,
                        AppConstant.SORT_ADDRESSES_BY, AppConstant.SORT_DIR);

        assertThat(pageResponse).isNotNull();
        assertThat(pageResponse.getContent().size()).isEqualTo(2);
        assertThat(pageResponse.getContent().get(0)).isEqualTo(addressResponseA);
        assertThat(pageResponse.getContent().get(1)).isEqualTo(addressResponseB);
    }

    @Test
    public void shouldFindAllAddressByUserIdWhenValidRequest() {
        when(addressRepository.findAllWithAssociationsByUserId(addressA.getUser().getId()))
                .thenReturn(List.of(addressA));
        when(addressMapper.toAddressResponse(addressA)).thenReturn(addressResponseA);

        List<AddressResponse> addressResponses =
                addressService.findAllByUserId(addressA.getUser().getId(), authentication);

        assertThat(addressResponses.size()).isEqualTo(1);
        assertThat(addressResponses.get(0)).isEqualTo(addressResponseA);
    }

    @Test
    public void shouldNotFindAllAddressByUserIdWhenAddressOwnerIsNotLoggedIn() {
        assertThatThrownBy(() -> addressService.findAllByUserId(addressB.getUser().getId(), authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessage("You do not have permission to view the address of this user");
    }

    @Test
    public void shouldMakeAddressMainWhenValidRequest() {
        // Define a second address belonging to user A
        Address addressC = Address.builder()
                .id(3)
                .name("Test Address C")
                .country("United Kingdom")
                .street("Oxford Street")
                .state("England")
                .city("London")
                .postalCode("W1D 1BS")
                .phoneNumber("+44 20 1234 5678")
                .isMain(false)
                .addressType(AddressType.WORK)
                .user(addressA.getUser())
                .build();
        AddressResponse addressResponseC = AddressResponse.builder()
                .id(3)
                .name("Test Address C")
                .country("United Kingdom")
                .street("Oxford Street")
                .state("England")
                .city("London")
                .postalCode("W1D 1BS")
                .phoneNumber("+44 20 1234 5678")
                .isMain(true)
                .addressType(AddressType.WORK)
                .userId(addressA.getUser().getId())
                .build();
        addressA.setIsMain(true);

        when(addressRepository.existsById(addressC.getId())).thenReturn(true);
        when(addressRepository.findAllWithAssociationsByUserId(addressA.getUser().getId()))
                .thenReturn(List.of(addressA, addressC));
        when(addressMapper.toAddressResponse(addressC)).thenReturn(addressResponseC);

        AddressResponse addressResponse = addressService.makeMain(addressC.getId(), authentication);

        assertThat(addressResponse).isEqualTo(addressResponseC);
        assertThat(addressA.getIsMain()).isFalse();
        assertThat(addressC.getIsMain()).isTrue();
    }

    @Test
    public void shouldNotMakeAddressMainWhenInvalidAddressId() {
        when(addressRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> addressService.makeMain(99, authentication))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No address found with ID: 99");
    }

    @Test
    public void shouldNotMakeAddressMainWhenAddressOwnerIsNotLoggedIn() {
        when(addressRepository.existsById(addressB.getId())).thenReturn(true);
        when(addressRepository.findAllWithAssociationsByUserId(addressA.getUser().getId()))
                .thenReturn(List.of(addressA));

        assertThatThrownBy(() -> addressService.makeMain(addressB.getId(), authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessage("The specified address is related to another user");
    }

    @Test
    public void shouldUpdateAddressWhenValidRequest() {
        addressRequestA.setCountry("Updated country");
        addressRequestA.setStreet("Updated street");
        addressResponseA.setCountry("Updated country");
        addressResponseA.setStreet("Updated street");

        when(addressRepository.findWithAssociationById(addressA.getId())).thenReturn(Optional.of(addressA));
        when(addressRepository.save(addressA)).thenReturn(addressA);
        when(addressMapper.toAddressResponse(addressA)).thenReturn(addressResponseA);

        AddressResponse addressResponse = addressService.update(addressRequestA, addressA.getId(), authentication);

        assertThat(addressResponse).isEqualTo(addressResponseA);
        assertThat(addressA.getCountry()).isEqualTo("Updated country");
        assertThat(addressA.getStreet()).isEqualTo("Updated street");
    }

    @Test
    public void shouldNotUpdateAddressWhenInvalidAddressId() {
        when(addressRepository.findWithAssociationById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.update(addressRequestA, 99, authentication))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No address found with ID: 99");
    }

    @Test
    public void shouldNotUpdateAddressWhenNotAdminAndAddressOwnerIsNotLoggedIn() {
        authentication = new UsernamePasswordAuthenticationToken(
                addressB.getUser(),
                addressB.getUser().getPassword(),
                addressB.getUser().getAuthorities()
        );

        when(addressRepository.findWithAssociationById(addressA.getId())).thenReturn(Optional.of(addressA));

        assertThatThrownBy(() -> addressService.update(addressRequestA, addressA.getId(), authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessage("You do not have permission to update the address of this user");
    }

    @Test
    public void shouldDeleteAddressWhenValidRequest() {
        when(addressRepository.findWithAssociationById(addressA.getId())).thenReturn(Optional.of(addressA));

        addressService.delete(addressA.getId(), authentication);

        verify(addressRepository, times(1)).deleteById(addressA.getId());
    }

    @Test
    public void shouldNotDeleteAddressWhenInValidAddressId() {
        when(addressRepository.findWithAssociationById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.delete(99, authentication))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No address found with ID: 99");
    }

    @Test
    public void shouldNotDeleteAddressWhenNotAdminAndAddressOwnerIsNotLoggedIn() {
        authentication = new UsernamePasswordAuthenticationToken(
                addressB.getUser(),
                addressB.getUser().getPassword(),
                addressB.getUser().getAuthorities()
        );

        when(addressRepository.findWithAssociationById(addressA.getId())).thenReturn(Optional.of(addressA));

        assertThatThrownBy(() -> addressService.delete(addressA.getId(), authentication))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessage("You do not have permission to delete the address of this user");
    }
}
