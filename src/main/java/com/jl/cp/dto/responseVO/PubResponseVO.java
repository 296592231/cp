package com.jl.cp.dto.responseVO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author jl
 * @date 2021/7/22 16:25
 */
@Getter
@Setter
public class PubResponseVO implements Serializable {

    private String respCode;
    private String respMsg;
    private Object data;
}
