package one.digitalinnovation.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExceededException extends Exception {

    public BeerStockExceededException(Long id, int quantityToIncrement) {
        super(String.format("Beers with %s ID to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }
    
    public BeerStockExceededException(Long id, int quantityToDecrement, boolean isDecrement) {
        super(String.format("Beers with %s ID to decrement informed would result in negative stock. Current quantity minus decrement: %s", id, quantityToDecrement));
    }
}
