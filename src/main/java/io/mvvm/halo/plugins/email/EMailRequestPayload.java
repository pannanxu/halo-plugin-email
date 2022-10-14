package io.mvvm.halo.plugins.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: pan
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EMailRequestPayload {

    private String endpoint;
    private Object data;
}