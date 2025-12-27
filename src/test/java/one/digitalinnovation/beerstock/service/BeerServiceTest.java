package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Diz que tem que usar uma extensão do Mockito para conseguir rodar o teste
@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

	private static final long INVALID_BEER_ID = 1L;

	@Mock
	private BeerRepository beerRepository;
	@Mock
	private BeerMapper beerMapper;

	// Faz uma injeção do BeerRepository
	@InjectMocks
	private BeerService beerService;

	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
		// given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		// Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);
		Beer expectedSavedBeer = Beer.builder()
				.id(expectedBeerDTO.getId())
	            .name(expectedBeerDTO.getName())
	            .brand(expectedBeerDTO.getBrand())
	            .max(expectedBeerDTO.getMax())
	            .quantity(expectedBeerDTO.getQuantity())
	            .type(expectedBeerDTO.getType())
	            .build();
		
		// Configuração do mock BeerMapper
		when(beerMapper.toModel(expectedBeerDTO)).thenReturn(expectedSavedBeer);
        when(beerMapper.toDTO(expectedSavedBeer)).thenReturn(expectedBeerDTO);

		// when
		when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

		// then
		BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

		assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
	}

	@Test
	void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
		// given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		// Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);
		Beer duplicatedBeer = Beer.builder()
				.id(expectedBeerDTO.getId())
				.name(expectedBeerDTO.getName())
				.build();
		
		// when
		when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

		// then
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
	}

	@Test
	void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		// Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		Beer expectedFoundBeer = Beer.builder()
	            .id(expectedFoundBeerDTO.getId())
	            .name(expectedFoundBeerDTO.getName())
	            .brand(expectedFoundBeerDTO.getBrand())
	            .max(expectedFoundBeerDTO.getMax())
	            .quantity(expectedFoundBeerDTO.getQuantity())
	            .type(expectedFoundBeerDTO.getType())
	            .build();
		
		// Configuração do mock BeerMapper
		when(beerMapper.toDTO(expectedFoundBeer)).thenReturn(expectedFoundBeerDTO);

		// when
		when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

		// then
		BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

		assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
	}

	@Test
	void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when
		when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

		// then
		assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));
	}

	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		// Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		Beer expectedFoundBeer = Beer.builder()
	            .id(expectedFoundBeerDTO.getId())
	            .name(expectedFoundBeerDTO.getName())
	            .brand(expectedFoundBeerDTO.getBrand())
	            .max(expectedFoundBeerDTO.getMax())
	            .quantity(expectedFoundBeerDTO.getQuantity())
	            .type(expectedFoundBeerDTO.getType())
	            .build();
		
		// Configuração do mock BeerMapper
		when(beerMapper.toDTO(expectedFoundBeer)).thenReturn(expectedFoundBeerDTO);

		// when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

		// then
		List<BeerDTO> foundListBeersDTO = beerService.listAll();

		assertThat(foundListBeersDTO, is(not(empty())));
		assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
	}

	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
		// when
		when(beerRepository.findAll()).thenReturn(Collections.emptyList());

		// then
		List<BeerDTO> foundListBeersDTO = beerService.listAll();

		assertThat(foundListBeersDTO, is(empty()));
	}

	@Test
	void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
		// given
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		// Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);
		Beer expectedDeletedBeer = Beer.builder()
	            .id(expectedDeletedBeerDTO.getId())
	            .name(expectedDeletedBeerDTO.getName())
	            .brand(expectedDeletedBeerDTO.getBrand())
	            .max(expectedDeletedBeerDTO.getMax())
	            .quantity(expectedDeletedBeerDTO.getQuantity())
	            .type(expectedDeletedBeerDTO.getType())
	            .build();

		// when
		when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

		// then
		beerService.deleteById(expectedDeletedBeerDTO.getId());

		verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
		verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
	}

	@Test
	void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
		// given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		expectedBeerDTO.setQuantity(10);
		expectedBeerDTO.setMax(100);
		
		// Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
		Beer expectedBeer = Beer.builder()
	            .id(expectedBeerDTO.getId())
	            .name(expectedBeerDTO.getName())
	            .brand(expectedBeerDTO.getBrand())
	            .max(expectedBeerDTO.getMax())
	            .quantity(expectedBeerDTO.getQuantity())
	            .type(expectedBeerDTO.getType())
	            .build();
		
		// Configuração do mock BeerMapper
        when(beerMapper.toDTO(expectedBeer)).thenReturn(expectedBeerDTO);

		// when
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
		when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

		int quantityToIncrement = 10;
		
		// Ajuste da quantidade esperada na expectedBeer
		expectedBeer.setQuantity(expectedBeer.getQuantity() + quantityToIncrement);
		when(beerMapper.toDTO(expectedBeer)).thenReturn(expectedBeerDTO);

		// then
		BeerDTO incrementedBeerDTO = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);
		
		assertThat(expectedBeer.getQuantity(), equalTo(20));
		assertThat(incrementedBeerDTO.getQuantity(), lessThan(expectedBeerDTO.getMax()));
	}

	@Test
	void whenIncrementIsGreatherThanMaxThenThrowException() {
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		expectedBeerDTO.setQuantity(10);
		expectedBeerDTO.setMax(50);
		
		// Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
		Beer expectedBeer = Beer.builder()
	            .id(expectedBeerDTO.getId())
	            .name(expectedBeerDTO.getName())
	            .brand(expectedBeerDTO.getBrand())
	            .max(expectedBeerDTO.getMax())
	            .quantity(expectedBeerDTO.getQuantity())
	            .type(expectedBeerDTO.getType())
	            .build();
		
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

		int quantityToIncrement = 80;
		assertThrows(BeerStockExceededException.class,
				() -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
	}

	@Test
	void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		expectedBeerDTO.setQuantity(30);
		expectedBeerDTO.setMax(50);
		// Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
		Beer expectedBeer = Beer.builder()
	            .id(expectedBeerDTO.getId())
	            .name(expectedBeerDTO.getName())
	            .brand(expectedBeerDTO.getBrand())
	            .max(expectedBeerDTO.getMax())
	            .quantity(expectedBeerDTO.getQuantity())
	            .type(expectedBeerDTO.getType())
	            .build();
		
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

		int quantityToIncrement = 45;
		assertThrows(BeerStockExceededException.class,
				() -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
	}

	@Test
	void whenIncrementIsCalledWithInvalidIdThenThrowException() {
		int quantityToIncrement = 10;

		when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

		assertThrows(BeerNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
	}

	@Test
	void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		expectedBeerDTO.setQuantity(30);
		
		// Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
		Beer expectedBeer = Beer.builder()
	            .id(expectedBeerDTO.getId())
	            .name(expectedBeerDTO.getName())
	            .brand(expectedBeerDTO.getBrand())
	            .max(expectedBeerDTO.getMax())
	            .quantity(expectedBeerDTO.getQuantity())
	            .type(expectedBeerDTO.getType())
	            .build();
		
		// Configuração do mock BeerMapper
        when(beerMapper.toDTO(expectedBeer)).thenReturn(expectedBeerDTO);

		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
		when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

		int quantityToDecrement = 5;
		
		// Ajustando a quantidade esperada
		expectedBeer.setQuantity(expectedBeer.getQuantity() - quantityToDecrement);
		expectedBeerDTO.setQuantity(15);
		
		BeerDTO decrementBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);

		assertThat(decrementBeerDTO.getQuantity(), equalTo(15));
		assertThat(decrementBeerDTO.getQuantity(), greaterThan(0));
	}

	@Test
    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        expectedBeerDTO.setQuantity(10);
        
        // Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
        Beer expectedBeer = Beer.builder()
                .id(expectedBeerDTO.getId())
                .name(expectedBeerDTO.getName())
                .brand(expectedBeerDTO.getBrand())
                .max(expectedBeerDTO.getMax())
                .quantity(expectedBeerDTO.getQuantity())
                .type(expectedBeerDTO.getType())
                .build();
		
		// Configuração do mock BeerMapper
        when(beerMapper.toDTO(expectedBeer)).thenReturn(expectedBeerDTO);
  
        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
  
        int quantityToDecrement = 10;
        
        // Ajustando a quantidade
        expectedBeerDTO.setQuantity(expectedBeer.getQuantity() - quantityToDecrement);
        expectedBeerDTO.setQuantity(0);
        
        BeerDTO decrementBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
  
        assertThat(decrementBeerDTO.getQuantity(), equalTo(0));
    }
  
    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        expectedBeerDTO.setQuantity(10);
        
        // Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
        Beer expectedBeer = Beer.builder()
                .id(expectedBeerDTO.getId())
                .name(expectedBeerDTO.getName())
                .brand(expectedBeerDTO.getBrand())
                .max(expectedBeerDTO.getMax())
                .quantity(expectedBeerDTO.getQuantity())
                .type(expectedBeerDTO.getType())
                .build();
  
        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
  
        int quantityToDecrement = 80;
        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
    }
  
    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;
  
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
  
        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
    }
}
