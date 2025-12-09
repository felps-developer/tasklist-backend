package br.com.jtech.tasklist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListFilterDTO {

    @Min(value = 0, message = "Página deve ser maior ou igual a 0")
    private Integer page;

    @Min(value = 1, message = "Tamanho da página deve ser maior que 0")
    private Integer size;

    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    public int getPageOrDefault() {
        return page != null ? page : 0;
    }

    public int getSizeOrDefault() {
        return size != null ? size : 10;
    }
}

