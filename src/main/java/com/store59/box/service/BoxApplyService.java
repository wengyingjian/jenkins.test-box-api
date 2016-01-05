package com.store59.box.service;

import com.store59.box.model.BoxApply;
import com.store59.box.util.ResultUtil;
import com.store59.box.viewmodel.Result;
import com.store59.box.viewmodel.ViewBoxApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhangwangyong on 15/7/30.
 */
@Service
public class BoxApplyService{

    @Autowired
    private com.store59.box.remoting.BoxApplyService boxApplyServiceApi;

    public Result addBoxApply(ViewBoxApply viewBoxApply) {
        BoxApply boxApply = new BoxApply();
        boxApply.setRoom(viewBoxApply.getRoom());
        boxApply.setPhone(viewBoxApply.getPhone());
        boxApply.setOwner(viewBoxApply.getName());
        boxApply.setDormentryId(viewBoxApply.getDormentryId());
        boxApply.setUid(viewBoxApply.getUid());
        return ResultUtil.resultService2View(boxApplyServiceApi.addBoxApply(boxApply));
    }
}
