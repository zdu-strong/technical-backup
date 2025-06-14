package com.john.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.john.project.entity.OrganizeEntity;
import com.john.project.entity.OrganizeRelationEntity;
import com.john.project.model.SuperAdminOrganizeQueryPaginationModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.john.project.common.baseService.BaseService;
import com.john.project.model.OrganizeModel;
import com.john.project.model.PaginationModel;

@Service
public class OrganizeService extends BaseService {

    public OrganizeModel create(OrganizeModel organizeModel) {
        var parentOrganize = this.getParentOrganize(organizeModel);
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(newId());
        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setIsDeleted(false);
        organizeEntity
                .setDeletionCode(StringUtils.EMPTY);
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setParent(parentOrganize);
        organizeEntity.setIsCompany(organizeEntity.getParent() == null);
        this.persist(organizeEntity);

        return this.organizeFormatter.format(organizeEntity);
    }

    public void update(OrganizeModel organizeModel) {
        var id = organizeModel.getId();
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    public void delete(String id) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setIsDeleted(true);
        organizeEntity.setDeletionCode(uuidUtil.v4());
        this.merge(organizeEntity);
    }

    @Transactional(readOnly = true)
    public OrganizeModel getById(String id) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();

        return this.organizeFormatter.format(organizeEntity);
    }

    @Transactional(readOnly = true)
    public PaginationModel<OrganizeModel> searchByName(Long pageNum, Long pageSize, String name, String organizeId) {
        var stream = this.streamAll(OrganizeRelationEntity.class)
                .where(s -> s.getAncestor().getId().equals(organizeId))
                .where(s -> s.getDescendant().getName().contains(name))
                .select(s -> s.getDescendant());
        return new PaginationModel<>(pageNum, pageSize, stream, this.organizeFormatter::format);
    }

    @Transactional(readOnly = true)
    public PaginationModel<OrganizeModel> searchOrganizeForSuperAdminByPagination(SuperAdminOrganizeQueryPaginationModel query) {
        var stream = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getIsCompany())
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(query.getPageNum(), query.getPageSize(), stream, this.organizeFormatter::format);
    }

    public void move(String id, String parentId) {
        var parentOrganizeEntity = this
                .getParentOrganize(new OrganizeModel().setParent(new OrganizeModel().setId(parentId)));
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> !s.getIsCompany())
                .getOnlyValue();
        organizeEntity.setParent(parentOrganizeEntity);
        organizeEntity.setIsCompany(organizeEntity.getParent() == null);
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    @Transactional(readOnly = true)
    public boolean isChildOfOrganize(String id, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return false;
        }
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        var isChild = this.organizeFormatter.getAncestorIdList(organizeEntity).contains(parentId);
        return isChild;
    }

    @Transactional(readOnly = true)
    public List<String> getAccestorIdList(String id) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        return this.organizeFormatter.getAncestorIdList(organizeEntity);
    }

    @Transactional(readOnly = true)
    public List<String> getChildIdList(String id) {
        var childIdList = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getParent().getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .select(s -> s.getId())
                .toList();
        return childIdList;
    }

    @Transactional(readOnly = true)
    public PaginationModel<OrganizeModel> searchOrganizeByPagination(Long pageNum, Long pageSize) {
        var stream = this.streamAll(OrganizeEntity.class)
                .sortedBy(s -> s.getId())
                .sortedBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, this.organizeFormatter::format);
    }

    @Transactional(readOnly = true)
    public OrganizeModel getTopOrganize(String organizeId) {
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(organizeId))
                .getOnlyValue();
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeEntity.getIsCompany()) {
                break;
            }
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return this.organizeFormatter.format(organizeEntity);
    }

    @Transactional(readOnly = true)
    public void checkHasExistOfParentOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = Optional.ofNullable(organizeModel.getParent())
                .map(s -> s.getId())
                .filter(s -> StringUtils.isNotBlank(s))
                .orElse(null);
        if (StringUtils.isBlank(parentOrganizeId)) {
            return;
        }
        this.checkHasExistById(parentOrganizeId);
    }

    @Transactional(readOnly = true)
    public void checkCanBeMoveOfOrganize(String id, String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return;
        }
        if (id.equals(parentId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize cannot be moved");
        }
        if (this.isChildOfOrganize(parentId, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize cannot be moved");
        }
        var organizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .getOnlyValue();
        if (organizeEntity.getIsCompany()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organize cannot be moved");
        }
    }

    @Transactional(readOnly = true)
    public void checkHasExistById(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var hasExist = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .filter(s -> this.organizeFormatter.isActive(s))
                .findFirst()
                .isPresent();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    @Transactional(readOnly = true)
    public void checkCannotHasParentOrganizeById(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        var hasExist = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(id))
                .where(s -> s.getIsCompany())
                .exists();
        if (!hasExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot has parent organize");
        }
    }

    private OrganizeEntity getParentOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = Optional.ofNullable(organizeModel.getParent())
                .map(s -> s.getId())
                .filter(s -> StringUtils.isNotBlank(s))
                .orElse(null);
        if (StringUtils.isBlank(parentOrganizeId)) {
            return null;
        }

        var parentOrganizeEntity = this.streamAll(OrganizeEntity.class)
                .where(s -> s.getId().equals(parentOrganizeId))
                .getOnlyValue();
        return parentOrganizeEntity;
    }

}
