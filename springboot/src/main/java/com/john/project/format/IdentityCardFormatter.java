package com.john.project.format;

import com.john.project.common.baseService.BaseService;
import com.john.project.entity.IdentityCardEntity;
import com.john.project.entity.UserEmailEntity;
import com.john.project.enums.OrganizeTypeEnum;
import com.john.project.model.IdentityCardModel;
import com.john.project.model.UserEmailModel;
import com.john.project.model.UserModel;
import com.john.project.service.OrganizeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentityCardFormatter extends BaseService {

    @Autowired
    private OrganizeService organizeService;

    public IdentityCardModel format(IdentityCardEntity identityCardEntity) {
        var identityCardModel = new IdentityCardModel();
        BeanUtils.copyProperties(identityCardEntity, identityCardModel);
        identityCardModel.setUser(new UserModel().setId(identityCardEntity.getUser().getId()));
        identityCardModel.setGovernanceRegion(this.organizeFormatter.format(identityCardEntity.getOrganize()));
        var topOrganize = this.organizeService.getTopOrganize(identityCardEntity.getOrganize().getId());
        identityCardModel.setTopOrganize(topOrganize);
        return identityCardModel;
    }
}
