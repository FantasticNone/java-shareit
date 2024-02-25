package ru.practicum.shareit.valid;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataException;

@Component
public class PageableValidator {

    public void checkingPageableParams(Integer from, Integer size) {
        if (size < 0 || from < 0) {
            throw new DataException("Size OR From in page can't be < 0");
        }
    }
}