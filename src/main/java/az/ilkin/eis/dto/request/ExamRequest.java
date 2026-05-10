package az.ilkin.eis.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExamRequest {

    @NotBlank(message = "Imtahan adi bos ola bilmez")
    private String title;


    @NotNull(message = "Muddet gosterilmelidir")
    @Min(value = 1,message = "Muddet en az 1 deqiqe olmalidir")
    private Integer duration; //deqiqe ile


    @NotNull(message = "Asan sualin bali gosterilmelidir")
    @Min(value = 1,message = "Bal musbet olmalidir")
    private Integer easyScore;


    @NotNull(message = "Orta sualin bali gosterilmelidir")
    @Min(value = 1,message = "Bal musbet olmalidir")
    private Integer mediumScore;


    @NotNull(message = "Cetin sualin bali gosterilmelidir")
    @Min(value = 1,message = "Bal musbet olmalidir")
    private Integer hardScore;
}
