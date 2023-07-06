package io.livevideo.server.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * @description:
 * @author:25445
 * @date:2022/12/4 20:46
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class DirDTO {
    private String url;
}
