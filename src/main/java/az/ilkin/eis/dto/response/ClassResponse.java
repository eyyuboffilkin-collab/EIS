package az.ilkin.eis.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponse {

    private Long id;
    private String name;
    private List<UserResponse> students;
    private List<UserResponse>teachers;
}
