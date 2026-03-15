package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@Schema(description = "用户投票请求体")
public class UserVoteForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "投票列表id")
    private List<Integer> vote;


}
