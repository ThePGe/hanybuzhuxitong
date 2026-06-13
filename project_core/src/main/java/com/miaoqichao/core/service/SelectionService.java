package com.miaoqichao.core.service;

import com.miaoqichao.core.dto.selection.AdminUpdateSelectionReq;
import com.miaoqichao.core.dto.selection.ClothingVO;
import com.miaoqichao.core.dto.selection.SelectionSubmitReq;

import java.util.List;

public interface SelectionService {

    /**
     * 学生获取选款列表
     */
    List<ClothingVO> getSelectionClothingList();

    /**
     * 学生提交选款
     */
    void submitSelection(SelectionSubmitReq req);

    /**
     * 管理员干预选款
     */
    void adminUpdateSelection(AdminUpdateSelectionReq req);
}
