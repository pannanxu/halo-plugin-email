package io.mvvm.halo.plugins.email;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.InputStreamSource;


/**
 * Attach.
 *
 * @author: pan
 **/
@Data
@Builder
public class Attach {
    /**
     * 文件名称（含后缀，例如：xxx.png）
     */
    private String name;
    /**
     * 文件类型（可选）
     */
    private String contentType;
    /**
     * 文件。
     * <p>
     * 例如读取系统路径文件：{@link org.springframework.core.io.FileSystemResource}
     */
    private InputStreamSource source;

}
